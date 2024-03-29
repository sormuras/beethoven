package com.github.sormuras.beethoven;

import static java.util.Collections.singletonList;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntPredicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;

/**
 * Names are used to refer to entities declared in a program.
 *
 * <p>A declared entity is a package, class type (normal or enum), interface type (normal or
 * annotation type), member (class, interface, field, or method) of a reference type, type parameter
 * (of a class, interface, method or constructor), parameter (to a method, constructor, or exception
 * handler), or local variable.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-6.html">JLS 6</a>
 */
public class Name implements Listable {

  /** Compiled <code>"."</code> pattern used to split canonical package and type names. */
  public static final Pattern DOT = Pattern.compile("\\.");

  /**
   * Cast/convert any object to an instance of {@link Name}.
   *
   * @return {@link Name}
   */
  public static Name cast(Object any) {
    if (any == null) {
      return null;
    }
    if (any instanceof Name) {
      return (Name) any;
    }
    if (any instanceof Class) {
      return name((Class<?>) any);
    }
    if (any instanceof Enum) {
      return name((Enum<?>) any);
    }
    if (any instanceof Member) {
      return name((Member) any);
    }
    if (any instanceof String) {
      return name((String) any);
    }
    if (any instanceof String[]) {
      return name((String[]) any);
    }
    if (any instanceof Collection<?>) {
      Collection<?> collection = (Collection<?>) any;
      return name(collection.stream().map(Object::toString).collect(Collectors.toList()));
    }
    throw new IllegalArgumentException("Can't cast/convert " + any.getClass() + " to Name!");
  }

  /**
   * Create name instance for the given class instance.
   *
   * @return {@link Name}
   */
  public static Name name(Class<?> type) {
    if (type.isAnonymousClass()) {
      throw new IllegalArgumentException("Anonymous. No name. Compiler generated: " + type);
    }
    if (type.isLocalClass()) {
      return new Name(0, singletonList(type.getSimpleName()));
    }
    String[] packageNames = DOT.split(type.getName()); // java[.]lang[.]Thread$State
    String[] identifiers = DOT.split(type.getCanonicalName()); // java[.]lang[.]Thread[.]State
    return new Name(packageNames.length - 1, Arrays.asList(identifiers));
  }

  /**
   * Create new Name based on the class type and declared member name.
   *
   * @return {@link Name}
   */
  public static Name name(Class<?> declaringType, String declaredMemberName) {
    Name declaringName = name(declaringType);
    int packageLevel = declaringName.packageLevel;
    String canonical = declaringName.canonical + '.' + declaredMemberName;
    return name(packageLevel, canonical, true);
  }

  /**
   * Create new Name based on type element instance.
   *
   * @return {@link Name}
   */
  public static Name name(Element element) {
    List<String> simpleNames = new ArrayList<>();
    for (Element e = element; true; e = e.getEnclosingElement()) {
      if (e.getKind() == ElementKind.PACKAGE) {
        PackageElement casted = (PackageElement) e;
        if (casted.isUnnamed()) {
          return new Name(0, simpleNames);
        }
        String[] packageNames = DOT.split(casted.getQualifiedName().toString());
        simpleNames.addAll(0, Arrays.asList(packageNames));
        return new Name(packageNames.length, simpleNames);
      }
      simpleNames.add(0, e.getSimpleName().toString());
    }
  }

  /**
   * Create name instance for the given enum constant.
   *
   * @return {@link Name}
   */
  public static Name name(Enum<?> constant) {
    return name(constant.getDeclaringClass(), constant.name());
  }

  /**
   * Create name instance for the canonical name.
   *
   * <pre>
   * name(2, "abc.xyz.Alphabet")
   * </pre>
   *
   * @return {@link Name}
   */
  public static Name name(int packageLevel, String canonical, boolean isMemberReference) {
    return name(packageLevel, Arrays.asList(DOT.split(canonical)), isMemberReference);
  }

  /**
   * Create name instance for the identifiers.
   *
   * <p>The fully qualified class name {@code abc.xyz.Alphabet} can be created by:
   *
   * <pre>
   * name(2, List.of("abc", "xyz", "Alphabet"))
   * </pre>
   *
   * @return {@link Name}
   * @throws AssertionError if any identifier is not a syntactically valid qualified name.
   */
  public static Name name(int packageLevel, List<String> names) {
    return name(packageLevel, names, false);
  }

  public static Name name(int packageLevel, List<String> names, boolean isMemberReference) {
    assert packageLevel >= 0 : "Package level must not be < 0, but is " + packageLevel;
    assert packageLevel <= names.size() : "Package level " + packageLevel + " too high: " + names;
    assert names.stream().allMatch(Objects::nonNull) : "Null-name in " + names;
    assert names.stream().allMatch(SourceVersion::isName) : "Non-name in " + names;
    return new Name(packageLevel, names, isMemberReference);
  }

  /**
   * Create name instance for the identifiers by delegating to {@link #name(int, List, boolean)}.
   *
   * <p>The package level is determined by the first capital name of the list.
   */
  public static Name name(List<String> names) {
    int size = names.size();
    IntPredicate uppercase = index -> Character.isUpperCase(names.get(index).codePointAt(0));
    int packageLevel = IntStream.range(0, size).filter(uppercase).findFirst().orElse(size);
    boolean allUpper = names.get(size - 1).toUpperCase().equals(names.get(size - 1));
    return name(packageLevel, names, allUpper || !uppercase.test(size - 1));
  }

  /** Create new Name based on the member instance. */
  public static Name name(Member member) {
    Name declaringName = name(member.getDeclaringClass());
    List<String> simples = new ArrayList<>(Arrays.asList(DOT.split(declaringName.canonical())));
    simples.add(member.getName());
    return new Name(declaringName.packageLevel, simples, true);
  }

  /** Create name instance for the identifiers by delegating to {@link #name(List)}. */
  public static Name name(String... identifiers) {
    if (identifiers.length == 1) {
      identifiers = DOT.split(identifiers[0]);
    }
    return name(Arrays.asList(identifiers));
  }

  /** Create new Name based on the class type and declared member name. */
  public static Name reflect(Class<?> type, String declaredName) {
    try {
      Member field = type.getDeclaredField(declaredName);
      return name(field);
    } catch (Exception expected) {
      // fall-through
    }
    for (Member method : type.getDeclaredMethods()) {
      if (method.getName().equals(declaredName)) {
        return name(method);
      }
    }
    throw new AssertionError(String.format("Member '%s' of %s not found!", declaredName, type));
  }

  private final String canonical;
  private final String lastName;
  private final int packageLevel;
  private final String packageName;
  private final String simpleNames;
  private final int size;
  private final String topLevelName;
  private final boolean isMemberReference;

  public Name(int packageLevel, List<String> identifiers) {
    this(packageLevel, identifiers, false);
  }

  Name(int packageLevel, List<String> identifiers, boolean isMemberReference) {
    this.packageLevel = packageLevel;
    this.size = identifiers.size();
    if (size == 0) throw new AssertionError("Identifiers must not be empty");
    if (packageLevel < 0)
      throw new AssertionError("Package level must not be negative: " + packageLevel);
    if (packageLevel > size)
      throw new AssertionError("Package level " + packageLevel + " too high: " + identifiers);
    this.canonical = String.join(".", identifiers);
    this.packageName = String.join(".", identifiers.subList(0, packageLevel));
    this.simpleNames = String.join(".", identifiers.subList(packageLevel, size));
    this.lastName = identifiers.get(size - 1);
    this.topLevelName = packageLevel < size ? identifiers.get(packageLevel) : null;
    this.isMemberReference = isMemberReference;
  }

  /** Add name respecting name mode styling result. */
  public Listing apply(Listing listing) {
    listing.getCollectedNames().add(this);
    Style style = listing.getStyling().apply(this);
    if (style == Style.LAST) {
      return listing.add(lastName());
    }
    if (style == Style.SIMPLE) {
      return listing.add(simpleNames());
    }
    if (style != Style.CANONICAL) throw new AssertionError("Unknown style: " + style);
    return listing.add(canonical());
  }

  public String canonical() {
    return canonical;
  }

  @Override
  public String comparisonKey() {
    return canonical;
  }

  /** Create new enclosing {@link Name} instance based on this identifiers. */
  public Name enclosing() {
    if (!isEnclosed()) {
      throw new IllegalStateException(String.format("Not enclosed: '%s'", this));
    }
    int shrunkByOne = size - 1;
    int newPackageLevel = Math.min(packageLevel, shrunkByOne);
    return name(newPackageLevel, canonical.substring(0, canonical.lastIndexOf('.')), false);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    return hashCode() == other.hashCode();
  }

  @Override
  public int hashCode() {
    return canonical.hashCode();
  }

  public boolean isEnclosed() {
    return size > 1;
  }

  public boolean isJavaLangObject() {
    return size == 3 && "java.lang.Object".equals(canonical);
  }

  public boolean isJavaLangPackage() {
    return packageLevel == 2 && "java.lang".equals(packageName);
  }

  public boolean isMemberReference() {
    return isMemberReference;
  }

  public String lastName() {
    return lastName;
  }

  public String packageName() {
    return packageName;
  }

  public String simpleNames() {
    return simpleNames;
  }

  public int size() {
    return size;
  }

  public Optional<String> topLevelName() {
    return Optional.ofNullable(topLevelName);
  }

  @Override
  public String toString() {
    return String.format("Name{%s/%s}", packageName, simpleNames);
  }
}
