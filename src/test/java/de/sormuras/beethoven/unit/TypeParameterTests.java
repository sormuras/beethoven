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
import de.sormuras.beethoven.type.ClassType;
import de.sormuras.beethoven.type.Type;
import java.lang.annotation.ElementType;
import org.junit.jupiter.api.Test;

class TypeParameterTests {

  @Test
  void simple() {
    assertEquals("T", new TypeParameter().getName());
    assertEquals("T", TypeParameter.of("T").list());
    assertEquals(ElementType.TYPE_PARAMETER, new TypeParameter().getAnnotationsTarget());
    assertEquals("T extends S", TypeParameter.of("T", "S").list());
    Type run = ClassType.type(Runnable.class);
    assertEquals("S extends Runnable", TypeParameter.of("S", run).list());
    // annotated
    TypeParameter parameter = new TypeParameter();
    parameter.addAnnotation(Annotation.annotation(Name.name("A")));
    assertEquals("@A T", parameter.list());
  }

  @Test
  void boundWithTypeVariable() {
    TypeParameter tp = TypeParameter.of("T", "TV");
    assertEquals("T extends TV", tp.list());
    assertEquals("TV", tp.getBoundTypeVariable().get().getIdentifier());
    assertEquals(true, tp.getBounds().isEmpty());
  }

  @Test
  void boundWithClassType() {
    TypeParameter tp = new TypeParameter();
    tp.addBounds(ClassType.type(Number.class), ClassType.type(Cloneable.class));
    tp.addBounds();
    assertEquals("T extends Number & Cloneable", tp.list());
    assertEquals(false, tp.getBoundTypeVariable().isPresent());
    assertEquals(false, tp.getBounds().isEmpty());
    // clears bounds by setting bound type variable
    tp.setBoundTypeVariable("S");
    assertEquals(true, tp.getBoundTypeVariable().isPresent());
    assertEquals(true, tp.getBounds().isEmpty());
  }

  @Test
  void constructorFailsWithIllegalName() {
    assertThrows(AssertionError.class, () -> TypeParameter.of("123"));
  }
}
