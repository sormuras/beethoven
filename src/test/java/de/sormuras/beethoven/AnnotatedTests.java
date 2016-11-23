/*
 * Copyright (C) 2016 Christian Stein
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package de.sormuras.beethoven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import de.sormuras.beethoven.type.ArrayType;
import de.sormuras.beethoven.type.ClassType;
import de.sormuras.beethoven.type.PrimitiveType.Primitive;
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
    List<Primitive> primitives = List.of(Primitive.values());
    return DynamicTest.stream(primitives.iterator(), Primitive::name, p -> test(p::build));
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
        dynamicTest("AnnotationDeclaration", () -> test(AnnotationDeclaration::new)));
  }
}
