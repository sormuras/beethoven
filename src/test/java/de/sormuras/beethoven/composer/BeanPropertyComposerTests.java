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

import static de.sormuras.beethoven.unit.UnitTool.addConstructor;
import static de.sormuras.beethoven.unit.UnitTool.addToString;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.sormuras.beethoven.Name;
import de.sormuras.beethoven.Tests;
import de.sormuras.beethoven.unit.ClassDeclaration;
import de.sormuras.beethoven.unit.CompilationUnit;
import java.lang.Thread.State;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Test;

class BeanPropertyComposerTests {

  @Test
  void properties() throws Exception {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName("pool");
    ClassDeclaration car = unit.declareClass("Car");
    car.setModifiers(Modifier.PUBLIC);
    new BeanPropertyComposer()
        .setType(String.class)
        .setName("name")
        .setSetterAvailable(false)
        .setFieldFinal(true)
        .accept(car);
    new BeanPropertyComposer().setType(Number.class).setName("gear").accept(car);
    new BeanPropertyComposer()
        .setType(State.class)
        .setName("state")
        .setSetterReturnsThis(true)
        .setFieldInitializer(listing -> listing.add(Name.cast(State.NEW)))
        .accept(car);
    addConstructor(car);
    addToString(car);

    Tests.assertEquals(getClass(), "properties", unit);
    Class<?> carClass = unit.compile();
    Object beetle =
        carClass
            .getConstructor(String.class, Number.class, State.class)
            .newInstance("Beetle", 53, State.RUNNABLE);
    assertEquals("Car[name=Beetle, gear=53, state=RUNNABLE]", beetle.toString());
  }
}
