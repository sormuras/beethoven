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

import de.sormuras.beethoven.Name;
import de.sormuras.beethoven.Tests;

import static de.sormuras.beethoven.unit.ModuleDeclaration.Scope.COMPILATION;
import static de.sormuras.beethoven.unit.ModuleDeclaration.Scope.EXECUTION;
import static de.sormuras.beethoven.unit.ModuleDeclaration.Scope.REFLECTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ModuleDeclarationTests {

  @Test
  void empty() {
    assertTrue(new ModuleDeclaration().isEmpty());
  }

  @Test
  void simple() {
    ModuleDeclaration module = new ModuleDeclaration();
    module.setName("M");
    assertEquals("module M {\n}\n", module.list("\n"));
    module.setVersion("1.0");
    assertEquals("module M @ 1.0 {\n}\n", module.list("\n"));
  }

  @Test
  void requiresModule() {
    ModuleDeclaration module = new ModuleDeclaration();
    module.setName("M");
    module.setVersion("1.0");
    module.requiresModule("A", ">= 2.0");
    module.requiresModule("B", null, COMPILATION, REFLECTION);
    module.requiresModule("C", "= 9.9.1", EXECUTION);
    assertEquals(
        "module M @ 1.0 {\n" //
            + "  requires A @ >= 2.0;\n" //
            + "  requires B for compilation, reflection;\n" //
            + "  requires C @ = 9.9.1 for execution;\n" //
            + "}\n",
        module.list("\n"));
  }

  @Test
  void requiresService() {
    ModuleDeclaration module = new ModuleDeclaration();
    module.setName("M");
    module.setVersion("1.0");
    module.requiresService("S1");
    module.requiresService(true, Name.name("S2"));
    assertEquals(
        "module M @ 1.0 {\n" //
            + "  requires service S1;\n" //
            + "  requires optional service S2;\n" //
            + "}\n",
        module.list("\n"));
  }

  @Test
  void jigsaw() {
    ModuleDeclaration module = new ModuleDeclaration();
    module.setName("M");
    module.setVersion("1.0");
    module.requiresModule("A", ">= 2.0");
    module.requiresModule("B", null, COMPILATION, REFLECTION);
    module.requiresService("S1");
    module.requiresService(true, Name.name("S2"));
    module.providesModule(Name.name("MI"), "4.0");
    module.providesService(Name.name("MS"), Name.name("C"));
    module.exports(Name.name("ME"));
    module.permits(Name.name("MF"));
    module.entryPoint(Name.name("MMain"));
    ModuleView view = module.declareView("N");
    view.providesModule(Name.name("NI"), "1.0");
    view.providesService(Name.name("NS"), Name.name("D"));
    view.exports(Name.name("NE"));
    view.permits(Name.name("MF"));
    view.entryPoint(Name.name("NMain"));
    Tests.assertEquals(getClass(), "jigsaw", module);
  }
}
