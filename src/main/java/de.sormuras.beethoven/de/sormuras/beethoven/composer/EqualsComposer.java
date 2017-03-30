/*
 * Copyright 2017 Christian Stein
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package de.sormuras.beethoven.composer;

import de.sormuras.beethoven.Listing;
import de.sormuras.beethoven.unit.ClassDeclaration;
import de.sormuras.beethoven.unit.MethodDeclaration;
import java.util.function.Function;
import javax.lang.model.element.Modifier;

public class EqualsComposer implements Function<ClassDeclaration, MethodDeclaration> {

  private static final String OTHER = "other";

  @Override
  public MethodDeclaration apply(ClassDeclaration declaration) {
    MethodDeclaration method = declaration.declareMethod(boolean.class, "equals");
    method.addAnnotation(Override.class);
    method.setModifiers(Modifier.PUBLIC);
    method.declareParameter(Object.class, OTHER);
    method.addStatement(this::apply);
    return method;
  }

  public Listing apply(Listing listing) {
    listing.add("if (this == ").add(OTHER).add(") {").newline();
    listing.indent(1).add("return true;").newline().indent(-1);
    listing.add("}").newline();
    listing
        .add("if (")
        .add(OTHER)
        .add(" == null || getClass() != ")
        .add(OTHER)
        .add(".getClass()) {")
        .newline();
    listing.indent(1).add("return false;").newline().indent(-1);
    listing.add("}").newline();
    listing.add("return hashCode() == ").add(OTHER).add(".hashCode()");
    return listing;
  }
}
