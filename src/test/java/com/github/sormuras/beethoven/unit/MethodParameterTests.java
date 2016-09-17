package com.github.sormuras.beethoven.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.expectThrows;

import com.github.sormuras.beethoven.Annotation;
import com.github.sormuras.beethoven.Name;
import com.github.sormuras.beethoven.type.TypeVariable;
import java.lang.annotation.ElementType;
import org.junit.jupiter.api.Test;

public class MethodParameterTests {

  @Test
  void simple() {
    assertEquals("int i", MethodParameter.of(int.class, "i").list());
    assertEquals("int... ia1", MethodParameter.of(int[].class, "ia1").setVariable(true).list());
    assertEquals("int[]... ia2", MethodParameter.of(int[][].class, "ia2").setVariable(true).list());
    MethodParameter parameter =
        new MethodParameter().setType(TypeVariable.variable("T")).setName("t").setFinal(true);
    parameter.addAnnotation(Annotation.annotation(Name.name("A")));
    assertEquals("final @A T t", parameter.list());
    assertEquals(ElementType.PARAMETER, new MethodParameter().getAnnotationsTarget());
    IllegalStateException expected =
        expectThrows(
            IllegalStateException.class,
            () -> MethodParameter.of(int.class, "i").setVariable(true));
    assertEquals(true, expected.toString().contains("array type expected"));
  }
}
