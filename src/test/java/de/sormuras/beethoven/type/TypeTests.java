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

import static de.sormuras.beethoven.Style.SIMPLE;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.sormuras.beethoven.All;
import de.sormuras.beethoven.Annotation;
import de.sormuras.beethoven.Compilation;
import de.sormuras.beethoven.Counter;
import de.sormuras.beethoven.Listing;
import de.sormuras.beethoven.Tests;
import de.sormuras.beethoven.U;
import de.sormuras.beethoven.unit.Annotatable;
import de.sormuras.beethoven.unit.Block;
import de.sormuras.beethoven.unit.ClassDeclaration;
import de.sormuras.beethoven.unit.CompilationUnit;
import de.sormuras.beethoven.unit.MethodDeclaration;
import de.sormuras.beethoven.unit.NormalClassDeclaration;
import de.sormuras.beethoven.unit.TypeParameter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedType;
import java.net.URI;
import java.util.List;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.tools.JavaFileObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TypeTests<T> {

  class W<X> {

    class Y<Z extends X> {
      int i = a;
    }
  }

  @U W<Number>.@U Y<Integer> w = null;

  int a = 4;

  @U int b = 5;

  int c @U [] @U [] @U [] = {};

  List<String> @U [] @U [] d = null;

  @U List<@U String> los = List.of();

  List<@U T> lot = List.of();

  List<@U ?> low = List.of();

  List<@U ? extends T> lowe = List.of();

  List<@U ? super T> lows = List.of();

  private String asAnnotatedType(String fieldName) throws Exception {
    AnnotatedType annotatedType = getClass().getDeclaredField(fieldName).getAnnotatedType();
    Listing listing = new Listing(SIMPLE);
    return listing.add(Type.type(annotatedType)).toString();
  }

  private String asGenericType(String fieldName) throws Exception {
    java.lang.reflect.Type type = getClass().getDeclaredField(fieldName).getGenericType();
    Listing listing = new Listing(SIMPLE);
    return listing.add(Type.type(type)).toString();
  }

  @Test
  void voidType() {
    assertEquals("void", Type.type(void.class).list());
    assertTrue(VoidType.INSTANCE.isVoid());
  }

  @Test
  void wildcard() {
    assertEquals(
        "?",
        Type.type(
                Tests.proxy(
                    java.lang.reflect.WildcardType.class,
                    (p, m, a) -> {
                      if (m.getName().equals("getLowerBounds")) {
                        return new java.lang.reflect.Type[0];
                      }
                      if (m.getName().equals("getUpperBounds")) {
                        return new java.lang.reflect.Type[0];
                      }
                      return null;
                    }))
            .list());
  }

  @Test
  void reflectFieldTypeAsAnnotatedType() throws Exception {
    assertEquals("int", asAnnotatedType("a"));
    assertEquals("@U int", asAnnotatedType("b"));
    assertEquals("int@U []@U []@U []", asAnnotatedType("c"));
    assertEquals("List<String>@U []@U []", asAnnotatedType("d"));
    assertEquals("@U List<@U String>", asAnnotatedType("los"));
    assertEquals("List<@U T>", asAnnotatedType("lot"));
    assertEquals("List<@U ?>", asAnnotatedType("low"));
    assertEquals("List<@U ? extends T>", asAnnotatedType("lowe"));
    assertEquals("List<@U ? super T>", asAnnotatedType("lows"));
    assertEquals("TypeTests<T>.@U W<Number>.@U Y<Integer>", asAnnotatedType("w"));
  }

  @Test
  void reflectFieldTypeAsGenericType() throws Exception {
    assertEquals("int", asGenericType("a"));
    assertEquals("int", asGenericType("b"));
    assertEquals("int[][][]", asGenericType("c"));
    assertEquals("List<String>[][]", asGenericType("d"));
    assertEquals("List<String>", asGenericType("los"));
    assertEquals("List<T>", asGenericType("lot"));
    assertEquals("List<?>", asGenericType("low"));
    assertEquals("List<? extends T>", asGenericType("lowe"));
    assertEquals("List<? super T>", asGenericType("lows"));
    assertEquals(getClass().getSimpleName() + "<T>.W<Number>.Y<Integer>", asGenericType("w"));
  }

  @Test
  void binary() {
    assertEquals(boolean.class.getName(), Type.type(boolean.class).binary());
    assertEquals(byte.class.getName(), Type.type(byte.class).binary());
    assertEquals(char.class.getName(), Type.type(char.class).binary());
    assertEquals(double.class.getName(), Type.type(double.class).binary());
    assertEquals(float.class.getName(), Type.type(float.class).binary());
    assertEquals(int.class.getName(), Type.type(int.class).binary());
    assertEquals(long.class.getName(), Type.type(long.class).binary());
    assertEquals(short.class.getName(), Type.type(short.class).binary());
    assertEquals(void.class.getName(), Type.type(void.class).binary());
    assertEquals(Object.class.getName(), Type.type(Object.class).binary());
    assertEquals(Thread.class.getName(), Type.type(Thread.class).binary());
    assertEquals(Thread.State.class.getName(), Type.type(Thread.State.class).binary());
    assertEquals(Object[].class.getName(), Type.type(Object[].class).binary());
    assertEquals(Object[][].class.getName(), Type.type(Object[][].class).binary());
    assertEquals(boolean[][][].class.getName(), Type.type(boolean[][][].class).binary());
    assertEquals(byte[][][].class.getName(), Type.type(byte[][][].class).binary());
    assertEquals(char[][][].class.getName(), Type.type(char[][][].class).binary());
    assertEquals(double[][][].class.getName(), Type.type(double[][][].class).binary());
    assertEquals(float[][][].class.getName(), Type.type(float[][][].class).binary());
    assertEquals(int[][][].class.getName(), Type.type(int[][][].class).binary());
    assertEquals(long[][][].class.getName(), Type.type(long[][][].class).binary());
    assertEquals(short[][][].class.getName(), Type.type(short[][][].class).binary());
    assertThrows(UnsupportedOperationException.class, () -> WildcardType.wildcard().binary());
  }

  @Test
  void classType() {
    assertEquals("boolean", Type.type(boolean.class).list());
    assertEquals("byte", Type.type(byte.class).list());
    assertEquals("char", Type.type(char.class).list());
    assertEquals("double", Type.type(double.class).list());
    assertEquals("float", Type.type(float.class).list());
    assertEquals("int", Type.type(int.class).list());
    assertEquals("long", Type.type(long.class).list());
    assertEquals("short", Type.type(short.class).list());
    Type annotatedInt = PrimitiveType.primitive(U.SINGLETON, int.class);
    assertEquals(U.USE + " int", annotatedInt.list());
  }

  public List<String> parametrizedFieldType;

  @Test
  void object() {
    assertEquals("Object", Type.type(Object.class).list());
  }

  @Test
  void type() throws Exception {
    assertEquals(
        "void", Type.type(TypeTests.class.getDeclaredMethod("type").getGenericReturnType()).list());
    assertEquals(
        "java.util.List<String>",
        Type.type(TypeTests.class.getDeclaredField("parametrizedFieldType").getGenericType())
            .list());
  }

  private <A extends Annotatable> A mark(A annotatable) {
    annotatable.addAnnotation(Counter.Mark.class);
    return annotatable;
  }

  private void primitives(Counter counter) {
    assertEquals(9, counter.types.size());
    assertEquals(Type.type(boolean.class), counter.types.get("field1"));
    assertEquals(Type.type(byte.class), counter.types.get("field2"));
    assertEquals(Type.type(char.class), counter.types.get("field3"));
    assertEquals(Type.type(double.class), counter.types.get("field4"));
    assertEquals(Type.type(float.class), counter.types.get("field5"));
    assertEquals(Type.type(int.class), counter.types.get("field6"));
    assertEquals(Type.type(long.class), counter.types.get("field7"));
    assertEquals(Type.type(short.class), counter.types.get("field8"));
    assertEquals(Type.type(void.class), counter.types.get("noop"));
  }

  @Test
  void primitivesFromCompilationUnit() throws Exception {
    CompilationUnit unit = CompilationUnit.of("test");
    ClassDeclaration type = unit.declareClass("PrimitiveFields");
    mark(type.declareField(boolean.class, "field1"));
    mark(type.declareField(byte.class, "field2"));
    mark(type.declareField(char.class, "field3"));
    mark(type.declareField(double.class, "field4"));
    mark(type.declareField(float.class, "field5"));
    mark(type.declareField(int.class, "field6"));
    mark(type.declareField(long.class, "field7"));
    mark(type.declareField(short.class, "field8"));
    MethodDeclaration noop = type.declareMethod(void.class, "noop");
    noop.setBody(new Block());
    noop.addAnnotation(Counter.Mark.class);

    Counter counter = new Counter();
    Compilation.compile(null, emptyList(), List.of(counter), List.of(unit.toJavaFileObject()));
    primitives(counter);
  }

  @Test
  void primitivesFromFile() {
    String charContent = Tests.load(TypeTests.class, "primitives");
    JavaFileObject source = Compilation.source(URI.create("test/Primitives.java"), charContent);
    Counter counter = new Counter();
    Compilation.compile(getClass().getClassLoader(), List.of(), List.of(counter), List.of(source));
    primitives(counter);
    // Tree tree = counter.trees.get("field1");
    // Type type = tree.accept(new Type.Trees.TypeTreeVisitor(), null);
    // System.out.print(tree + " -> " + type);
  }

  @Test
  void rootAnnotation() {
    CompilationUnit unit = CompilationUnit.of("test");

    Annotation annotation = Annotation.annotation(All.class);
    annotation.addObject("o", Annotation.annotation(Target.class, ElementType.TYPE));
    annotation.addObject("p", 4711);
    annotation.addObject("r", Double.class);
    annotation.addObject("r", Float.class);

    NormalClassDeclaration type = unit.declareClass("Root");
    type.addAnnotation(annotation);
    type.addTypeParameter(TypeParameter.of("X"));
    mark(type.declareField(TypeVariable.variable("X"), "i"));

    Counter counter = new Counter();
    Compilation.compile(null, emptyList(), List.of(counter), List.of(unit.toJavaFileObject()));
    assertEquals(1, counter.annotations.size());
    assertEquals(annotation.list(), counter.annotations.get(0).list());
  }

  //  @Test
  //  void unknownTypeFails() {
  //    AssertionError e =
  //        expectThrows(
  //            AssertionError.class,
  //            () -> JavaMirrors.of(Tests.proxy(PrimitiveType.class, (p, m, a) -> TypeKind.ERROR)));
  //    assertTrue(e.toString().contains("Unsupported primitive type"));
  //    e =
  //        expectThrows(
  //            AssertionError.class,
  //            () -> JavaMirrors.of(Tests.proxy(NoType.class, (p, m, a) -> TypeKind.ERROR)));
  //    assertTrue(e.toString().contains("Unsupported no type"));
  //  }

  @Test
  void visitor() {
    Type.Mirrors.TypeVisitor visitor = new Type.Mirrors.TypeVisitor();
    NoType voidType = Tests.proxy(NoType.class, (p, m, a) -> TypeKind.VOID);
    assertEquals(Type.type(void.class), visitor.visitNoType(voidType, null));
  }
}
