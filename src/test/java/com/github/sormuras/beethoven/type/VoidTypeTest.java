package com.github.sormuras.beethoven.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.sormuras.beethoven.Listable;
import org.junit.jupiter.api.Test;

class VoidTypeTest {

  @Test
  void annotationTargetIsNull() {
    assertNull(VoidType.INSTANCE.getAnnotationTarget());
    assertEquals(Listable.NEWLINE, VoidType.INSTANCE.getAnnotationSeparator());
  }

  @Test
  void annotationsAreImmutable() {
    assertTrue(VoidType.INSTANCE.getAnnotations().isEmpty());
    assertThrows(Exception.class, () -> VoidType.INSTANCE.getAnnotations().clear());
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
