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
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import com.github.sormuras.beethoven.Annotated;
import com.github.sormuras.beethoven.Annotation;
import com.github.sormuras.beethoven.Name;
import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.AnnotatedWildcardType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;
import javax.lang.model.util.SimpleTypeVisitor8;

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

  /** Common {@link Type} factory collection parsing {@link javax.lang.model.type.TypeMirror}s. */
  interface Mirrors {

    /** Annotation value visitor adding members to the {@link Annotation} instance. */
    class AnnotationVisitor extends SimpleAnnotationValueVisitor8<Annotation, String> {
      private final Annotation annotation;

      AnnotationVisitor(Annotation annotation) {
        super(annotation);
        this.annotation = annotation;
      }

      @Override
      protected Annotation defaultAction(Object object, String name) {
        annotation.addObject(name, object);
        return annotation;
      }

      @Override
      public Annotation visitAnnotation(AnnotationMirror mirror, String name) {
        annotation.addMember(name, Mirrors.annotation(mirror));
        return annotation;
      }

      @Override
      public Annotation visitArray(List<? extends AnnotationValue> values, String name) {
        for (AnnotationValue value : values) {
          value.accept(this, name);
        }
        return annotation;
      }

      @Override
      public Annotation visitEnumConstant(VariableElement element, String name) {
        annotation.addMember(name, l -> l.add(Name.name(element)));
        return annotation;
      }

      @Override
      public Annotation visitType(TypeMirror mirror, String name) {
        annotation.addMember(name, l -> l.add(type(mirror)).add(".class"));
        return annotation;
      }
    }

    class TypeVisitor extends SimpleTypeVisitor8<Type, Void> {

      @Override
      public Type visitArray(javax.lang.model.type.ArrayType type, Void tag) {
        return Mirrors.mirror(type);
      }

      @Override
      public Type visitDeclared(javax.lang.model.type.DeclaredType type, Void tag) {
        return Mirrors.mirror(type);
      }

      @Override
      public Type visitError(javax.lang.model.type.ErrorType type, Void tag) {
        return Mirrors.mirror(type);
      }

      @Override
      public Type visitNoType(javax.lang.model.type.NoType type, Void tag) {
        return Mirrors.mirror(type);
      }

      @Override
      public Type visitPrimitive(javax.lang.model.type.PrimitiveType type, Void tag) {
        return Mirrors.mirror(type);
      }

      @Override
      public Type visitTypeVariable(javax.lang.model.type.TypeVariable type, Void tag) {
        return Mirrors.mirror(type);
      }

      @Override
      public Type visitWildcard(javax.lang.model.type.WildcardType type, Void tag) {
        return Mirrors.mirror(type);
      }
    }

    static List<Annotation> annotations(AnnotatedConstruct source) {
      List<? extends AnnotationMirror> mirrors = source.getAnnotationMirrors();
      if (mirrors.isEmpty()) {
        return emptyList();
      }
      return mirrors.stream().map(Mirrors::annotation).collect(toList());
    }

    /** Create {@link Annotation} based on {@link AnnotationMirror} instance. */
    static Annotation annotation(AnnotationMirror mirror) {
      Element element = mirror.getAnnotationType().asElement();
      Annotation annotation = Annotation.annotation(Name.name(element));
      Map<? extends ExecutableElement, ? extends AnnotationValue> values =
          mirror.getElementValues();
      if (values.isEmpty()) {
        return annotation;
      }
      AnnotationVisitor visitor = new AnnotationVisitor(annotation);
      for (ExecutableElement executableElement : values.keySet()) {
        String name = executableElement.getSimpleName().toString();
        AnnotationValue value = values.get(executableElement);
        value.accept(visitor, name);
      }
      return annotation;
    }

    /** Create {@link ArrayType} based on {@link javax.lang.model.type.ArrayType} instance. */
    static ArrayType mirror(javax.lang.model.type.ArrayType type) {
      List<ArrayType.Dimension> dimensions = new ArrayList<>();
      TypeMirror mirror = type;
      while (mirror instanceof javax.lang.model.type.ArrayType) {
        ArrayType.Dimension dimension = new ArrayType.Dimension(annotations(mirror));
        dimensions.add(dimension);
        mirror = ((javax.lang.model.type.ArrayType) mirror).getComponentType();
      }
      return ArrayType.array(type(mirror), dimensions);
    }

    /** Create {@link ClassType} based on {@link javax.lang.model.type.DeclaredType} instance. */
    static ClassType mirror(javax.lang.model.type.DeclaredType type) {
      // extract package name
      Element packageElement = type.asElement();
      while (packageElement.getKind() != ElementKind.PACKAGE) {
        packageElement = packageElement.getEnclosingElement();
      }
      PackageElement casted = (PackageElement) packageElement;
      String packageName = casted.getQualifiedName().toString();

      // extract simple name, annotations and type arguments
      List<ClassType.Simple> simples = new ArrayList<>();
      for (Element element = type.asElement();
          element.getKind().isClass() || element.getKind().isInterface();
          element = element.getEnclosingElement()) {

        String name = element.getSimpleName().toString();
        type = (javax.lang.model.type.DeclaredType) element.asType();
        List<Annotation> annotations = annotations(type);
        List<TypeArgument> arguments = emptyList();
        List<? extends TypeMirror> mirrors = type.getTypeArguments();
        if (!mirrors.isEmpty()) {
          arguments = mirrors.stream().map(ta -> TypeArgument.argument(type(ta))).collect(toList());
        }
        simples.add(0, new ClassType.Simple(annotations, name, arguments));
      }

      return new ClassType(packageName, simples);
    }

    /** Create {@link Type} based on {@link javax.lang.model.type.NoType} instance. */
    static Type mirror(javax.lang.model.type.NoType type) {
      if (type.getKind() == TypeKind.VOID) {
        return VoidType.INSTANCE;
      }
      throw new AssertionError("Unsupported no type: " + type.getKind());
    }

    /** Create {@link PrimitiveType} based on {@link javax.lang.model.type.PrimitiveType} type. */
    static PrimitiveType mirror(javax.lang.model.type.PrimitiveType mirror) {
      List<Annotation> annotations = annotations(mirror);
      TypeKind kind = mirror.getKind();
      if (kind == TypeKind.BOOLEAN) {
        return PrimitiveType.primitive(annotations, boolean.class);
      }
      if (kind == TypeKind.INT) {
        return PrimitiveType.primitive(annotations, int.class);
      }
      if (kind == TypeKind.LONG) {
        return PrimitiveType.primitive(annotations, long.class);
      }
      if (kind == TypeKind.CHAR) {
        return PrimitiveType.primitive(annotations, char.class);
      }
      if (kind == TypeKind.FLOAT) {
        return PrimitiveType.primitive(annotations, float.class);
      }
      if (kind == TypeKind.DOUBLE) {
        return PrimitiveType.primitive(annotations, double.class);
      }
      if (kind == TypeKind.BYTE) {
        return PrimitiveType.primitive(annotations, byte.class);
      }
      if (kind == TypeKind.SHORT) {
        return PrimitiveType.primitive(annotations, short.class);
      }
      throw new AssertionError("Unsupported primitive type: " + mirror.getKind());
    }

    /** Create {@link TypeVariable} based on {@link javax.lang.model.type.TypeVariable} mirror. */
    static TypeVariable mirror(javax.lang.model.type.TypeVariable mirror) {
      List<Annotation> annotations = annotations(mirror);
      return TypeVariable.variable(annotations, mirror.asElement().getSimpleName().toString());
    }

    /** Create {@link WildcardType} based on {@link javax.lang.model.type.WildcardType} instance. */
    static WildcardType mirror(javax.lang.model.type.WildcardType mirror) {
      List<Annotation> annotations = annotations(mirror);
      TypeMirror extendsBound = mirror.getExtendsBound();
      if (extendsBound != null) {
        return WildcardType.extend(annotations, (ReferenceType) type(extendsBound));
      }
      TypeMirror superBound = mirror.getSuperBound();
      if (superBound != null) {
        return WildcardType.supertype(annotations, (ReferenceType) type(superBound));
      }
      return WildcardType.wildcard(annotations);
    }
  }

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
      return TypeVariable.variable(Annotation.annotations(annotatedType), name);
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
        dimensions.add(new ArrayType.Dimension(emptyList()));
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
        simples.add(0, new ClassType.Simple(emptyList(), name, arguments));
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

  @SafeVarargs
  public static <T extends Type> T annotated(
      T type, Class<? extends java.lang.annotation.Annotation>... annotations) {
    return annotated(type, Annotation.annotations(annotations));
  }

  @SuppressWarnings("unchecked")
  public static <T extends Type> T annotated(T type, List<Annotation> annotations) {
    return (T) type.annotate(i -> i == type.getAnnotationIndex() ? annotations : emptyList());
  }

  public static <T extends Type> T annotationless(T type) {
    return annotated(type, emptyList());
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
    return annotated(type(annotatedType.getType()), Annotation.annotations(annotatedType));
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

  /** Create {@link Type} based on {@link javax.lang.model.type.TypeMirror} instance. */
  public static Type type(javax.lang.model.type.TypeMirror mirror) {
    Mirrors.TypeVisitor visitor = new Mirrors.TypeVisitor();
    return mirror.accept(visitor, null);
  }

  /** Create list of types based on variable array of {@link java.lang.reflect.Type}s. */
  public static List<Type> types(java.lang.reflect.Type... types) {
    return stream(types).map(Type::type).collect(toList());
  }

  /** Initialize this {@link Type} instance. */
  Type(List<Annotation> annotations) {
    super(annotations);
  }

  /** Create new copy of this instance attaching supplied index-based annotations. */
  public Type annotate(IntFunction<List<Annotation>> annotationsSupplier) {
    throw new UnsupportedOperationException(getClass() + " does not support annotate()");
  }

  /**
   * Return the binary name of this type as a String.
   *
   * @return (binary) class name
   * @see Class#getName()
   * @see Class#forName(String)
   */
  public String binary() {
    throw new UnsupportedOperationException(getClass() + " does not support binary()");
  }

  @Override
  public ElementType getAnnotationTarget() {
    return ElementType.TYPE_USE;
  }

  public int getAnnotationIndex() {
    return 0;
  }

  public boolean isJavaLangObject() {
    return false;
  }
}
