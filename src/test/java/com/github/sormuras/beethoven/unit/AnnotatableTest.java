package com.github.sormuras.beethoven.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import com.github.sormuras.beethoven.Annotated;
import com.github.sormuras.beethoven.Listable;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

class AnnotatableTest {

  @Test
  void annotatableExtendsListable() {
    assertTrue(Listable.class.isAssignableFrom(Annotatable.class));
  }

  void test(Supplier<? extends Annotatable> supplier) {
    testAnnotatable(supplier);
    testAnnotated(supplier);
  }

  void testAnnotatable(Supplier<? extends Annotatable> supplier) {
    Annotatable annotatable = supplier.get();
    annotatable.addAnnotation(Deprecated.class);
    assertTrue(annotatable.isAnnotated());
  }

  void testAnnotated(Supplier<? extends Annotated> supplier) {
    Annotated annotated = supplier.get();
    assertEquals(annotated, annotated);
    assertEquals(annotated, supplier.get());
    assertFalse(annotated.isAnnotated());
    assertNotNull(annotated.toString());
  }

  @TestFactory
  List<DynamicTest> declarations() {
    return List.of(
        dynamicTest("PackageDeclaration", () -> test(PackageDeclaration::new)),
        dynamicTest("ConstantDeclaration", () -> test(ConstantDeclaration::new)),
        dynamicTest("AnnotationElement", () -> test(AnnotationElement::new)),
        dynamicTest("AnnotationDeclaration", () -> test(AnnotationDeclaration::new)));
  }
}
