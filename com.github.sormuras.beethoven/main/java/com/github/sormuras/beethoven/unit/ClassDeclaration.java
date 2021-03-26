package com.github.sormuras.beethoven.unit;

import com.github.sormuras.beethoven.Listable;
import com.github.sormuras.beethoven.Listing;
import com.github.sormuras.beethoven.type.ClassType;
import com.github.sormuras.beethoven.type.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class declaration specifies a new named reference type.
 *
 * <p>There are two kinds of class declarations: normal class declarations and enum declarations.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.1">JLS 8.1</a>
 */
public abstract class ClassDeclaration extends TypeDeclaration {

  private List<FieldDeclaration> fields = new ArrayList<>();
  private List<Initializer> initializers = Collections.emptyList();
  private List<ClassType> interfaces = Collections.emptyList();
  private boolean local = false;

  public ClassDeclaration addInterface(Type interfaceType) {
    getInterfaces().add((ClassType) interfaceType);
    return this;
  }

  /** Applies class body. */
  public Listing applyClassBody(Listing listing) {
    listing.add(' ').add('{').newline();
    listing.indent(1);
    applyClassBodyElements(listing);
    listing.indent(-1);
    listing.add('}').newline();
    return listing;
  }

  /** Applies class body. */
  public Listing applyClassBodyElements(Listing listing) {
    if (!isDeclarationsEmpty()) {
      getDeclarations().forEach(listing::add);
    }
    listing.addAll(getFields(), Listable.IDENTITY);
    listing.addAll(getMethods());
    if (!isInitializersEmpty()) {
      getInitializers().forEach(listing::add);
    }
    return listing;
  }

  public MethodDeclaration declareConstructor() {
    return declareMethod(void.class, "<init>");
  }

  /** Declare new field. */
  public FieldDeclaration declareField(Class<?> type, String name) {
    return declareField(Type.type(type), name);
  }

  /** Declare new field. */
  public FieldDeclaration declareField(Type type, String name) {
    FieldDeclaration field = new FieldDeclaration();
    field.setCompilationUnit(getCompilationUnit());
    field.setEnclosingDeclaration(this);
    field.setType(type);
    field.setName(name);
    getFields().add(field);
    return field;
  }

  /** Declare new initializer block. */
  public Initializer declareInitializer(boolean staticInitializer) {
    Initializer initializer = new Initializer();
    initializer.setEnclosing(this);
    initializer.setStatic(staticInitializer);
    getInitializers().add(initializer);
    return initializer;
  }

  public List<FieldDeclaration> getFields() {
    return fields;
  }

  public List<Initializer> getInitializers() {
    if (initializers == Collections.EMPTY_LIST) {
      initializers = new ArrayList<>();
    }
    return initializers;
  }

  public List<ClassType> getInterfaces() {
    if (interfaces == Collections.EMPTY_LIST) {
      interfaces = new ArrayList<>();
    }
    return interfaces;
  }

  @Override
  public boolean isEmpty() {
    return super.isEmpty() && isInitializersEmpty() && getFields().isEmpty();
  }

  public boolean isInitializersEmpty() {
    return initializers.isEmpty();
  }

  public boolean isInterfacesEmpty() {
    return interfaces.isEmpty();
  }

  public boolean isLocal() {
    return local;
  }

  public void setLocal(boolean local) {
    this.local = local;
  }
}
