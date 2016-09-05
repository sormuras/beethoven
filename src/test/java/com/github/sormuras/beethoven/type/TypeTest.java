package com.github.sormuras.beethoven.type;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.sormuras.beethoven.Compilation;
import com.github.sormuras.beethoven.Counter;
import com.github.sormuras.beethoven.Importing;
import com.github.sormuras.beethoven.Listing;
import com.github.sormuras.beethoven.Tests;
import com.github.sormuras.beethoven.U;
import java.lang.reflect.AnnotatedType;
import java.net.URI;
import java.util.List;
import javax.tools.JavaFileObject;
import org.junit.jupiter.api.Test;

class TypeTest<T> {

  class W<X> {

    class Y<Z extends X> {
      int i = a;
    }
  }
  
  // "@U Y<..." crashes GJF 1.0 -- fixed in 1.1-SNAPSHOT ?
  @U W<Number>.@U Y<Integer> w = null;

  int a = 4;

  @U int b = 5;

  int c @U [] @U [] @U [] = {};

  List<String> @U [] @U [] d = null;

  @U List<@U String> los = emptyList();

  List<@U T> lot = emptyList();

  List<@U ?> low = emptyList();

  List<@U ? extends T> lowe = emptyList();

  List<@U ? super T> lows = emptyList();

  private String asAnnotatedType(String fieldName) throws Exception {
    AnnotatedType annotatedType = getClass().getDeclaredField(fieldName).getAnnotatedType();
    Listing listing = new Importing(Listing.NameMode.SIMPLE);
    return listing.add(Type.type(annotatedType)).toString();
  }

  private String asGenericType(String fieldName) throws Exception {
    java.lang.reflect.Type type = getClass().getDeclaredField(fieldName).getGenericType();
    Listing listing = new Importing(Listing.NameMode.SIMPLE);
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
    assertEquals("int", asAnnotatedType("a"));
    assertEquals("@U int", asAnnotatedType("b"));
    assertEquals("int@U []@U []@U []", asAnnotatedType("c"));
    assertEquals("List<String>@U []@U []", asAnnotatedType("d"));
    assertEquals("@U List<@U String>", asAnnotatedType("los"));
    assertEquals("List<@U T>", asAnnotatedType("lot"));
    assertEquals("List<@U ?>", asAnnotatedType("low"));
    assertEquals("List<@U ? extends T>", asAnnotatedType("lowe"));
    assertEquals("List<@U ? super T>", asAnnotatedType("lows"));
    assertEquals("TypeTest<T>.@U W<Number>.@U Y<Integer>", asAnnotatedType("w"));
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

  private void primitives(Counter counter) {
    assertEquals(9, counter.map.size());
    assertEquals(Type.type(boolean.class), counter.map.get("field1"));
    assertEquals(Type.type(byte.class), counter.map.get("field2"));
    assertEquals(Type.type(char.class), counter.map.get("field3"));
    assertEquals(Type.type(double.class), counter.map.get("field4"));
    assertEquals(Type.type(float.class), counter.map.get("field5"));
    assertEquals(Type.type(int.class), counter.map.get("field6"));
    assertEquals(Type.type(long.class), counter.map.get("field7"));
    assertEquals(Type.type(short.class), counter.map.get("field8"));
    assertEquals(Type.type(void.class), counter.map.get("noop"));
  }

  @Test
  void primitivesFromFile() {
    String charContent = Tests.load(TypeTest.class, "primitives");
    JavaFileObject source = Compilation.source(URI.create("test/Primitives.java"), charContent);
    Counter counter = new Counter();
    Compilation.compile(null, emptyList(), singletonList(counter), singletonList(source));
    primitives(counter);
  }
}
