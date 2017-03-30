/*
 * Copyright 2017 Christian Stein
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package de.sormuras.beethoven.type;

import static de.sormuras.beethoven.Annotation.annotations;
import static de.sormuras.beethoven.Tests.assertListable;
import static de.sormuras.beethoven.type.PrimitiveType.primitive;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.sormuras.beethoven.U;
import de.sormuras.beethoven.V;
import de.sormuras.beethoven.type.PrimitiveType.Primitive;
import org.junit.jupiter.api.Test;

class PrimitiveTypeTests {

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
  void primitiveTypeBox() {
    assertListable("Boolean", Primitive.BOOLEAN.build().box());
    assertListable("Byte", Primitive.BYTE.build().box());
    assertListable("Character", Primitive.CHAR.build().box());
    assertListable("Double", Primitive.DOUBLE.build().box());
    assertListable("Float", Primitive.FLOAT.build().box());
    assertListable("Integer", Primitive.INT.build().box());
    assertListable("Long", Primitive.LONG.build().box());
    assertListable("Short", Primitive.SHORT.build().box());
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
    assertEquals(Type.type(int.class), Type.withoutAnnotations(primitive(U.SINGLETON, int.class)));
  }

  @Test
  void primitiveTypeUseWithAnnotations() throws Exception {
    assertListable("@U int", primitive(U.SINGLETON, int.class));
    assertListable("@U int", Type.withAnnotations(Type.type(int.class), U.class));
    assertListable("@U int", Type.type(U.class.getDeclaredField("NUMBER").getAnnotatedType()));
    PrimitiveType uvi = primitive(annotations(U.class, V.class), int.class);
    assertListable("@U @V int", uvi);
    assertListable("@U @V Integer", uvi.box());
  }
}
