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

import static com.github.sormuras.beethoven.type.Type.Reflection.reflect;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import com.github.sormuras.beethoven.Annotated;
import com.github.sormuras.beethoven.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.AnnotatedWildcardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

  interface Reflection {

    /** Create {@link Type} based on {@link AnnotatedArrayType} instance. */
    static ArrayType reflect(AnnotatedArrayType annotatedType) {
      List<ArrayType.Dimension> dimensions = new ArrayList<>();
      AnnotatedType component = annotatedType;
      while (component instanceof AnnotatedArrayType) {
        ArrayType.Dimension dimension = new ArrayType.Dimension(Annotation.annotations(component));
        dimensions.add(dimension);
        component = ((AnnotatedArrayType) component).getAnnotatedGenericComponentType();
      }
      return ArrayType.array(type(component), dimensions);
    }

    /** Create {@link Type} based on {@link AnnotatedParameterizedType} instance. */
    static ClassType reflect(AnnotatedParameterizedType annotatedType) {
      List<ClassType.Simple> simples = new ArrayList<>();
      while (true) {
        List<TypeArgument> arguments = new ArrayList<>();
        for (AnnotatedType actual : annotatedType.getAnnotatedActualTypeArguments()) {
          arguments.add(TypeArgument.argument(Type.type(actual)));
        }
        java.lang.reflect.Type underlying = annotatedType.getType();
        java.lang.reflect.ParameterizedType type = (java.lang.reflect.ParameterizedType) underlying;
        ClassType ownerClassType = reflect(type);
        List<Annotation> annotations = Annotation.annotations(annotatedType);
        String name = ownerClassType.getLastClassName().getName();
        simples.add(0, new ClassType.Simple(annotations, name, arguments));
        annotatedType = (AnnotatedParameterizedType) type.getOwnerType();
        if (annotatedType == null) {
          String packageName = ownerClassType.getPackageName();
          return new ClassType(packageName, simples);
        }
      }
    }

    /** Create {@link Type} based on {@link AnnotatedTypeVariable} instance. */
    static TypeVariable reflect(AnnotatedTypeVariable annotatedType) {
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
    static WildcardType reflect(AnnotatedWildcardType annotatedType) {
      List<Annotation> annotations = Annotation.annotations(annotatedType);
      // ? super lower bound
      AnnotatedType[] lowerBounds = annotatedType.getAnnotatedLowerBounds();
      if (lowerBounds.length > 0) {
        return WildcardType.supertype(annotations, (ReferenceType) type(lowerBounds[0]));
      }
      // ? extends upper bound
      AnnotatedType[] upperBounds = annotatedType.getAnnotatedUpperBounds();
      if (upperBounds.length == 1 && upperBounds[0].getType().equals(Object.class)) {
        WildcardType.wildcard(annotations);
      }
      if (upperBounds.length > 0) {
        return WildcardType.extend(annotations, (ReferenceType) type(upperBounds[0]));
      }
      return WildcardType.wildcard(annotations);
    }

    /** Create {@link Type} based on {@link java.lang.reflect.GenericArrayType} instance. */
    static ArrayType reflect(java.lang.reflect.GenericArrayType type) {
      List<ArrayType.Dimension> dimensions = new ArrayList<>();
      java.lang.reflect.Type component = type;
      while (component instanceof java.lang.reflect.GenericArrayType) {
        dimensions.add(new ArrayType.Dimension(Collections.emptyList()));
        component = ((java.lang.reflect.GenericArrayType) component).getGenericComponentType();
      }
      return ArrayType.array(Type.type(component), dimensions);
    }

    /** Create {@link Type} based on {@link java.lang.reflect.ParameterizedType} instance. */
    static ClassType reflect(java.lang.reflect.ParameterizedType type) {
      List<ClassType.Simple> simples = new ArrayList<>();
      java.lang.reflect.ParameterizedType owner = type;
      while (owner != null) {
        List<TypeArgument> arguments = new ArrayList<>();
        for (java.lang.reflect.Type actual : owner.getActualTypeArguments()) {
          arguments.add(TypeArgument.argument(Type.type(actual)));
        }
        String name = ((Class<?>) owner.getRawType()).getSimpleName();
        simples.add(0, new ClassType.Simple(Collections.emptyList(), name, arguments));
        owner = (java.lang.reflect.ParameterizedType) owner.getOwnerType();
      }
      String packageName = ((Class<?>) type.getRawType()).getPackage().getName();
      return new ClassType(packageName, simples);
    }

    /** Create {@link Type} based on {@link java.lang.reflect.TypeVariable} instance. */
    static TypeVariable reflect(java.lang.reflect.TypeVariable<?> type) {
      return TypeVariable.variable(type.getName());
    }

    /** Create {@link Type} based on {@link java.lang.reflect.WildcardType} instance. */
    static WildcardType reflect(java.lang.reflect.WildcardType type) {
      // ? super lower bound
      java.lang.reflect.Type[] lowerBounds = type.getLowerBounds();
      if (lowerBounds.length > 0) {
        return WildcardType.supertype(lowerBounds[0]);
      }
      // ? extends upper bound
      java.lang.reflect.Type[] upperBounds = type.getUpperBounds();
      if (upperBounds.length == 1 && upperBounds[0].equals(Object.class)) {
        return WildcardType.wildcard();
      }
      if (upperBounds.length > 0) {
        return WildcardType.extend(upperBounds[0]);
      }
      return WildcardType.wildcard();
    }
  }

  /** Create {@link Type} based on {@link AnnotatedType} instance. */
  public static Type type(AnnotatedType annotatedType) {
    if (annotatedType instanceof AnnotatedArrayType) {
      return reflect((AnnotatedArrayType) annotatedType);
    }
    if (annotatedType instanceof AnnotatedParameterizedType) {
      return reflect((AnnotatedParameterizedType) annotatedType);
    }
    if (annotatedType instanceof AnnotatedTypeVariable) {
      return reflect((AnnotatedTypeVariable) annotatedType);
    }
    if (annotatedType instanceof AnnotatedWildcardType) {
      return reflect((AnnotatedWildcardType) annotatedType);
    }
    // default case: use underlying type and create potentially annotated version of it.
    return type(annotatedType.getType()).toAnnotatedType(Annotation.annotations(annotatedType));
  }

  /** Create {@link Type} based on {@link Class} instance. */
  public static Type type(Class<?> classType) {
    // handle primitive types like: boolean, byte ... short, void included
    if (classType.isPrimitive()) {
      if (classType == void.class) {
        return VoidType.INSTANCE;
      }
      return PrimitiveType.primitive(classType);
    }
    // handle array type: count dimensions, reflect component type.
    if (classType.isArray()) {
      int dimensions = 1;
      while (true) {
        classType = classType.getComponentType();
        if (!classType.isArray()) {
          return ArrayType.array(type(classType), dimensions);
        }
        dimensions++;
      }
    }
    // default case: canonical class type like java.lang.Thread.State
    return ClassType.type(classType);
  }

  /** Create {@link Type} based on {@link java.lang.reflect.Type} instance. */
  public static Type type(java.lang.reflect.Type type) {
    if (type instanceof java.lang.reflect.GenericArrayType) {
      return reflect((java.lang.reflect.GenericArrayType) type);
    }
    if (type instanceof java.lang.reflect.ParameterizedType) {
      return reflect((java.lang.reflect.ParameterizedType) type);
    }
    if (type instanceof java.lang.reflect.TypeVariable<?>) {
      return reflect((java.lang.reflect.TypeVariable<?>) type);
    }
    if (type instanceof java.lang.reflect.WildcardType) {
      return reflect((java.lang.reflect.WildcardType) type);
    }
    return type((Class<?>) type);
  }

  public static List<Type> types(java.lang.reflect.Type... types) {
    return stream(types).map(Type::type).collect(toList());
  }

  Type(List<Annotation> annotations) {
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

  public Type toAnnotationFreeType() {
    return toAnnotatedType(Collections.emptyList());
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
