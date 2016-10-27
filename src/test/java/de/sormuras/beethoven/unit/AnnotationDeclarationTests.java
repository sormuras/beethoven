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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.sormuras.beethoven.Name;
import de.sormuras.beethoven.Tests;
import de.sormuras.beethoven.type.ClassType;
import de.sormuras.beethoven.type.Type;
import de.sormuras.beethoven.type.WildcardType;
import java.lang.*;
import java.lang.annotation.ElementType;
import java.util.Formatter;
import java.util.List;
import org.junit.jupiter.api.Test;

class AnnotationDeclarationTests {

  @Test
  void empty() {
    TypeDeclaration declaration = new AnnotationDeclaration();
    declaration.setName("Empty");
    assertEquals("@interface Empty {\n}\n", declaration.list("\n"));
    assertTrue(declaration.isEmpty());
    assertFalse(
        new AnnotationDeclaration()
            .declareConstant(Type.type(int.class), "constant", l -> l)
            .getEnclosingDeclaration()
            .isEmpty());
    assertFalse(
        new AnnotationDeclaration()
            .declareElement(Type.type(int.class), "element")
            .getEnclosingDeclaration()
            .isEmpty());
  }

  @Test
  void everything() {
    WildcardType extendsFormatter = WildcardType.extend(Formatter.class);
    AnnotationDeclaration declaration = new AnnotationDeclaration();
    declaration.setName("Everything");
    declaration.declareConstant(Type.type(String.class), "EMPTY_TEXT", "");
    declaration
        .declareConstant(Type.type(float.class), "PI", l -> l.add("3.141F"))
        .addAnnotation(Deprecated.class);
    declaration.declareConstant(Type.type(double.class), "E", Name.name(Math.class, "E"));
    declaration.declareElement(Type.type(int.class), "id");
    declaration
        .declareElement(Type.type(String.class), "date", "201608032129")
        .addAnnotation(Deprecated.class);
    declaration.declareElement(
        ClassType.type(java.lang.Class.class).parameterized(i -> List.of(extendsFormatter)),
        "formatterClass");
    Tests.assertEquals(getClass(), "everything", declaration);
    assertFalse(declaration.isEmpty());
  }

  @Test
  void target() {
    assertEquals(ElementType.TYPE, new AnnotationDeclaration().getAnnotationsTarget());
  }
}
