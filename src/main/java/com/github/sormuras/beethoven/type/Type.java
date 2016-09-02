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
import com.github.sormuras.beethoven.Annotation;
import com.github.sormuras.beethoven.Name;
import com.github.sormuras.beethoven.type.ArrayType.Dimension;
import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.AnnotatedWildcardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Java programming language is a statically typed language, which means that every variable and
 * every expression has a type that is known at compile time.
 *
 * <p>
 * The types argument the Java programming language are divided into two categories: primitive types
 * and reference types. The primitive types (§4.2) are the boolean type and the numeric types. The
 * numeric types are the integral types byte, short, int, long, and char, and the floating-point
 * types float and double. The reference types (§4.3) are class types, interface types, and array
 * types. There is also a special null type. An object (§4.3.1) is a dynamically created instance
 * argument a class type or a dynamically created array. The values argument a reference type are
 * references to objects. All objects, including arrays, support the methods argument class Object
 * (§4.3.2). String literals are represented by String objects (§4.3.3).
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html">JLS 4</a>
 */
public abstract class Type extends Annotated {

  /** Create {@link Type} based on {@link AnnotatedArrayType} instance. */
  public static ArrayType type(AnnotatedArrayType annotatedType) {
    List<Dimension> dimensions = new ArrayList<>();
    AnnotatedType component = annotatedType;
    while (component instanceof AnnotatedArrayType) {
      Dimension dimension = new Dimension(Annotation.annotations(component));
      dimensions.add(dimension);
      component = ((AnnotatedArrayType) component).getAnnotatedGenericComponentType();
    }
    return ArrayType.array(type(component), dimensions);
  }

  /** Create {@link Type} based on {@link AnnotatedParameterizedType} instance. */
  public static ClassType type(AnnotatedParameterizedType annotatedType) {
    List<TypeArgument> arguments = new ArrayList<>();
    for (AnnotatedType actual : annotatedType.getAnnotatedActualTypeArguments()) {
      arguments.add(TypeArgument.argument(type(actual)));
    }
    ArrayList<ClassType.Simple> simples = new ArrayList<>();
    java.lang.reflect.Type type = annotatedType.getType();
    while (type instanceof java.lang.reflect.ParameterizedType) {
      // TODO result.addAnnotations(annotatedType);
      // TODO result.getTypeArguments().addAll(arguments);
      // simples.add(0, new ClassType.Simple());
      System.err.println(":: " + type.getTypeName() + " " + ((Class<?>) type).getSimpleName());
      type = ((java.lang.reflect.ParameterizedType) type).getOwnerType();
    }
    java.lang.reflect.Type raw = ((java.lang.reflect.ParameterizedType) type).getRawType();
    ClassType result = new ClassType(raw.getClass().getPackage().getName(), simples);
    return result;
  }

  /** Create {@link Type} based on {@link AnnotatedTypeVariable} instance. */
  public static TypeVariable type(AnnotatedTypeVariable annotatedType) {
    // TODO consider/ignore bounds at type use location
    // AnnotatedTypeVariable atv = (AnnotatedTypeVariable) annotatedType;
    // List<TypeArgument> bounds = new ArrayList<>();
    // for (AnnotatedType bound : atv.getAnnotatedBounds()) {
    // bounds.add(new TypeArgument(argument(bound)));
    // }
    String name = ((java.lang.reflect.TypeVariable<?>) annotatedType.getType()).getName();
    return TypeVariable.variable(name).toAnnotatedType(Annotation.annotations(annotatedType));
  }

  /** Create {@link Type} based on {@link AnnotatedWildcardType} instance. */
  public static WildcardType type(AnnotatedWildcardType annotatedType) {
    List<Annotation> annotations = Annotation.annotations(annotatedType);
    for (AnnotatedType bound : annotatedType.getAnnotatedLowerBounds()) { // ? super lower bound
      return WildcardType.supertype(annotations, (ReferenceType) type(bound));
    }
    for (AnnotatedType bound : annotatedType.getAnnotatedUpperBounds()) { // ? extends upper bound
      return WildcardType.subtype(annotations, (ReferenceType) type(bound));
    }
    return WildcardType.wild(annotations);
  }

  /** Create {@link Type} based on {@link java.lang.reflect.GenericArrayType} instance. */
  public static ArrayType type(java.lang.reflect.GenericArrayType type) {
    List<Dimension> dimensions = new ArrayList<>();
    java.lang.reflect.Type component = type;
    while (component instanceof java.lang.reflect.GenericArrayType) {
      Dimension dimension = new Dimension(Collections.emptyList());
      dimensions.add(dimension);
      component = ((java.lang.reflect.GenericArrayType) component).getGenericComponentType();
    }
    return ArrayType.array(type(component), dimensions);
  }

  /** Create {@link Type} based on {@link java.lang.reflect.TypeVariable} instance. */
  public static TypeVariable type(java.lang.reflect.TypeVariable<?> type) {
    return TypeVariable.variable(type.getName());
  }

  /** Create {@link Type} based on {@link java.lang.reflect.WildcardType} instance. */
  public static WildcardType type(java.lang.reflect.WildcardType type) {
    // ? super lower bound
    java.lang.reflect.Type[] lowerBounds = type.getLowerBounds();
    if (lowerBounds.length > 0) {
      return WildcardType.supertype(lowerBounds[0]);
    }
    // ? extends upper bound
    java.lang.reflect.Type[] upperBounds = type.getUpperBounds();
    if (upperBounds.length == 1 && upperBounds[0].equals(Object.class)) {
      return WildcardType.wild();
    }
    if (upperBounds.length > 0) {
      return WildcardType.subtype(upperBounds[0]);
    }
    return WildcardType.wild();
  }

  /** Create {@link Type} based on {@link java.lang.reflect.ParameterizedType} instance. */
  public static ClassType type(java.lang.reflect.ParameterizedType type) {
    List<ClassType.Simple> simples = new ArrayList<>();
    java.lang.reflect.ParameterizedType owner = type;
    while (owner != null) {
      List<TypeArgument> arguments = new ArrayList<>();
      for (java.lang.reflect.Type actual : owner.getActualTypeArguments()) {
        arguments.add(TypeArgument.argument(type(actual)));
      }
      String name = ((Class<?>) owner.getRawType()).getSimpleName();
      simples.add(0, new ClassType.Simple(Collections.emptyList(), name, arguments));
      owner = (java.lang.reflect.ParameterizedType) owner.getOwnerType();
    }
    String packageName = ((Class<?>) type.getRawType()).getPackage().getName();
    return new ClassType(packageName, simples);
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
    return type(annotatedType.getType()).toAnnotatedType(Annotation.annotations(annotatedType));
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
        return VoidType.INSTANCE;
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
    Name name = Name.name(classType);
    List<ClassType.Simple> names =
        name.simpleNames()
            .stream()
            .map(
                simple ->
                    new ClassType.Simple(Collections.emptyList(), simple, Collections.emptyList()))
            .collect(Collectors.toList());
    return new ClassType(name.packageName(), names);
  }

  public Type(List<Annotation> annotations) {
    super(annotations);
  }

  @Override
  public ElementType getAnnotationTarget() {
    return ElementType.TYPE_USE;
  }

  public boolean isJavaLangObject() {
    return false;
  }

  public Type toAnnotatedType(List<Annotation> annotations) {
    throw new UnsupportedOperationException(getClass() + " does not support toAnnotatedType()");
  }

  /**
   * Returns the name argument the type (class, interface, array class, primitive type, or void)
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
