package de.sormuras.beethoven.type;

import static de.sormuras.beethoven.Tests.assertListable;
import static de.sormuras.beethoven.type.PrimitiveType.primitive;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.sormuras.beethoven.Annotation;
import de.sormuras.beethoven.Tests;
import de.sormuras.beethoven.U;
import de.sormuras.beethoven.V;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PrimitiveTypeTests {

  @Test
  void primitiveType() {
    Tests.assertListable("boolean", PrimitiveType.primitive(boolean.class));
    Tests.assertListable("byte", PrimitiveType.primitive(byte.class));
    Tests.assertListable("char", PrimitiveType.primitive(char.class));
    Tests.assertListable("double", PrimitiveType.primitive(double.class));
    Tests.assertListable("float", PrimitiveType.primitive(float.class));
    Tests.assertListable("int", PrimitiveType.primitive(int.class));
    Tests.assertListable("long", PrimitiveType.primitive(long.class));
    Tests.assertListable("short", PrimitiveType.primitive(short.class));
  }

  @Test
  void primitiveTypeBuilder() {
    Tests.assertListable("boolean", PrimitiveType.Primitive.BOOLEAN.build());
    Tests.assertListable("byte", PrimitiveType.Primitive.BYTE.build());
    Tests.assertListable("char", PrimitiveType.Primitive.CHAR.build());
    Tests.assertListable("double", PrimitiveType.Primitive.DOUBLE.build());
    Tests.assertListable("float", PrimitiveType.Primitive.FLOAT.build());
    Tests.assertListable("int", PrimitiveType.Primitive.INT.build());
    Tests.assertListable("long", PrimitiveType.Primitive.LONG.build());
    Tests.assertListable("short", PrimitiveType.Primitive.SHORT.build());
  }

  @Test
  void primitiveTypeFailsForNonPrimitiveTypes() {
    assertThrows(AssertionError.class, () -> PrimitiveType.primitive(void.class));
    assertThrows(AssertionError.class, () -> PrimitiveType.primitive(Byte.class));
  }

  @Test
  void primitiveTypeEqualsAndHashcode() {
    Assertions.assertEquals(PrimitiveType.primitive(boolean.class), Type.type(boolean.class));
    Assertions.assertEquals(PrimitiveType.primitive(byte.class), Type.type(byte.class));
    Assertions.assertEquals(PrimitiveType.primitive(char.class), Type.type(char.class));
    Assertions.assertEquals(PrimitiveType.primitive(double.class), Type.type(double.class));
    Assertions.assertEquals(PrimitiveType.primitive(float.class), Type.type(float.class));
    Assertions.assertEquals(PrimitiveType.primitive(int.class), Type.type(int.class));
    Assertions.assertEquals(PrimitiveType.primitive(long.class), Type.type(long.class));
    Assertions.assertEquals(PrimitiveType.primitive(short.class), Type.type(short.class));
    assertNotEquals(PrimitiveType.primitive(byte.class), Type.type(char.class));
    assertNotEquals(Type.type(int.class), PrimitiveType.primitive(U.SINGLETON, int.class));
    assertEquals(
        Type.type(int.class),
        Type.withoutAnnotations(PrimitiveType.primitive(U.SINGLETON, int.class)));
  }

  @Test
  void primitiveTypeUseWithAnnotations() throws Exception {
    Tests.assertListable("@U int", PrimitiveType.primitive(U.SINGLETON, int.class));
    Tests.assertListable("@U int", Type.withAnnotations(Type.type(int.class), U.class));
    Tests.assertListable(
        "@U int", Type.type(U.class.getDeclaredField("NUMBER").getAnnotatedType()));
    Tests.assertListable(
        "@U @V int", PrimitiveType.primitive(Annotation.annotations(U.class, V.class), int.class));
  }
}
