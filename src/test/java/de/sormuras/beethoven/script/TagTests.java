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

package de.sormuras.beethoven.script;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TagTests {

  @Test
  void reflectNullFails() {
    assertThrows(NullPointerException.class, () -> Tag.reflect("#", null));
  }

  @Test
  void reflectWithAutoBoxing() {
    assertEquals(1, Tag.reflect("#", 1));
    assertEquals(Integer.class, Tag.reflect("#class", 1));
    assertEquals("Integer", Tag.reflect("#class.simpleName", 1));
    assertEquals(30, Tag.reflect("#class.simpleName.hashCode.byteValue.intValue", 1));
  }
}
