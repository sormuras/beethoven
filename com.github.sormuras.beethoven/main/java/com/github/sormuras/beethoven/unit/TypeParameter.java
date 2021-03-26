package com.github.sormuras.beethoven.unit;

import com.github.sormuras.beethoven.Listing;
import com.github.sormuras.beethoven.type.ClassType;
import com.github.sormuras.beethoven.type.Type;
import com.github.sormuras.beethoven.type.TypeVariable;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.lang.model.SourceVersion;

/**
 * A class or method is generic if it declares one or more type variables (§4.4).
 *
 * <p>These type variables are known as the type parameters of the class. The type parameter section
 * follows the class name and is delimited by angle brackets.
 *
 * <pre>
 * {TypeParameterModifier} Identifier [TypeBound]
 * </pre>
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.1.2">JLS
 *     8.1.2</a>
 */
public class TypeParameter extends Annotatable {

  public static TypeParameter of(String name, Type... bounds) {
    if (!SourceVersion.isIdentifier(name))
      throw new AssertionError("Expected legal identifier, but got: " + name);
    TypeParameter parameter = new TypeParameter();
    parameter.setName(name);
    parameter.addBounds(bounds);
    return parameter;
  }

  public static TypeParameter of(String name, String boundTypeVariableName) {
    TypeParameter parameter = of(name);
    parameter.setBoundTypeVariable(boundTypeVariableName);
    return parameter;
  }

  private List<ClassType> bounds = Collections.emptyList();
  private TypeVariable boundTypeVariable = null;
  private String name = "T";

  /** Add bound(s) to the list of bounds and clear bound type variable. */
  public void addBounds(ClassType... bounds) {
    this.boundTypeVariable = null;
    if (bounds.length == 0) {
      return;
    }
    Collections.addAll(getBounds(), bounds);
    getBounds().removeIf(ClassType::isJavaLangObject);
  }

  /** Add bound(s) to the list of bounds and clear bound type variable. */
  public void addBounds(Type... bounds) {
    this.boundTypeVariable = null;
    if (bounds.length == 0) {
      return;
    }
    addBounds(Arrays.stream(bounds).map(t -> (ClassType) t).toArray(ClassType[]::new));
  }

  @Override
  public Listing apply(Listing listing) {
    applyAnnotations(listing);
    listing.add(getName());
    if (boundTypeVariable == null && bounds.isEmpty()) {
      return listing;
    }
    listing.add(" extends ");
    if (boundTypeVariable != null) {
      listing.add(boundTypeVariable);
    } else {
      listing.addAll(bounds, " & ");
    }
    return listing;
  }

  @Override
  public ElementType getAnnotationsTarget() {
    return ElementType.TYPE_PARAMETER;
  }

  public List<ClassType> getBounds() {
    if (bounds == Collections.EMPTY_LIST) {
      bounds = new ArrayList<>();
    }
    return bounds;
  }

  public Optional<TypeVariable> getBoundTypeVariable() {
    return Optional.ofNullable(boundTypeVariable);
  }

  public String getName() {
    return name;
  }

  /** Set single type variable as bound and clear all other bounds. */
  public void setBoundTypeVariable(String typeVariableName) {
    setBoundTypeVariable(TypeVariable.variable(typeVariableName));
  }

  /** Set single type variable as bound and clear all other bounds. */
  public void setBoundTypeVariable(TypeVariable boundTypeVariable) {
    this.boundTypeVariable = boundTypeVariable;
    if (!bounds.isEmpty()) {
      getBounds().clear();
    }
  }

  public void setName(String name) {
    this.name = name;
  }
}
