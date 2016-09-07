package com.github.sormuras.beethoven.type;

import static com.github.sormuras.beethoven.Tests.assertListable;
import static com.github.sormuras.beethoven.type.PrimitiveType.primitive;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.sormuras.beethoven.Annotation;
import com.github.sormuras.beethoven.U;
import com.github.sormuras.beethoven.V;
import com.github.sormuras.beethoven.type.PrimitiveType.Primitive;
import org.junit.jupiter.api.Test;

class PrimitiveTypeTest {

  @Test
  void primitiveType() {
    assertListable("boolean", primitive(boolean.class));
    assertListable("byte", primitive(byte.class));
    assertListable("char", primitive(char.class));
    assertListable("double", primitive(double.class));
    assertListable("float", primitive(float.class));
    assertListable("int", primitive(int.class));
    assertListable("long", primitive(long.class));
    assertListable("short", primitive(short.class));
  }

  @Test
  void primitiveTypeBuilder() {
    assertListable("boolean", Primitive.BOOLEAN.build());
    assertListable("byte", Primitive.BYTE.build());
    assertListable("char", Primitive.CHAR.build());
    assertListable("double", Primitive.DOUBLE.build());
    assertListable("float", Primitive.FLOAT.build());
    assertListable("int", Primitive.INT.build());
    assertListable("long", Primitive.LONG.build());
    assertListable("short", Primitive.SHORT.build());
  }

  @Test
  void primitiveTypeFailsForNonPrimitiveTypes() {
    assertThrows(AssertionError.class, () -> primitive(void.class));
    assertThrows(AssertionError.class, () -> primitive(Byte.class));
  }

  @Test
  void primitiveTypeEqualsAndHashcode() {
    assertEquals(primitive(boolean.class), Type.type(boolean.class));
    assertEquals(primitive(byte.class), Type.type(byte.class));
    assertEquals(primitive(char.class), Type.type(char.class));
    assertEquals(primitive(double.class), Type.type(double.class));
    assertEquals(primitive(float.class), Type.type(float.class));
    assertEquals(primitive(int.class), Type.type(int.class));
    assertEquals(primitive(long.class), Type.type(long.class));
    assertEquals(primitive(short.class), Type.type(short.class));
    assertNotEquals(primitive(byte.class), Type.type(char.class));
    assertNotEquals(Type.type(int.class), primitive(U.SINGLETON, int.class));
    assertEquals(Type.type(int.class), Type.annotationless(primitive(U.SINGLETON, int.class)));
  }

  @Test
  void primitiveTypeUseWithAnnotations() throws Exception {
    assertListable("@U int", primitive(U.SINGLETON, int.class));
    assertListable("@U int", Type.annotated(Type.type(int.class), U.class));
    assertListable("@U int", Type.type(U.class.getDeclaredField("NUMBER").getAnnotatedType()));
    assertListable("@U @V int", primitive(Annotation.annotations(U.class, V.class), int.class));
  }
}
