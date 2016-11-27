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

package de.sormuras.beethoven.composer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import de.sormuras.beethoven.Name;
import de.sormuras.beethoven.Tests;
import de.sormuras.beethoven.type.ClassType;
import de.sormuras.beethoven.unit.ClassDeclaration;
import de.sormuras.beethoven.unit.CompilationUnit;
import de.sormuras.beethoven.unit.NormalClassDeclaration;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Test;

class ImportsComposerTests {

  @Test
  void empty() {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName("test");
    ClassDeclaration empty = unit.declareClass("Empty");
    empty.setModifiers(Modifier.PUBLIC);
    String expected = unit.list();
    new ImportsComposer().apply(unit);
    String actual = unit.list();
    assertEquals(expected, actual);
  }

  @Test
  void extendsObject() {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName("test");
    NormalClassDeclaration empty = unit.declareClass("ExtendsObject");
    empty.setModifiers(Modifier.PUBLIC);
    empty.setSuperClass(ClassType.OBJECT);
    String expected = unit.list();
    new ImportsComposer().apply(unit);
    String actual = unit.list();
    assertEquals(expected, actual);
  }

  @Test
  void extendsTypeInSamePackage() {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName("test");
    NormalClassDeclaration empty = unit.declareClass("ExtendsTypeInSamePackage");
    empty.setModifiers(Modifier.PUBLIC);
    empty.setSuperClass(ClassType.type(Name.name("test", "TypeInSamePackage")));
    String expected = unit.list();
    new ImportsComposer().apply(unit);
    String actual = unit.list();
    assertEquals(expected, actual);
  }

  @Test
  void instantAndNumberFields() {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName("test");
    unit.getImportDeclarations().addSingleTypeImport(java.time.Instant.class);
    unit.getImportDeclarations().addSingleTypeImport(java.util.Date.class);
    NormalClassDeclaration empty = unit.declareClass("Fields");
    empty.setModifiers(Modifier.PUBLIC);
    empty.declareField(java.time.Instant.class, "timeInstant");
    empty.declareField(ClassType.type(Name.name("test", "Instant")), "testInstant");
    empty.declareField(Number.class, "langNumber");
    empty.declareField(ClassType.type(Name.name("test", "Number")), "testNumber");
    String expected = unit.list();
    new ImportsComposer().apply(unit);
    String actual = unit.list();
    assertNotEquals(expected, actual);
    Tests.assertEquals(getClass(), "instantAndNumberFields", unit);
  }

  @Test
  void unused() {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName("test");
    unit.getImportDeclarations().addSingleTypeImport(java.time.Instant.class);
    unit.getImportDeclarations().addSingleTypeImport(java.util.Date.class);
    unit.declareInterface("Unused");
    // first, keep user-declared imports
    new ImportsComposer().setRemoveUnused(false).apply(unit);
    assertEquals(
        "package test;\n"
            + "\n"
            + "import java.time.Instant;\n"
            + "import java.util.Date;\n"
            + "\n"
            + "interface Unused {\n"
            + "}\n",
        unit.list("\n"));
    // now, remove unused imports
    new ImportsComposer().setRemoveUnused(true).apply(unit);
    assertEquals("package test;\n\ninterface Unused {\n}\n", unit.list("\n"));
  }
}
