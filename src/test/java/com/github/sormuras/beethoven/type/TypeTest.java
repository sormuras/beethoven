package com.github.sormuras.beethoven.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.sormuras.beethoven.Listing;
import com.github.sormuras.beethoven.Tests;
import com.github.sormuras.beethoven.U;
import java.lang.reflect.AnnotatedType;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class TypeTest<T> {

  class W<X> {

    class Y<Z extends X> {
      int i = a;
    }
  }

  W<Number>.Y<Integer> w = null;

  int a = 4;

  @U int b = 5;

  int c @U [] @U [] @U [] = {};

  List<String> @U [] @U [] d = null;

  @U List<@U String> los = Collections.emptyList();

  List<@U T> lot = Collections.emptyList();

  List<@U ?> low = Collections.emptyList();

  List<@U ? extends T> lowe = Collections.emptyList();

  List<@U ? super T> lows = Collections.emptyList();

  private String asAnno(String fieldName) throws Exception {
    AnnotatedType annotatedType = getClass().getDeclaredField(fieldName).getAnnotatedType();
    Listing listing = new Listing();
    return listing.add(Type.type(annotatedType)).toString();
  }

  private String asGenericType(String fieldName) throws Exception {
    java.lang.reflect.Type type = getClass().getDeclaredField(fieldName).getGenericType();
    Listing listing = new Listing();
    return listing.add(Type.type(type)).toString();
  }

  @Test
  void voidType() {
    assertEquals("void", Type.type(void.class).list());
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
    assertEquals("int", asAnno("a"));
    assertEquals(U.USE + " int", asAnno("b"));
    //    assertEquals("int" + U.USE + " []" + U.USE + " []" + U.USE + " []", asAnno("c"));
    //    assertEquals("java.util.List<java.lang.String>" + U.USE + " []" + U.USE + " []", asAnno("d"));
    //    assertEquals("java.util." + U.USE + " List<java.lang." + U.USE + " String>", asAnno("los"));
    //    assertEquals("java.util.List<" + U.USE + " T>", asAnno("lot"));
    //    assertEquals("java.util.List<" + U.USE + " ?>", asAnno("low"));
    //    assertEquals("java.util.List<" + U.USE + " ? extends T>", asAnno("lowe"));
    //    assertEquals("java.util.List<" + U.USE + " ? super T>", asAnno("lows"));
  }

  @Test
  void reflectFieldTypeAsGenericType() throws Exception {
    assertEquals("int", asGenericType("a"));
    assertEquals("int", asGenericType("b"));
    assertEquals("int[][][]", asGenericType("c"));
    assertEquals(
        getClass().getTypeName() + "<T>.W<java.lang.Number>.Y<java.lang.Integer>",
        asGenericType("w"));
    assertEquals("java.util.List<java.lang.String>[][]", asGenericType("d"));
    assertEquals("java.util.List<java.lang.String>", asGenericType("los"));
    assertEquals("java.util.List<T>", asGenericType("lot"));
    assertEquals("java.util.List<?>", asGenericType("low"));
    assertEquals("java.util.List<? extends T>", asGenericType("lowe"));
    assertEquals("java.util.List<? super T>", asGenericType("lows"));
  }

  @Test
  void className() {
    assertEquals(boolean.class.getName(), Type.type(boolean.class).toClassName());
    assertEquals(byte.class.getName(), Type.type(byte.class).toClassName());
    assertEquals(char.class.getName(), Type.type(char.class).toClassName());
    assertEquals(double.class.getName(), Type.type(double.class).toClassName());
    assertEquals(float.class.getName(), Type.type(float.class).toClassName());
    assertEquals(int.class.getName(), Type.type(int.class).toClassName());
    assertEquals(long.class.getName(), Type.type(long.class).toClassName());
    assertEquals(short.class.getName(), Type.type(short.class).toClassName());
    assertEquals(void.class.getName(), Type.type(void.class).toClassName());
    assertEquals(Object.class.getName(), Type.type(Object.class).toClassName());
    assertEquals(Thread.class.getName(), Type.type(Thread.class).toClassName());
    assertEquals(Thread.State.class.getName(), Type.type(Thread.State.class).toClassName());
    assertEquals(Object[].class.getName(), Type.type(Object[].class).toClassName());
    assertEquals(Object[][].class.getName(), Type.type(Object[][].class).toClassName());
    assertEquals(boolean[][][].class.getName(), Type.type(boolean[][][].class).toClassName());
    assertEquals(byte[][][].class.getName(), Type.type(byte[][][].class).toClassName());
    assertEquals(char[][][].class.getName(), Type.type(char[][][].class).toClassName());
    assertEquals(double[][][].class.getName(), Type.type(double[][][].class).toClassName());
    assertEquals(float[][][].class.getName(), Type.type(float[][][].class).toClassName());
    assertEquals(int[][][].class.getName(), Type.type(int[][][].class).toClassName());
    assertEquals(long[][][].class.getName(), Type.type(long[][][].class).toClassName());
    assertEquals(short[][][].class.getName(), Type.type(short[][][].class).toClassName());
    assertThrows(UnsupportedOperationException.class, () -> WildcardType.wild().toClassName());
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
    assertEquals("java.lang.Object", Type.type(Object.class).list());
  }

  @Test
  void type() throws Exception {
    assertEquals(
        "void", Type.type(TypeTest.class.getDeclaredMethod("type").getGenericReturnType()).list());
    try {
      assertEquals(
          "void",
          Type.type(TypeTest.class.getDeclaredField("parametrizedFieldType").getGenericType())
              .list());
    } catch (AssertionError e) {
      // expected
    }
  }
}
