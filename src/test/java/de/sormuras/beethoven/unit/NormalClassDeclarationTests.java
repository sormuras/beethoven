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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.sormuras.beethoven.type.ClassType;
import de.sormuras.beethoven.type.Type;
import java.lang.annotation.ElementType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NormalClassDeclarationTests {

  @Test
  void empty() {
    NormalClassDeclaration declaration = new NormalClassDeclaration();
    declaration.setName("Empty");
    Assertions.assertEquals("class Empty {\n}\n", declaration.list("\n"));
    assertTrue(declaration.isEmpty());
    assertFalse(new NormalClassDeclaration().declareInitializer(false).getEnclosing().isEmpty());
    assertFalse(
        new NormalClassDeclaration()
            .declareField(int.class, "i")
            .getEnclosingDeclaration()
            .isEmpty());
  }

  @Test
  void generic() {
    NormalClassDeclaration declaration = new NormalClassDeclaration();
    declaration.setName("G");
    declaration.addTypeParameter(new TypeParameter());
    Assertions.assertEquals("class G<T> {\n}\n", declaration.list("\n"));
    declaration.addTypeParameter(
        TypeParameter.of("I", ClassType.parameterized(Iterable.class, Long.class)));
    Assertions.assertEquals("class G<T, I extends Iterable<Long>> {\n}\n", declaration.list("\n"));
  }

  @Test
  void interfaces() {
    NormalClassDeclaration declaration = new NormalClassDeclaration();
    declaration.setName("I");
    declaration.addInterface(Type.type(Runnable.class));
    Assertions.assertEquals("class I implements Runnable {\n}\n", declaration.list("\n"));
    declaration.addInterface(ClassType.parameterized(Comparable.class, Byte.class));
    Assertions.assertEquals(
        "class I implements Runnable, Comparable<Byte> {\n}\n", declaration.list("\n"));
  }

  @Test
  void superclass() {
    NormalClassDeclaration declaration = new NormalClassDeclaration();
    declaration.setName("C");
    declaration.setSuperClass(ClassType.type(Object.class));
    Assertions.assertEquals("class C extends Object {\n}\n", declaration.list("\n"));
  }

  @Test
  void target() {
    assertEquals(ElementType.TYPE, new NormalClassDeclaration().getAnnotationsTarget());
  }
}
