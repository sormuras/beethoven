package com.github.sormuras.beethoven.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.sormuras.beethoven.Listable;
import com.github.sormuras.beethoven.Name;
import org.junit.jupiter.api.Test;

class VoidTypeTest {

  @Test
  void annotationTargetIsNull() {
    assertNull(new VoidType().getAnnotationTarget());
    assertEquals(Listable.NEWLINE, new VoidType().getAnnotationSeparator());
  }

  @Test
  void annotationsAreImmutable() {
    assertTrue(new VoidType().getAnnotations().isEmpty());
    assertThrows(
        UnsupportedOperationException.class, () -> new VoidType().addAnnotation(Name.name("Fail")));
  }

  @Test
  void equalsAndHashCode() {
    assertEquals("void", Type.type(void.class).list());
    assertEquals(new VoidType(), Type.type(void.class));
    assertEquals(new VoidType().hashCode(), Type.type(void.class).hashCode());
    assertFalse(new VoidType().equals(null));
    assertFalse(new VoidType().equals(new Object()));
    VoidType v = new VoidType();
    assertEquals(v, v);
  }
}
