package com.github.sormuras.beethoven.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.sormuras.beethoven.JavaAnnotation;
import com.github.sormuras.beethoven.Listable;
import com.github.sormuras.beethoven.Name;

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
        UnsupportedOperationException.class,
        () -> new VoidType().addAnnotation(JavaAnnotation.annotation(Name.name("Fail"))));
  }

  @Test
  void equalsAndHashCode() {
    assertEquals("void", JavaType.type(void.class).list());
    assertEquals(new VoidType(), JavaType.type(void.class));
    assertEquals(new VoidType().hashCode(), JavaType.type(void.class).hashCode());
    assertFalse(new VoidType().equals(null));
    assertFalse(new VoidType().equals(new Object()));
    VoidType v = new VoidType();
    assertEquals(v, v);
  }
}
