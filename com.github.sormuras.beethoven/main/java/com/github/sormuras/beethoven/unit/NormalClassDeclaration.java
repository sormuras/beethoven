package com.github.sormuras.beethoven.unit;

import com.github.sormuras.beethoven.Listing;
import com.github.sormuras.beethoven.type.ClassType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Normal class declaration.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.1">JLS 8.1</a>
 */
public class NormalClassDeclaration extends ClassDeclaration {

  private ClassType superClass = null;
  private List<TypeParameter> typeParameters = Collections.emptyList();

  public ClassDeclaration addTypeParameter(TypeParameter typeParameter) {
    getTypeParameters().add(typeParameter);
    return this;
  }

  @Override
  public Listing apply(Listing listing) {
    if (!isLocal()) {
      listing.newline();
    }
    applyAnnotations(listing);
    applyModifiers(listing);
    listing.add("class").add(' ').add(getName());
    // [TypeParameters]
    if (!isTypeParametersEmpty()) {
      listing.add('<').addAll(getTypeParameters(), ", ").add('>');
    }
    // [Superclass]
    if (getSuperClass() != null) {
      listing.add(" extends ").add(getSuperClass());
    }
    // [Superinterfaces]
    if (!isInterfacesEmpty()) {
      listing.add(" implements ").addAll(getInterfaces(), ", ");
    }
    applyClassBody(listing);
    return listing;
  }

  public ClassType getSuperClass() {
    return superClass;
  }

  public List<TypeParameter> getTypeParameters() {
    if (typeParameters == Collections.EMPTY_LIST) {
      typeParameters = new ArrayList<>();
    }
    return typeParameters;
  }

  public boolean isTypeParametersEmpty() {
    return typeParameters.isEmpty();
  }

  public ClassDeclaration setSuperClass(ClassType superClass) {
    this.superClass = superClass;
    return this;
  }
}
