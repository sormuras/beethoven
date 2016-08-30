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

import com.github.sormuras.beethoven.Listing;

/**
 * A primitive type is predefined by the Java language and named by its reserved keyword.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.2">JLS 4.2</a>
 */
public abstract class PrimitiveType extends JavaType {

  public static class BooleanType extends PrimitiveType {
    @Override
    public Class<?> getType() {
      return boolean.class;
    }

    @Override
    public char toArrayClassNameIndicator() {
      return 'Z';
    }
  }

  public static class ByteType extends PrimitiveType {
    @Override
    public Class<?> getType() {
      return byte.class;
    }
  }

  public static class CharType extends PrimitiveType {
    @Override
    public Class<?> getType() {
      return char.class;
    }
  }

  public static class DoubleType extends PrimitiveType {
    @Override
    public Class<?> getType() {
      return double.class;
    }
  }

  public static class FloatType extends PrimitiveType {
    @Override
    public Class<?> getType() {
      return float.class;
    }
  }

  public static class IntType extends PrimitiveType {
    @Override
    public Class<?> getType() {
      return int.class;
    }
  }

  public static class LongType extends PrimitiveType {
    @Override
    public Class<?> getType() {
      return long.class;
    }

    @Override
    public char toArrayClassNameIndicator() {
      return 'J';
    }
  }

  public static class ShortType extends PrimitiveType {
    @Override
    public Class<?> getType() {
      return short.class;
    }
  }

  /** Creates new instance for passed primitive class <code>type</code>. */
  public static JavaType primitive(Class<?> type) {
    if (type == boolean.class) {
      return new BooleanType();
    }
    if (type == byte.class) {
      return new ByteType();
    }
    if (type == char.class) {
      return new CharType();
    }
    if (type == double.class) {
      return new DoubleType();
    }
    if (type == float.class) {
      return new FloatType();
    }
    if (type == int.class) {
      return new IntType();
    }
    if (type == long.class) {
      return new LongType();
    }
    if (type == short.class) {
      return new ShortType();
    }
    throw new AssertionError("expected primitive type, got " + type);
  }

  @Override
  public Listing apply(Listing listing) {
    return listing.add(toAnnotationsListable()).add(toClassName());
  }

  public abstract Class<?> getType();

  public char toArrayClassNameIndicator() {
    return getClass().getSimpleName().substring(0, 1).charAt(0);
  }

  @Override
  public String toClassName() {
    return getType().getTypeName();
  }
}
