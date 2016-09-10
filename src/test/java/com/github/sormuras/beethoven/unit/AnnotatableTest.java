package com.github.sormuras.beethoven.unit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.sormuras.beethoven.Listable;
import com.github.sormuras.beethoven.Tests;
import java.util.List;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

class AnnotatableTest {

  @Test
  void annotatableExtendsListable() {
    assertTrue(Listable.class.isAssignableFrom(Annotatable.class));
  }

  void annotatable(Annotatable annotatable) {
    assertFalse(annotatable.isAnnotated());
    annotatable.addAnnotation(Deprecated.class);
    assertTrue(annotatable.isAnnotated());
  }

  @TestFactory
  List<DynamicTest> declarations() {
    return Tests.tests(Annotatable.class, this::annotatable);
  }
}
