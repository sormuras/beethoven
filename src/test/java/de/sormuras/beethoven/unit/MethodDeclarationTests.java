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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.sormuras.beethoven.Tests;
import de.sormuras.beethoven.type.ClassType;
import de.sormuras.beethoven.type.TypeVariable;
import java.lang.annotation.ElementType;
import java.util.List;
import org.junit.jupiter.api.Test;

class MethodDeclarationTests {

  @Test
  void constructor() {
    MethodDeclaration m = new MethodDeclaration();
    m.setName("<init>");
    assertEquals("<init>();\n", m.list("\n"));
    assertEquals(ElementType.METHOD, m.getAnnotationsTarget());
    assertEquals(true, m.isConstructor());
    assertEquals(false, m.isModified());
    assertEquals(false, m.isVarArgs());
    Exception expected = assertThrows(IllegalStateException.class, () -> m.setVarArgs(true));
    assertEquals(true, expected.toString().contains("no parameter"));
    // put into context
    //m.setEnclosingDeclaration(NormalClassDeclaration.of("Abc"));
    //assertEquals("Abc();\n", m.list());
  }

  @Test
  void declaration() {
    MethodDeclaration m = new MethodDeclaration();
    m.setName("m");
    assertEquals("void m();\n", m.list("\n"));
    assertEquals(ElementType.METHOD, m.getAnnotationsTarget());
    assertEquals(false, m.isConstructor());
    assertEquals(false, m.isModified());
    assertEquals(false, m.isVarArgs());
    Exception expected = assertThrows(IllegalStateException.class, () -> m.setVarArgs(true));
    assertEquals(true, expected.toString().contains("no parameter"));
  }

  @Test
  void emptyList() {
    ClassType listOfT =
        ClassType.type(List.class).parameterized(i -> List.of(TypeVariable.variable("T")));
    MethodDeclaration emptyList = new MethodDeclaration();
    emptyList.addAnnotation(SuppressWarnings.class, "unchecked");
    emptyList.addModifier("public", "static", "final");
    emptyList.addTypeParameter(new TypeParameter());
    emptyList.setReturnType(listOfT);
    emptyList.setName("emptyList");
    emptyList.addStatement("return ({L}) EMPTY_LIST", listOfT);
    Tests.assertEquals(getClass(), "emptyList", emptyList);
  }

  @Test
  void runnable() {
    MethodDeclaration runnable = new MethodDeclaration();
    runnable.addAnnotation(Override.class);
    runnable.addModifier("public");
    runnable.setName("run");
    runnable.addParameter(getClass(), "this");
    runnable.addThrows(RuntimeException.class);
    runnable.addThrows(TypeVariable.variable("X"));
    runnable.addStatement("System.out.println({S})", "Running!");
    Tests.assertEquals(getClass(), "runnable", runnable);
    assertEquals(true, runnable.isModified());
    assertEquals(false, runnable.isVarArgs());
    assertSame(runnable, runnable.getParameters().get(0).getMethodDeclaration().get());
    Exception expected = assertThrows(IllegalStateException.class, () -> runnable.setVarArgs(true));
    assertEquals(true, expected.toString().contains("array type expected"));
  }

  @Test
  void varArgs() {
    MethodDeclaration var = new MethodDeclaration();
    var.setName("var");
    var.addParameter(int[].class, "numbers");
    assertEquals(false, var.isVarArgs());
    assertEquals("void var(int[] numbers);\n", var.list("\n"));
    var.setVarArgs(true);
    assertEquals(true, var.isVarArgs());
    assertEquals("void var(int... numbers);\n", var.list("\n"));
    var.setVarArgs(false);
    assertEquals(false, var.isVarArgs());
    assertEquals("void var(int[] numbers);\n", var.list("\n"));
  }
}
