package com.github.sormuras.beethoven.type;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.expectThrows;

import org.junit.jupiter.api.Test;

class TypeArgumentTest {

  @Test
  void constructor() {
    TypeArgument argumentWithReference = TypeArgument.argument(Object.class);
    assertNotNull(argumentWithReference.getReference());
    assertNull(argumentWithReference.getWildcard());
    TypeArgument argumentWithWildcard = TypeArgument.argument(WildcardType.wild());
    assertNull(argumentWithWildcard.getReference());
    assertNotNull(argumentWithWildcard.getWildcard());
  }

  @Test
  void constructorFailsWithWrongJavaType() {
    AssertionError e =
        expectThrows(
            AssertionError.class, () -> TypeArgument.argument(PrimitiveType.type(int.class)));
    assertTrue(e.toString().contains("Neither reference nor wildcard"));
  }
}
