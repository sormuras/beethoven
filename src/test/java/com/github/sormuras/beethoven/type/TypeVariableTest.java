package com.github.sormuras.beethoven.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.expectThrows;

import org.junit.jupiter.api.Test;

class TypeVariableTest {

  @Test
  void defaults() {
    assertEquals("T", TypeVariable.variable("T").getIdentifier());
  }

  @Test
  void constructorFailsWithEmptyName() {
    Exception e = expectThrows(Exception.class, () -> TypeVariable.variable(""));
    assertEquals("TypeVariable identifier must not be empty!", e.getMessage());
  }
}
