/*
 * Copyright 2017 Christian Stein
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package de.sormuras.beethoven.unit;

import static de.sormuras.beethoven.Annotation.annotation;
import static de.sormuras.beethoven.unit.ModuleDeclaration.RequiresModifier.STATIC;
import static de.sormuras.beethoven.unit.ModuleDeclaration.RequiresModifier.TRANSITIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.sormuras.beethoven.Name;
import de.sormuras.beethoven.Tests;
import org.junit.jupiter.api.Test;

class ModuleDeclarationTests {

  @Test
  void empty() {
    assertTrue(new ModuleDeclaration().isEmpty());
  }

  @Test
  void simple() throws Exception {
    ModuleDeclaration module = new ModuleDeclaration();
    module.setName(Name.name("com.foo.bar"));
    assertEquals("module com.foo.bar {\n}\n", module.list("\n"));
    // TODO JDK9 module.compile(); https://github.com/sormuras/beethoven/issues/9
  }

  @Test
  void open() throws Exception {
    ModuleDeclaration module = new ModuleDeclaration();
    module.setName(Name.name("com.foo.bar"));
    module.setOpen(true);
    assertEquals("open module com.foo.bar {\n}\n", module.list("\n"));
    // TODO JDK9 module.compile(); https://github.com/sormuras/beethoven/issues/9
  }

  @Test
  void normalAndOpen() throws Exception {
    Name foo = Name.name("Foo");
    Name bar = Name.name("Bar");
    ModuleDeclaration module = new ModuleDeclaration();
    module.setOpen(false);
    module.addAnnotations(annotation(foo, 1), annotation(foo, 2), annotation(bar));
    module.setName(Name.name("M.N"));
    module.requires(Name.name("A.B"));
    module.requires(Name.name("C.D"), TRANSITIVE);
    module.requires(Name.name("E.F"), STATIC);
    module.requires(Name.name("G.H"), TRANSITIVE, STATIC);
    module.exports(Name.name("P.Q"));
    module.exports(Name.name("R.S"), Name.name("T1.U1"), Name.name("T2.U2"));
    module.opens(Name.name("P.Q"));
    module.opens(Name.name("R.S"), Name.name("T1.U1"), Name.name("T2.U2"));
    module.uses(Name.name("V.W"));
    module.provides(Name.name("X.Y"), Name.name("Z1.Z2"), Name.name("Z3.Z4"));
    Tests.assertEquals(getClass(), "normal", module);
    module.setOpen(true);
    Tests.assertEquals(getClass(), "open", module);
  }
}
