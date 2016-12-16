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

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.sormuras.beethoven.Name;
import de.sormuras.beethoven.Tests;
import de.sormuras.beethoven.unit.ClassDeclaration;
import de.sormuras.beethoven.unit.CompilationUnit;
import java.lang.Thread.State;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Test;

class ComposerTests {

  @Test
  void properties() throws Exception {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName("pool");
    ClassDeclaration car = unit.declareClass("Car");
    car.setModifiers(Modifier.PUBLIC);
    new PropertyComposer()
        .setType(String.class)
        .setName("name")
        .setSetterAvailable(false)
        .setFieldFinal(true)
        .apply(car);
    new PropertyComposer().setType(Number.class).setName("gear").apply(car);
    new PropertyComposer()
        .setType(State.class)
        .setName("state")
        .setSetterRequiresNonNullValue(true)
        .setSetterReturnsThis(true)
        .setFieldInitializer(listing -> listing.add(Name.cast(State.NEW)))
        .apply(car);

    new ConstructorComposer().apply(car);
    new EqualsComposer().apply(car);
    new HashCodeComposer().apply(car);
    new ToStringComposer().apply(car);

    new ImportsComposer().apply(unit);

    Tests.assertEquals(getClass(), "properties", unit);
    Class<?> carClass = unit.compile();
    Object beetle =
        carClass
            .getConstructor(String.class, Number.class, State.class)
            .newInstance("Beetle", 53, State.RUNNABLE);
    assertEquals("Car[name=Beetle, gear=53, state=RUNNABLE]", beetle.toString());
  }
}
