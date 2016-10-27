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

package de.sormuras.beethoven.unit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class DeclarationContainerTests {

  @Test
  void compilationUnit() {
    test(CompilationUnit::new);
  }

  @Test
  void annotationDeclaration() {
    test(AnnotationDeclaration::new);
  }

  @Test
  void classDeclaration() {
    test(NormalClassDeclaration::new);
  }

  @Test
  void enumDeclaration() {
    test(EnumDeclaration::new);
  }

  @Test
  void interfaceDeclaration() {
    test(InterfaceDeclaration::new);
  }

  private void test(Supplier<DeclarationContainer> supplier) {
    illegalJavaNameFails(supplier.get());
    duplicateSiblingNameFails(supplier.get());
    duplicateNestedNameFails(supplier.get());
  }

  private void duplicateNestedNameFails(DeclarationContainer container) {
    DeclarationContainer parent = container.declareClass("A");
    Exception e = assertThrows(Exception.class, () -> parent.declareClass("A"));
    assertTrue(e.getMessage().contains("nested"));
    DeclarationContainer child = parent.declareClass("B");
    e = assertThrows(Exception.class, () -> child.declareClass("A"));
    assertTrue(e.getMessage().contains("nested"));
  }

  private void duplicateSiblingNameFails(DeclarationContainer container) {
    container.declareClass("A");
    Exception e = assertThrows(Exception.class, () -> container.declareClass("A"));
    assertTrue(e.getMessage().contains("duplicate"));
  }

  private void illegalJavaNameFails(DeclarationContainer container) {
    Exception e = assertThrows(Exception.class, () -> container.declareClass("123"));
    assertTrue(e.getMessage().contains("valid"));
  }
}
