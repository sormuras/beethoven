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

import de.sormuras.beethoven.Listable;
import de.sormuras.beethoven.Tests;
import de.sormuras.beethoven.type.ClassType;
import de.sormuras.beethoven.type.Type;
import java.lang.annotation.ElementType;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Test;

class EnumDeclarationTests {

  @Test
  void empty() {
    ClassDeclaration declaration = new EnumDeclaration();
    declaration.setName("Empty");
    assertEquals("enum Empty {\n}\n", declaration.list("\n"));
  }

  @Test
  void everything() {
    EnumDeclaration declaration = new EnumDeclaration();
    declaration.setName("Everything");
    declaration.addInterface(Type.type(Runnable.class));
    assertEquals("enum Everything implements Runnable {\n}\n", declaration.list("\n"));
    declaration.addInterface(ClassType.parameterized(Comparable.class, Byte.class));
    assertEquals(
        "enum Everything implements Runnable, Comparable<Byte> {\n}\n", declaration.list("\n"));
    declaration.getInterfaces().clear();
    declaration.declareConstant("A");
    assertEquals("enum Everything {\n\n  A\n}\n", declaration.list("\n"));
    declaration.declareConstant("B", Listable.IDENTITY).addAnnotation(Deprecated.class);
    NormalClassDeclaration cbody = new NormalClassDeclaration();
    MethodDeclaration toString = cbody.declareMethod(String.class, "toString");
    toString.addStatement("return \"c\" + i");
    toString.addModifier(Modifier.PUBLIC);
    declaration.declareConstant("C", l -> l.add("123"), cbody);
    declaration.declareField(int.class, "i");
    declaration.declareConstructor().addStatement("this(0)");
    MethodDeclaration ctor = declaration.declareConstructor();
    ctor.declareParameter(int.class, "i");
    ctor.addStatement("this.i = i");
    Tests.assertEquals(getClass(), "everything", declaration);
  }

  @Test
  void target() {
    assertEquals(ElementType.TYPE, new EnumDeclaration().getAnnotationsTarget());
  }
}
