package com.github.sormuras.beethoven.unit;

import com.github.sormuras.beethoven.Compilation;
import com.github.sormuras.beethoven.Listing;
import com.github.sormuras.beethoven.Name;
import com.github.sormuras.beethoven.Style;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import javax.lang.model.element.Modifier;
import javax.tools.JavaFileObject;

/**
 * Java compilation unit.
 *
 * <pre>
 * CompilationUnit:
 *   [PackageDeclaration] {ImportDeclaration} {TypeDeclaration}
 *   [ModuleDeclaration]
 * </pre>
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.3">JLS 7.3</a>
 */
public class CompilationUnit implements DeclarationContainer {

  public static CompilationUnit of(String packageName) {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName(packageName);
    return unit;
  }

  private List<TypeDeclaration> declarations = new ArrayList<>();
  private ImportDeclarations importDeclarations = new ImportDeclarations();
  private PackageDeclaration packageDeclaration = new PackageDeclaration();
  private Map<Name, Style> nameStyleMap = Collections.emptyMap();

  @Override
  public Listing apply(Listing listing) {
    listing.add(getPackageDeclaration());
    listing.add(getImportDeclarations());
    getDeclarations().forEach(declaration -> declaration.apply(listing));
    return listing;
  }

  /** Compile and return {@link Class} instance. */
  public Class<?> compile() throws ClassNotFoundException {
    ClassLoader loader = Compilation.compile(toJavaFileObject());
    TypeDeclaration declaration = getEponymousDeclaration().orElseThrow(IllegalStateException::new);
    return loader.loadClass(getPackageDeclaration().resolve(declaration.getName()));
  }

  /** Compile and create new instance. */
  public <T> T compile(Class<T> clazz, Object... args) {
    try {
      return clazz.cast(compile().getDeclaredConstructors()[0].newInstance(args));
    } catch (Exception exception) {
      throw new AssertionError("compiling or instantiating failed", exception);
    }
  }

  /** Compile and create new instance. */
  public <T> T compile(Class<T> clazz, Supplier<Class<?>[]> typesProvider, Object... args) {
    try {
      Class<? extends T> subClass = compile().asSubclass(clazz);
      return subClass.getConstructor(typesProvider.get()).newInstance(args);
    } catch (Exception exception) {
      throw new AssertionError("compiling or instantiating failed", exception);
    }
  }

  /** Compile and invoke "public static void main(String[] args)". */
  public void launch(String... args) {
    try {
      Object[] arguments = {args};
      compile().getMethod("main", String[].class).invoke(null, arguments);
    } catch (Exception cause) {
      throw new RuntimeException("launching " + this + " failed!", cause);
    }
  }

  @Override
  public <T extends TypeDeclaration> T declare(T declaration, String name, Modifier... modifiers) {
    DeclarationContainer.super.declare(declaration, name, modifiers);
    declaration.setEnclosingDeclaration(null);
    declaration.setCompilationUnit(this);
    return declaration;
  }

  @Override
  public List<TypeDeclaration> getDeclarations() {
    return declarations;
  }

  /**
   * @return file name defining type declaration
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.6-510">JLS
   *     7.6</a>
   */
  public Optional<TypeDeclaration> getEponymousDeclaration() {
    List<TypeDeclaration> types = getDeclarations();
    // trivial case: no type present
    if (types.isEmpty()) {
      return Optional.empty();
    }
    // trivial case: only one type present
    TypeDeclaration declaration = types.get(0);
    // if multiple types are present, find first public one
    if (types.size() > 1) {
      return types.stream().filter(TypeDeclaration::isPublic).findFirst();
    }
    return Optional.of(declaration);
  }

  public ImportDeclarations getImportDeclarations() {
    return importDeclarations;
  }

  public PackageDeclaration getPackageDeclaration() {
    return packageDeclaration;
  }

  public String getPackageName() {
    if (getPackageDeclaration().isUnnamed()) {
      return "";
    }
    return getPackageDeclaration().getName().packageName();
  }

  @Override
  public String list() {
    return list(new Listing(this::style));
  }

  @Override
  public String list(String lineSeparator) {
    return list(new Listing("  ", lineSeparator, this::style));
  }

  @Override
  public boolean isEmpty() {
    return getDeclarations().isEmpty()
        && getPackageDeclaration().isEmpty()
        && getImportDeclarations().isEmpty();
  }

  public void setNameStyleMap(Map<Name, Style> map) {
    this.nameStyleMap = Objects.requireNonNull(map, "name-to-style map is null");
  }

  public void setPackageName(String packageName) {
    List<String> names = Arrays.asList(Name.DOT.split(packageName));
    getPackageDeclaration().setName(Name.name(names.size(), names));
  }

  public Style style(Name name) {
    if (nameStyleMap != Collections.EMPTY_MAP) {
      return nameStyleMap.getOrDefault(name, Style.CANONICAL);
    }
    Style style = getImportDeclarations().style(name);
    if (style == Style.CANONICAL) {
      style = Style.auto(getPackageName(), name);
    }
    return style;
  }

  public URI toURI() {
    TypeDeclaration declaration = getEponymousDeclaration().orElseThrow(IllegalStateException::new);
    return getPackageDeclaration().toUri(declaration.getName() + ".java");
  }

  public JavaFileObject toJavaFileObject() {
    return Compilation.source(toURI(), list());
  }
}
