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

import de.sormuras.beethoven.Name;
import de.sormuras.beethoven.Tests;
import de.sormuras.beethoven.unit.ClassDeclaration;
import de.sormuras.beethoven.unit.CompilationUnit;
import java.util.Objects;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Test;

class HashCodeComposerTests {

  @Test
  void empty() throws Exception {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName("test");
    ClassDeclaration empty = unit.declareClass("Empty");
    empty.setModifiers(Modifier.PUBLIC);
    new HashCodeComposer().apply(empty);

    Tests.assertEquals(getClass(), "empty", unit);
    unit.compile();
  }

  @Test
  void single() throws Exception {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName("test");
    ClassDeclaration single = unit.declareClass("Single");
    single.setModifiers(Modifier.PUBLIC);
    single.declareField(String.class, "text");
    new HashCodeComposer().apply(single);

    Tests.assertEquals(getClass(), "single", unit);
    unit.compile();
  }

  @Test
  void xyz() throws Exception {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName("test");
    unit.getImportDeclarations().addSingleTypeImport(Objects.class);
    // unit.getImportDeclarations().addSingleStaticImport(Name.reflect(Objects.class, "hash"));
    ClassDeclaration xyz = unit.declareClass("Xyz");
    xyz.setModifiers(Modifier.PUBLIC);
    xyz.declareField(String.class, "x");
    xyz.declareField(boolean.class, "y");
    xyz.declareField(Thread.State.class, "z");
    new HashCodeComposer().apply(xyz);

    Tests.assertEquals(getClass(), "xyz", unit);
    unit.compile();
  }
}
