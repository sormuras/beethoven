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

package de.sormuras.beethoven.composer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import de.sormuras.beethoven.Tests;
import de.sormuras.beethoven.unit.ClassDeclaration;
import de.sormuras.beethoven.unit.CompilationUnit;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Test;

class EqualsComposerTests {

  @Test
  void empty() throws Exception {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName("test");
    ClassDeclaration empty = unit.declareClass("Empty");
    empty.setModifiers(Modifier.PUBLIC);
    new EqualsComposer().apply(empty);

    Tests.assertEquals(getClass(), "empty", unit);

    Object a = unit.compile(Object.class);
    Object b = unit.compile(Object.class);
    assertSame(a, a);
    assertEquals(a, a);
    assertNotSame(a, b);
    assertNotEquals(a, b);
  }
}
