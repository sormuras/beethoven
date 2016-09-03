package com.github.sormuras.beethoven.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.sormuras.beethoven.Annotation;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class ArrayTypeTest {

  @Test
  void arrayType() {
    assertEquals("byte[]", ArrayType.array(byte.class, 1).list());
    assertEquals("byte[][][]", ArrayType.array(Type.type(byte.class), 3).list());
    assertEquals("byte[][][]", Type.type(byte[][][].class).list());
  }

  @Test
  void arrayTypeWithAnnotatedDimensions() {
    ArrayType.Dimension[] dimensions = {
      new ArrayType.Dimension(Collections.singletonList(Annotation.cast("A"))),
      new ArrayType.Dimension(Arrays.asList(Annotation.cast("B"), Annotation.cast("C"))),
      new ArrayType.Dimension(Collections.singletonList(Annotation.cast("D")))
    };
    ArrayType actual = ArrayType.array(Type.type(byte.class), Arrays.asList(dimensions));
    assertEquals("byte@A []@B @C []@D []", actual.list());
  }
}
