package de.sormuras.beethoven.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.sormuras.beethoven.Annotation;
import java.util.List;
import org.junit.jupiter.api.Test;

class TypeVariableTests {

  @Test
  void defaults() {
    assertEquals("T", TypeVariable.variable("T").getIdentifier());
  }

  @Test
  void annotated() {
    assertEquals(
        "@Deprecated T",
        TypeVariable.variable("T")
            .annotated(i -> List.of(Annotation.annotation(Deprecated.class)))
            .list());
  }

  @Test
  void constructorFailsWithEmptyName() {
    Exception e = assertThrows(Exception.class, () -> TypeVariable.variable(""));
    assertEquals("TypeVariable identifier must not be empty!", e.getMessage());
  }

  @Test
  void binaryIsUnsupported() {
    Exception e = assertThrows(Exception.class, () -> TypeVariable.variable("T").binary());
    assertEquals("Type variables have no binary class name.", e.getMessage());
  }
}
