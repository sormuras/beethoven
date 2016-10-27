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
import static org.junit.jupiter.api.Assertions.assertNull;

import de.sormuras.beethoven.type.Type;
import java.lang.annotation.ElementType;
import org.junit.jupiter.api.Test;

class FieldDeclarationTests {

  @Test
  void empty() {
    FieldDeclaration i = new FieldDeclaration();
    i.setType(Type.type(int.class));
    i.setName("i");
    assertEquals("int i;\n", i.list("\n"));
    assertEquals(ElementType.FIELD, i.getAnnotationsTarget());
    assertEquals(false, i.isModified());
    assertNull(i.getEnclosingDeclaration());
    i.setInitializer(l -> l.add(Integer.toString(4711)));
    assertEquals("int i = 4711;\n", i.list("\n"));
  }
}
