package com.github.sormuras.beethoven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.sormuras.beethoven.type.PrimitiveType.Primitive;
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
    assertEquals(supplier.get(), supplier.get());
  }

  @TestFactory
  Stream<DynamicTest> primitives() {
    List<Primitive> primitives = List.of(Primitive.values());
    return DynamicTest.stream(primitives.iterator(), Primitive::name, p -> test(p::build));
  }
}
