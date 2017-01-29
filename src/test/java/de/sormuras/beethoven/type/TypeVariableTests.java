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

package de.sormuras.beethoven.type;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.sormuras.beethoven.Annotation;
import org.junit.jupiter.api.Test;

class TypeVariableTests {

  @Test
  void defaults() {
    assertEquals("T", TypeVariable.variable("T").getIdentifier());
  }

  @Test
  void annotated() {
    assertEquals(
        "@Deprecated T",
        TypeVariable.variable("T")
            .annotated(i -> singletonList(Annotation.annotation(Deprecated.class)))
            .list());
  }

  @Test
  void constructorFailsWithEmptyName() {
    Exception e = assertThrows(Exception.class, () -> TypeVariable.variable(""));
    assertEquals("TypeVariable identifier must not be empty!", e.getMessage());
  }

  @Test
  void binaryIsUnsupported() {
    Exception e = assertThrows(Exception.class, () -> TypeVariable.variable("T").binary());
    assertEquals("Type variables have no binary class name.", e.getMessage());
  }
}
