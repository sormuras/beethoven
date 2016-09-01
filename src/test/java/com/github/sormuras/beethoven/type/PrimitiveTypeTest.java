package com.github.sormuras.beethoven.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.sormuras.beethoven.Annotation;
import com.github.sormuras.beethoven.U;
import com.github.sormuras.beethoven.V;
import org.junit.jupiter.api.Test;

class PrimitiveTypeTest {

  @Test
  void primitiveType() {
    assertEquals("boolean", PrimitiveType.primitive(boolean.class).list());
    assertEquals("byte", PrimitiveType.primitive(byte.class).list());
    assertEquals("char", PrimitiveType.primitive(char.class).list());
    assertEquals("double", PrimitiveType.primitive(double.class).list());
    assertEquals("float", PrimitiveType.primitive(float.class).list());
    assertEquals("int", PrimitiveType.primitive(int.class).list());
    assertEquals("long", PrimitiveType.primitive(long.class).list());
    assertEquals("short", PrimitiveType.primitive(short.class).list());
    assertThrows(AssertionError.class, () -> PrimitiveType.primitive(void.class));
    assertThrows(AssertionError.class, () -> PrimitiveType.primitive(Byte.class));
  }

  @Test
  void primitiveTypeEqualsAndHashcode() {
    assertEquals(PrimitiveType.primitive(boolean.class), JavaType.type(boolean.class));
    assertEquals(PrimitiveType.primitive(byte.class), JavaType.type(byte.class));
    assertEquals(PrimitiveType.primitive(char.class), JavaType.type(char.class));
    assertEquals(PrimitiveType.primitive(double.class), JavaType.type(double.class));
    assertEquals(PrimitiveType.primitive(float.class), JavaType.type(float.class));
    assertEquals(PrimitiveType.primitive(int.class), JavaType.type(int.class));
    assertEquals(PrimitiveType.primitive(long.class), JavaType.type(long.class));
    assertEquals(PrimitiveType.primitive(short.class), JavaType.type(short.class));
    assertNotEquals(PrimitiveType.primitive(byte.class), JavaType.type(char.class));
    JavaType intAnnotatedWithU = new PrimitiveType.IntType();
    intAnnotatedWithU.addAnnotation(U.class);
    assertNotEquals(intAnnotatedWithU, JavaType.type(int.class));
  }

  @Test
  void primitiveTypeUseWithAnnotation() throws Exception {
    Annotation u = Annotation.annotation(U.class);
    JavaType uint = JavaType.type(int.class);
    uint.addAnnotation(u);
    assertEquals(U.USE + " int", uint.list());
    JavaType uvint = PrimitiveType.primitive(int.class);
    uvint.addAnnotation(U.class);
    uvint.addAnnotation(V.class);
    assertEquals(U.USE + " " + V.USE + " int", uvint.list());
    U reflected = U.class.getDeclaredField("NUMBER").getAnnotatedType().getAnnotation(U.class);
    JavaType uint2 = JavaType.type(int.class);
    uint2.addAnnotation(reflected);
    assertEquals(U.USE + " int", uint2.list());
  }
}
