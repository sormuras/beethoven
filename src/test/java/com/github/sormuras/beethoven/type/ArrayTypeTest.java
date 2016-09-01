package com.github.sormuras.beethoven.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.github.sormuras.beethoven.Annotation;
import com.github.sormuras.beethoven.Name;
import org.junit.jupiter.api.Test;

class ArrayTypeTest {

  @Test
  void arrayType() {
    assertEquals("byte[]", ArrayType.array(Type.type(byte.class), 1).list());
    assertEquals("byte[][][]", ArrayType.array(Type.type(byte.class), 3).list());
    assertEquals("byte[][][]", Type.type(byte[][][].class).list());
  }

  @Test
  void arrayTypeWithAnnotatedDimensions() {
    ArrayType actual = ArrayType.array(Type.type(byte.class), 3);
    actual.addAnnotations(0, Annotation.annotation(Name.name("test", "T")));
    actual.addAnnotations(
        1,
        Annotation.annotation(Name.name("test", "S")),
        Annotation.annotation(Name.name("test", "T")));
    actual.addAnnotations(2, Annotation.annotation(Name.name("test", "T")));
    assertEquals("byte@test.T []@test.S @test.T []@test.T []", actual.list());
    assertSame(actual.getAnnotations(), actual.getDimensions().get(0).getAnnotations());
  }

  @Test
  void mutable() {
    ArrayType array = new ArrayType();
    assertEquals(true, array.isEmpty());
    assertEquals(false, array.isAnnotated());
    assertEquals(true, array.getAnnotations().isEmpty());
    assertEquals(true, array.getDimensions().isEmpty());
    array.getDimensions().add(new ArrayType.Dimension());
    assertEquals(false, array.getDimensions().isEmpty());
    assertEquals(1, array.getDimensions().size());
    array.setComponentType(new PrimitiveType.IntType());
    assertEquals("int[]", array.list());
    array.addAnnotation(Name.name("test", "T"));
    assertEquals(1, array.getAnnotations().size());
    assertEquals("int@test.T []", array.list());
  }
}
