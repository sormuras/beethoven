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

package de.sormuras.beethoven.type;

import de.sormuras.beethoven.Annotation;
import de.sormuras.beethoven.Listing;
import java.lang.annotation.ElementType;
import java.util.Collections;
import java.util.List;
import java.util.function.IntFunction;

/**
 * A type variable is an unqualified identifier used as a type in class, interface, method, and
 * constructor bodies.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.4">JLS 4.4</a>
 */
public class TypeVariable extends ReferenceType {

  public static TypeVariable variable(String identifier) {
    return variable(Collections.emptyList(), identifier);
  }

  public static TypeVariable variable(List<Annotation> annotations, String identifier) {
    if (identifier.isEmpty()) {
      throw new IllegalArgumentException("TypeVariable identifier must not be empty!");
    }
    return new TypeVariable(annotations, identifier);
  }

  private final String identifier;

  TypeVariable(List<Annotation> annotations, String identifier) {
    super(annotations);
    this.identifier = identifier;
  }

  @Override
  public TypeVariable annotated(IntFunction<List<Annotation>> annotationsSupplier) {
    return new TypeVariable(annotationsSupplier.apply(0), identifier);
  }

  @Override
  public Listing apply(Listing listing) {
    return applyAnnotations(listing).add(getIdentifier());
  }

  @Override
  public String binary() {
    throw new UnsupportedOperationException("Type variables have no binary class name.");
  }

  @Override
  public ElementType getAnnotationsTarget() {
    return ElementType.TYPE_PARAMETER;
  }

  public String getIdentifier() {
    return identifier;
  }
}
