package com.github.sormuras.beethoven.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.sormuras.beethoven.Annotation;
import com.github.sormuras.beethoven.U;
import com.github.sormuras.beethoven.V;
import org.junit.jupiter.api.Test;

import java.util.Collections;

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
    assertEquals(PrimitiveType.primitive(boolean.class), Type.type(boolean.class));
    assertEquals(PrimitiveType.primitive(byte.class), Type.type(byte.class));
    assertEquals(PrimitiveType.primitive(char.class), Type.type(char.class));
    assertEquals(PrimitiveType.primitive(double.class), Type.type(double.class));
    assertEquals(PrimitiveType.primitive(float.class), Type.type(float.class));
    assertEquals(PrimitiveType.primitive(int.class), Type.type(int.class));
    assertEquals(PrimitiveType.primitive(long.class), Type.type(long.class));
    assertEquals(PrimitiveType.primitive(short.class), Type.type(short.class));
    assertNotEquals(PrimitiveType.primitive(byte.class), Type.type(char.class));
    Type intAnnotatedWithU = PrimitiveType.primitive(U.SINGLETON, int.class);
    assertNotEquals(Type.type(int.class), intAnnotatedWithU);
    assertEquals(Type.type(int.class), Type.annotationless(intAnnotatedWithU));
  }

  @Test
  void primitiveTypeUseWithAnnotation() throws Exception {
    //    Annotation u = Annotation.annotation(U.class);
    //    Type uint = Type.type(int.class);
    //    uint.addAnnotation(u);
    //    assertEquals(U.USE + " int", uint.list());

    Type uvint = PrimitiveType.primitive(Annotation.annotations(U.class, V.class), int.class);
    assertEquals(U.USE + " " + V.USE + " int", uvint.list());
    U reflected = U.class.getDeclaredField("NUMBER").getAnnotatedType().getAnnotation(U.class);
    Type uint2 =
        PrimitiveType.primitive(
            Collections.singletonList(Annotation.annotation(reflected)), int.class);
    assertEquals(U.USE + " int", uint2.list());
  }
}
