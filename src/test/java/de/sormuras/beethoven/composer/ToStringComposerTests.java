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

import de.sormuras.beethoven.Tests;
import de.sormuras.beethoven.unit.ClassDeclaration;
import de.sormuras.beethoven.unit.CompilationUnit;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ToStringComposerTests {

  @Test
  void empty() throws Exception {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName("test");
    ClassDeclaration empty = unit.declareClass("Empty");
    empty.setModifiers(Modifier.PUBLIC);
    new ToStringComposer().apply(empty);

    Tests.assertEquals(getClass(), "empty", unit);
    Assertions.assertTrue(unit.compile(Object.class).toString().contains("test"));
    Assertions.assertTrue(unit.compile(Object.class).toString().contains("Empty"));
    Assertions.assertTrue(unit.compile(Object.class).toString().contains("@"));
  }
}
