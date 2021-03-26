package com.github.sormuras.beethoven.unit;

import com.github.sormuras.beethoven.Annotation;
import com.github.sormuras.beethoven.Listable;
import com.github.sormuras.beethoven.Listing;
import com.github.sormuras.beethoven.type.ClassType;
import com.github.sormuras.beethoven.type.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * An interface declaration introduces a new reference type whose members are classes, interfaces,
 * constants, and methods. This type has no instance variables, and typically declares one or more
 * abstract methods; otherwise unrelated classes can implement the interface by providing
 * implementations for its abstract methods. Interfaces may not be directly instantiated.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.1">JLS 9.1</a>
 */
public class InterfaceDeclaration extends TypeDeclaration {

  private final List<ConstantDeclaration> constants = new ArrayList<>();
  private final List<ClassType> interfaces = new ArrayList<>();
  private final List<TypeParameter> typeParameters = new ArrayList<>();

  public void addInterface(Type interfaceType) {
    getInterfaces().add((ClassType) interfaceType);
  }

  public void addTypeParameter(TypeParameter parameter) {
    getTypeParameters().add(parameter);
  }

  @Override
  public Listing apply(Listing listing) {
    listing.newline();
    // {InterfaceModifier}
    applyAnnotations(listing);
    applyModifiers(listing);
    // interface Identifier
    listing.add("interface").add(' ').add(getName());
    // [TypeParameters]
    if (!getTypeParameters().isEmpty()) {
      listing.add('<').addAll(getTypeParameters(), ", ").add('>');
    }
    // [ExtendsInterfaces]
    if (!getInterfaces().isEmpty()) {
      listing.add(" extends ").addAll(getInterfaces(), ", ");
    }
    // InterfaceBody
    listing.add(' ').add('{').newline();
    listing.indent(1);
    if (!isDeclarationsEmpty()) {
      getDeclarations().forEach(listing::add);
    }
    getConstants().forEach(listing::add);
    getMethods().forEach(listing::add);
    listing.indent(-1).add('}').newline();
    return listing;
  }

  /** Add new constant field. */
  public ConstantDeclaration declareConstant(Type type, String name, Listable initializer) {
    ConstantDeclaration constants = new ConstantDeclaration();
    constants.setEnclosingDeclaration(this);
    constants.setName(name);
    constants.setType(type);
    constants.setInitializer(initializer);
    getConstants().add(constants);
    return constants;
  }

  /** Add new constant field. */
  public ConstantDeclaration declareConstant(Type type, String name, Object value) {
    return declareConstant(type, name, Annotation.value(value));
  }

  public List<ConstantDeclaration> getConstants() {
    return constants;
  }

  public List<ClassType> getInterfaces() {
    return interfaces;
  }

  public List<TypeParameter> getTypeParameters() {
    return typeParameters;
  }
}
