/*
 * Copyright (C) 2016 Christian Stein
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package de.sormuras.beethoven.unit;

import de.sormuras.beethoven.Listable;
import de.sormuras.beethoven.Listing;
import de.sormuras.beethoven.type.ArrayType;
import de.sormuras.beethoven.type.Type;
import java.lang.annotation.ElementType;
import java.util.Optional;

/**
 * The formal parameters of a method or constructor, if any, are specified by a list of
 * comma-separated parameter specifiers. Each parameter specifier consists of a type (optionally
 * preceded by the final modifier and/or one or more annotations) and an identifier (optionally
 * followed by brackets) that specifies the name of the parameter.
 *
 * @author Christian Stein
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.4.1">JLS
 *     8.4.1</a>
 */
public class MethodParameter extends Annotatable {

  public static MethodParameter of(Class<?> type, String name) {
    return of(Type.type(type), name);
  }

  public static MethodParameter of(Type type, String name) {
    return new MethodParameter().setType(type).setName(name);
  }

  public static MethodParameter of(MethodParameter source) {
    MethodParameter clone = new MethodParameter();
    clone.addAnnotations(source.getAnnotations());
    clone.setFinal(source.isFinal());
    clone.setType(source.getType());
    clone.setName(source.getName());
    clone.setVariable(source.isVariable());
    return clone;
  }

  private boolean finalModifier;
  private MethodDeclaration methodDeclaration;
  private String name;
  private Type type;
  private boolean variable;

  @Override
  public Listing apply(Listing listing) {
    if (isFinal()) {
      listing.add("final ");
    }
    applyAnnotations(listing);
    if (isVariable()) {
      ArrayType arrayType = (ArrayType) type; // throws ClassCastException
      int toIndex = arrayType.getDimensions().size() - 1;
      listing.add(arrayType.getComponentType());
      listing.add(arrayType.getDimensions().subList(0, toIndex), Listable.IDENTITY);
      listing.add("...");
    } else {
      listing.add(getType());
    }
    listing.add(' ').add(getName());
    return listing;
  }

  @Override
  public ElementType getAnnotationsTarget() {
    return ElementType.PARAMETER;
  }

  public Optional<MethodDeclaration> getMethodDeclaration() {
    return Optional.ofNullable(methodDeclaration);
  }

  public String getName() {
    return name;
  }

  public Type getType() {
    return type;
  }

  public boolean isFinal() {
    return finalModifier;
  }

  public boolean isVariable() {
    return variable;
  }

  public MethodParameter setFinal(boolean finalModifier) {
    this.finalModifier = finalModifier;
    return this;
  }

  public MethodParameter setMethodDeclaration(MethodDeclaration methodDeclaration) {
    this.methodDeclaration = methodDeclaration;
    return this;
  }

  public MethodParameter setName(String name) {
    this.name = name;
    return this;
  }

  public MethodParameter setType(Type type) {
    this.type = type;
    return this;
  }

  public MethodParameter setVariable(boolean variable) {
    if (variable && !(getType() instanceof ArrayType)) {
      throw new IllegalStateException("array type expected, got: " + getType());
    }
    this.variable = variable;
    return this;
  }
}
