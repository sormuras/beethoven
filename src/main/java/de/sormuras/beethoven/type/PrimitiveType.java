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
import java.util.List;
import java.util.function.IntFunction;

/**
 * A primitive type is predefined by the Java language and named by its reserved keyword.
 *
 * <p>see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.2">JLS 4.2</a>
 */
public class PrimitiveType extends Type {

  /** Builder-like enum of all well-known primitive types defined in the Java language. */
  public enum Primitive {
    BOOLEAN(boolean.class, Boolean.class, 'Z'),

    BYTE(byte.class, Byte.class, 'B'),

    CHAR(char.class, Character.class, 'C'),

    DOUBLE(double.class, Double.class, 'D'),

    FLOAT(float.class, Float.class, 'F'),

    INT(int.class, Integer.class, 'I'),

    LONG(long.class, Long.class, 'J'),

    SHORT(short.class, Short.class, 'S');

    public final char binary;
    public final Class<?> type;
    public final Class<?> wrapper;

    Primitive(Class<?> type, Class<?> wrapper, char binary) {
      this.type = type;
      this.wrapper = wrapper;
      this.binary = binary;
    }

    public PrimitiveType build() {
      return build(List.of());
    }

    public PrimitiveType build(List<Annotation> annotations) {
      return new PrimitiveType(annotations, this);
    }
  }

  /** Create new {@code PrimitiveType} instance for passed primitive class <code>type</code>. */
  public static PrimitiveType primitive(Class<?> type) {
    return primitive(List.of(), type);
  }

  /** Create new {@code PrimitiveType} instance for passed primitive class <code>type</code>. */
  public static PrimitiveType primitive(List<Annotation> annotations, Class<?> type) {
    if (!type.isPrimitive() || type.equals(Void.TYPE)) {
      throw new AssertionError("Expected primitive type, got " + type);
    }
    return primitive(annotations, type.getName().toUpperCase());
  }

  /** Create new {@code PrimitiveType} instance for passed primitive type name. */
  public static PrimitiveType primitive(List<Annotation> annotations, String name) {
    return Primitive.valueOf(name).build(annotations);
  }

  private final Primitive primitive;

  PrimitiveType(List<Annotation> annotations, Primitive primitive) {
    super(annotations);
    this.primitive = primitive;
  }

  @Override
  public PrimitiveType annotated(IntFunction<List<Annotation>> annotationsSupplier) {
    return new PrimitiveType(annotationsSupplier.apply(0), primitive);
  }

  @Override
  public String binary() {
    return getType().getTypeName();
  }

  @Override
  public Listing apply(Listing listing) {
    return applyAnnotations(listing).add(binary());
  }

  public Class<?> getType() {
    return primitive.type;
  }

  public char getTypeChar() {
    return primitive.binary;
  }

  public ClassType box() {
    return ClassType.type(primitive.wrapper).annotated(i -> getAnnotations());
  }
}
