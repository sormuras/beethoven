package test.integration.type;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.sormuras.beethoven.type.PrimitiveType;
import com.github.sormuras.beethoven.type.TypeArgument;
import com.github.sormuras.beethoven.type.WildcardType;
import org.junit.jupiter.api.Test;

class TypeArgumentTests {

  @Test
  void constructor() {
    TypeArgument argumentWithReference = TypeArgument.argument(Object.class);
    assertNotNull(argumentWithReference.getReference());
    assertNull(argumentWithReference.getWildcard());
    assertNotNull(argumentWithReference.toString());
    TypeArgument argumentWithWildcard = TypeArgument.argument(WildcardType.wildcard());
    assertNull(argumentWithWildcard.getReference());
    assertNotNull(argumentWithWildcard.getWildcard());
    assertNotNull(argumentWithWildcard.toString());
  }

  @Test
  void constructorFailsWithWrongJavaType() {
    AssertionError e =
        assertThrows(
            AssertionError.class, () -> TypeArgument.argument(PrimitiveType.type(int.class)));
    assertTrue(e.toString().contains("Neither reference nor wildcard"));
  }
}
