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

package com.github.sormuras.beethoven.type;

import com.github.sormuras.beethoven.Annotation;
import com.github.sormuras.beethoven.Listing;
import java.util.Collections;
import java.util.List;
import java.util.function.IntFunction;

/**
 * A primitive type is predefined by the Java language and named by its reserved keyword.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.2">JLS 4.2</a>
 */
public final class PrimitiveType extends Type {

  /** Create new {@link PrimitiveType} instance for passed primitive class <code>type</code>. */
  public static PrimitiveType primitive(Class<?> type) {
    return primitive(Collections.emptyList(), type);
  }

  /** Create new {@link PrimitiveType} instance for passed primitive class <code>type</code>. */
  public static PrimitiveType primitive(List<Annotation> annotations, Class<?> type) {
    if (type == boolean.class) {
      return new PrimitiveType(annotations, type, 'Z');
    }
    if (type == byte.class) {
      return new PrimitiveType(annotations, type, 'B');
    }
    if (type == char.class) {
      return new PrimitiveType(annotations, type, 'C');
    }
    if (type == double.class) {
      return new PrimitiveType(annotations, type, 'D');
    }
    if (type == float.class) {
      return new PrimitiveType(annotations, type, 'F');
    }
    if (type == int.class) {
      return new PrimitiveType(annotations, type, 'I');
    }
    if (type == long.class) {
      return new PrimitiveType(annotations, type, 'J');
    }
    if (type == short.class) {
      return new PrimitiveType(annotations, type, 'S');
    }
    throw new AssertionError("expected primitive type, got " + type);
  }

  private final Class<?> type;
  private final char typeChar;

  PrimitiveType(List<Annotation> annotations, Class<?> type, char typeChar) {
    super(annotations);
    this.type = type;
    this.typeChar = typeChar;
  }

  @Override
  public PrimitiveType annotated(IntFunction<List<Annotation>> annotationsSupplier) {
    return new PrimitiveType(annotationsSupplier.apply(0), type, typeChar);
  }

  @Override
  public String binary() {
    return getType().getTypeName();
  }

  @Override
  public Listing apply(Listing listing) {
    return listing.add(getAnnotationsListable()).add(binary());
  }

  public Class<?> getType() {
    return type;
  }

  public char getTypeChar() {
    return typeChar;
  }
}
