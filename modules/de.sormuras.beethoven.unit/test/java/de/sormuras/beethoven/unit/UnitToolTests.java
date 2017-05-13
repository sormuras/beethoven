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

import de.sormuras.beethoven.Annotation;
import de.sormuras.beethoven.Name;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Test;

class UnitToolTests {

  @Test
  void override() {
    MethodDeclaration base = new MethodDeclaration();
    base.addAnnotation(Override.class);
    base.addAnnotation(Annotation.annotation(Name.name("Reply"), 42));
    base.setReturnType(Object.class);
    base.setName("method");
    base.declareParameter(String.class, "text");
    MethodDeclaration over = UnitTool.override(base, false);
    over.addModifier(Modifier.DEFAULT);
    over.setBody(new Block());
    assertEquals("@Override\ndefault Object method(String text) {\n}\n", over.list("\n"));
    over = UnitTool.override(base, true);
    over.setBody(new Block());
    assertEquals("@Reply(42)\n@Override\nObject method(String text) {\n}\n", over.list("\n"));
  }
}
