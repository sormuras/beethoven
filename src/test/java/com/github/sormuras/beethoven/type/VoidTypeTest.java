package com.github.sormuras.beethoven.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.sormuras.beethoven.Annotation;
import java.util.List;
import org.junit.jupiter.api.Test;

class VoidTypeTest {

  @Test
  void annotationTargetIsNull() {
    assertNull(VoidType.INSTANCE.getAnnotationsTarget());
  }

  @Test
  void annotationsAreImmutable() {
    List<Annotation> annotations = VoidType.INSTANCE.getAnnotations();
    assertTrue(annotations.isEmpty());
    assertThrows(Exception.class, () -> annotations.add(Annotation.annotation(Deprecated.class)));
  }

  @Test
  void equalsAndHashCode() {
    assertEquals("void", Type.type(void.class).list());
    assertEquals(VoidType.INSTANCE, Type.type(void.class));
    assertEquals(VoidType.INSTANCE.hashCode(), Type.type(void.class).hashCode());
    assertFalse(VoidType.INSTANCE.equals(null));
    assertFalse(VoidType.INSTANCE.equals(new Object()));
    assertEquals(VoidType.INSTANCE, VoidType.INSTANCE);
  }
}
