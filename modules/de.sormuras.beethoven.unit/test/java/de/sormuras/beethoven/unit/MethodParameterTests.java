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
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.sormuras.beethoven.Annotation;
import de.sormuras.beethoven.Name;
import de.sormuras.beethoven.type.Type;
import de.sormuras.beethoven.type.TypeVariable;
import java.lang.annotation.ElementType;
import org.junit.jupiter.api.Test;

class MethodParameterTests {

  private MethodParameter of(Class<?> type, String name) {
    return new MethodParameter().setType(Type.type(type)).setName(name);
  }

  @Test
  void simple() {
    assertEquals("int i", of(int.class, "i").list());
    assertEquals("int... ia1", of(int[].class, "ia1").setVariable(true).list());
    assertEquals("int[]... ia2", of(int[][].class, "ia2").setVariable(true).list());
    MethodParameter parameter =
        new MethodParameter().setType(TypeVariable.variable("T")).setName("t").setFinal(true);
    parameter.addAnnotation(Annotation.annotation(Name.name("A")));
    assertEquals("final @A T t", parameter.list());
    assertEquals(ElementType.PARAMETER, new MethodParameter().getAnnotationsTarget());
    IllegalStateException expected =
        assertThrows(IllegalStateException.class, () -> of(int.class, "i").setVariable(true));
    assertEquals(true, expected.toString().contains("array type expected"));
  }
}
