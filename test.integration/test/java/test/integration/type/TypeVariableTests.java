package test.integration.type;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.sormuras.beethoven.Annotation;
import com.github.sormuras.beethoven.type.TypeVariable;
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
            .annotated(i -> singletonList(Annotation.annotation(Deprecated.class)))
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
