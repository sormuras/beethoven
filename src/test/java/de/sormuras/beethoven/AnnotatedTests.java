package de.sormuras.beethoven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import de.sormuras.beethoven.type.ArrayType;
import de.sormuras.beethoven.type.ClassType;
import de.sormuras.beethoven.type.PrimitiveType;
import de.sormuras.beethoven.type.TypeVariable;
import de.sormuras.beethoven.type.VoidType;
import de.sormuras.beethoven.type.WildcardType;
import de.sormuras.beethoven.unit.AnnotationDeclaration;
import de.sormuras.beethoven.unit.AnnotationElement;
import de.sormuras.beethoven.unit.ConstantDeclaration;
import de.sormuras.beethoven.unit.PackageDeclaration;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

class AnnotatedTests {

  @Test
  void annotatedExtendsListable() {
    assertTrue(Listable.class.isAssignableFrom(Annotated.class));
  }

  void test(Supplier<? extends Annotated> supplier) {
    Annotated annotated = supplier.get();
    assertEquals(annotated, annotated);
    assertEquals(annotated, supplier.get());
    assertFalse(annotated.isAnnotated());
    assertNotNull(annotated.getDescription());
    assertNotNull(annotated.toString());
  }

  @TestFactory
  Stream<DynamicTest> primitives() {
    List<PrimitiveType.Primitive> primitives = List.of(PrimitiveType.Primitive.values());
    return DynamicTest.stream(
        primitives.iterator(), PrimitiveType.Primitive::name, p -> test(p::build));
  }

  @TestFactory
  List<DynamicTest> types() {
    return List.of(
        // type
        dynamicTest("VoidType", () -> test(VoidType::instance)),
        dynamicTest("WildcardType", () -> test(WildcardType::wildcard)),
        dynamicTest("TypeVariable", () -> test(() -> TypeVariable.variable("T"))),
        dynamicTest("ArrayType.Dimension", () -> test(() -> ArrayType.dimensions(1).get(0))),
        dynamicTest("ClassType.Simple", () -> test(() -> ClassType.simple("S"))),
        // unit
        dynamicTest("PackageDeclaration", () -> test(PackageDeclaration::new)),
        dynamicTest("ConstantDeclaration", () -> test(ConstantDeclaration::new)),
        dynamicTest("AnnotationElement", () -> test(AnnotationElement::new)),
        dynamicTest("PackageDeclaration", () -> test(AnnotationDeclaration::new)));
  }
}
