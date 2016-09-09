package com.github.sormuras.beethoven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import com.github.sormuras.beethoven.type.ArrayType;
import com.github.sormuras.beethoven.type.ClassType;
import com.github.sormuras.beethoven.type.PrimitiveType.Primitive;
import com.github.sormuras.beethoven.type.TypeVariable;
import com.github.sormuras.beethoven.type.VoidType;
import com.github.sormuras.beethoven.type.WildcardType;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

class AnnotatedTest {

  @Test
  void annotatedExtendsListable() {
    assertTrue(Listable.class.isAssignableFrom(Annotated.class));
  }

  void test(Supplier<? extends Annotated> supplier) {
    Annotated annotated = supplier.get();
    assertEquals(annotated, annotated);
    assertEquals(annotated, supplier.get());
    assertFalse(annotated.isAnnotated());
    assertNotNull(annotated.toString());
  }

  @TestFactory
  Stream<DynamicTest> primitives() {
    List<Primitive> primitives = List.of(Primitive.values());
    return DynamicTest.stream(primitives.iterator(), Primitive::name, p -> test(p::build));
  }

  @TestFactory
  List<DynamicTest> types() {
    return List.of(
        dynamicTest("VoidType", () -> test(VoidType::instance)),
        dynamicTest("WildcardType", () -> test(WildcardType::wildcard)),
        dynamicTest("TypeVariable", () -> test(() -> TypeVariable.variable("T"))),
        dynamicTest("ArrayType.Dimension", () -> test(() -> ArrayType.dimensions(1).get(0))),
        dynamicTest("ClassType.Simple", () -> test(() -> ClassType.simple("S"))));
  }
}
