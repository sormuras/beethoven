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

import com.github.sormuras.beethoven.Annotated;
import com.github.sormuras.beethoven.Name;
import com.github.sormuras.beethoven.type.ArrayType.Dimension;
import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.AnnotatedWildcardType;
import java.util.ArrayList;
import java.util.List;

/**
 * The Java programming language is a statically typed language, which means that every variable and
 * every expression has a type that is known at compile time.
 *
 * <p>
 * The types of the Java programming language are divided into two categories: primitive types and
 * reference types. The primitive types (§4.2) are the boolean type and the numeric types. The
 * numeric types are the integral types byte, short, int, long, and char, and the floating-point
 * types float and double. The reference types (§4.3) are class types, interface types, and array
 * types. There is also a special null type. An object (§4.3.1) is a dynamically created instance of
 * a class type or a dynamically created array. The values of a reference type are references to
 * objects. All objects, including arrays, support the methods of class Object (§4.3.2). String
 * literals are represented by String objects (§4.3.3).
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html">JLS 4</a>
 */
public abstract class Type extends Annotated {

  /** Create {@link Type} based on {@link AnnotatedArrayType} instance. */
  public static ArrayType type(AnnotatedArrayType annotatedType) {
    List<Dimension> dimensions = new ArrayList<>();
    AnnotatedType component = annotatedType;
    while (component instanceof AnnotatedArrayType) {
      Dimension dimension = new Dimension();
      dimension.addAnnotations(component);
      dimensions.add(dimension);
      component = ((AnnotatedArrayType) component).getAnnotatedGenericComponentType();
    }
    return ArrayType.array(type(component), dimensions);
  }

  /** Create {@link Type} based on {@link AnnotatedParameterizedType} instance. */
  public static ClassType type(AnnotatedParameterizedType annotatedType) {
    List<TypeArgument> arguments = new ArrayList<>();
    for (AnnotatedType actual : annotatedType.getAnnotatedActualTypeArguments()) {
      arguments.add(TypeArgument.of(type(actual)));
    }
    java.lang.reflect.Type type = annotatedType.getType();
    java.lang.reflect.Type raw = ((java.lang.reflect.ParameterizedType) type).getRawType();
    ClassType result = (ClassType) type(raw);
    result.addAnnotations(annotatedType);
    result.getTypeArguments().addAll(arguments);
    return result;
  }

  /** Create {@link Type} based on {@link AnnotatedTypeVariable} instance. */
  public static TypeVariable type(AnnotatedTypeVariable annotatedType) {
    // TODO consider/ignore bounds at type use location
    // AnnotatedTypeVariable atv = (AnnotatedTypeVariable) annotatedType;
    // List<TypeArgument> bounds = new ArrayList<>();
    // for (AnnotatedType bound : atv.getAnnotatedBounds()) {
    // bounds.add(new TypeArgument(of(bound)));
    // }
    String name = ((java.lang.reflect.TypeVariable<?>) annotatedType.getType()).getName();
    TypeVariable result = TypeVariable.of(name);
    result.addAnnotations(annotatedType);
    return result;
  }

  /** Create {@link Type} based on {@link AnnotatedWildcardType} instance. */
  public static WildcardType type(AnnotatedWildcardType annotatedType) {
    WildcardType result = new WildcardType();
    for (AnnotatedType bound : annotatedType.getAnnotatedLowerBounds()) { // ? super lower bound
      result.setBoundSuper((ReferenceType) type(bound));
    }
    for (AnnotatedType bound : annotatedType.getAnnotatedUpperBounds()) { // ? extends upper bound
      result.setBoundExtends((ReferenceType) type(bound));
    }
    result.addAnnotations(annotatedType);
    return result;
  }

  /** Create {@link Type} based on {@link AnnotatedWildcardType} instance. */
  public static ArrayType type(java.lang.reflect.GenericArrayType type) {
    List<Dimension> dimensions = new ArrayList<>();
    java.lang.reflect.Type component = type;
    while (component instanceof java.lang.reflect.GenericArrayType) {
      Dimension dimension = new Dimension();
      dimensions.add(dimension);
      component = ((java.lang.reflect.GenericArrayType) component).getGenericComponentType();
    }
    return ArrayType.array(type(component), dimensions);
  }

  /** Create {@link Type} based on {@link java.lang.reflect.TypeVariable} instance. */
  public static TypeVariable type(java.lang.reflect.TypeVariable<?> type) {
    return TypeVariable.of(type.getName());
  }

  /** Create {@link Type} based on {@link java.lang.reflect.WildcardType} instance. */
  public static WildcardType type(java.lang.reflect.WildcardType type) {
    // ? super lower bound
    java.lang.reflect.Type[] lowerBounds = type.getLowerBounds();
    if (lowerBounds.length > 0) {
      return WildcardType.supertypeOf(lowerBounds[0]);
    }
    // ? extends upper bound
    java.lang.reflect.Type[] upperBounds = type.getUpperBounds();
    if (upperBounds.length == 1 && upperBounds[0].equals(Object.class)) {
      return new WildcardType();
    }
    if (upperBounds.length > 0) {
      return WildcardType.subtypeOf(upperBounds[0]);
    }
    return new WildcardType();
  }

  /** Create {@link Type} based on {@link java.lang.reflect.ParameterizedType} instance. */
  public static ClassType type(java.lang.reflect.ParameterizedType type) {
    List<TypeArgument> arguments = new ArrayList<>();
    for (java.lang.reflect.Type actual : type.getActualTypeArguments()) {
      arguments.add(TypeArgument.of(type(actual)));
    }
    ClassType result = (ClassType) type(type.getRawType());
    result.getTypeArguments().addAll(arguments);
    return result;
  }

  /** Create {@link Type} based on {@link AnnotatedType} instance. */
  public static Type type(AnnotatedType annotatedType) {
    if (annotatedType instanceof AnnotatedArrayType) {
      return type((AnnotatedArrayType) annotatedType);
    }
    if (annotatedType instanceof AnnotatedParameterizedType) {
      return type((AnnotatedParameterizedType) annotatedType);
    }
    if (annotatedType instanceof AnnotatedTypeVariable) {
      return type((AnnotatedTypeVariable) annotatedType);
    }
    if (annotatedType instanceof AnnotatedWildcardType) {
      return type((AnnotatedWildcardType) annotatedType);
    }
    // default case: use underlying raw type
    Type result = type(annotatedType.getType());
    result.addAnnotations(annotatedType);
    return result;
  }

  /**
   * Create {@link Type} based on {@link java.lang.reflect.Type} instance.
   *
   * @return potentially annotated and generic Type
   */
  public static Type type(java.lang.reflect.Type type) {
    if (type instanceof java.lang.reflect.GenericArrayType) {
      return type((java.lang.reflect.GenericArrayType) type);
    }
    if (type instanceof java.lang.reflect.ParameterizedType) {
      return type((java.lang.reflect.ParameterizedType) type);
    }
    if (type instanceof java.lang.reflect.TypeVariable<?>) {
      return type((java.lang.reflect.TypeVariable<?>) type);
    }
    if (type instanceof java.lang.reflect.WildcardType) {
      return type((java.lang.reflect.WildcardType) type);
    }
    Class<?> classType = (Class<?>) type;
    if (classType.isPrimitive()) {
      if (classType == void.class) {
        return new VoidType();
      }
      return PrimitiveType.primitive(classType);
    }
    if (classType.isArray()) {
      int dimensions = 1;
      while (true) {
        classType = classType.getComponentType();
        if (!classType.isArray()) {
          return ArrayType.array(classType, dimensions);
        }
        dimensions++;
      }
    }
    return ClassType.of(Name.name(classType));
  }

  @Override
  public ElementType getAnnotationTarget() {
    return ElementType.TYPE_USE;
  }

  public boolean isJavaLangObject() {
    return false;
  }

  /**
   * Returns the name of the type (class, interface, array class, primitive type, or void)
   * represented by this object, as a String.
   *
   * @see Class#getName()
   * @see Class#forName(String)
   * @return (binary) class name
   */
  public String toClassName() {
    throw new UnsupportedOperationException(getClass() + " does not support toClassName()");
  }
}
