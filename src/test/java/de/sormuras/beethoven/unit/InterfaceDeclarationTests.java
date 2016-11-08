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

import de.sormuras.beethoven.Name;
import de.sormuras.beethoven.Tests;
import de.sormuras.beethoven.type.Type;
import java.lang.annotation.ElementType;
import java.math.BigInteger;
import java.util.function.Consumer;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Test;

class InterfaceDeclarationTests {

  @Test
  void empty() {
    TypeDeclaration declaration = new InterfaceDeclaration();
    declaration.setName("Empty");
    assertEquals("interface Empty {\n}\n", declaration.list("\n"));
  }

  @Test
  void everything() {
    InterfaceDeclaration declaration = new InterfaceDeclaration();
    declaration.setName("Everything");
    declaration.addTypeParameter(new TypeParameter());
    declaration.addInterface(Type.type(Runnable.class));
    declaration.declareConstant(Type.type(String.class), "EMPTY_TEXT", "");
    declaration
        .declareConstant(Type.type(float.class), "PI", l -> l.add("3.141F"))
        .addAnnotation(Deprecated.class);
    declaration.declareConstant(Type.type(double.class), "E", Name.name(Math.class, "E"));
    declaration.declareMethod(BigInteger.class, "id");
    Tests.assertEquals(getClass(), "everything", declaration);
  }

  @Test
  void nested() {
    InterfaceDeclaration top = new InterfaceDeclaration();
    top.setName("Top");
    InterfaceDeclaration nested = top.declareInterface("Nested");
    InterfaceDeclaration base64 = nested.declareInterface("Base64");
    Consumer<InterfaceDeclaration> constants =
        declaration -> {
          declaration.declareConstant(top.toType(), "topper", (Object) null);
          declaration.declareConstant(nested.toType(), "nested", (Object) null);
          declaration.declareConstant(base64.toType(), "base64", (Object) null);
        };
    constants.accept(top);
    constants.accept(nested);
    constants.accept(base64);
    Tests.assertEquals(getClass(), "nested", top);
  }

  @Test
  void target() {
    assertEquals(ElementType.TYPE, new InterfaceDeclaration().getAnnotationsTarget());
  }

  @Test
  void override() {
    InterfaceDeclaration base = new InterfaceDeclaration();
    base.setName("Base");
    base.declareMethod(Object.class, "method").addParameter(String.class, "text");
    InterfaceDeclaration next = new InterfaceDeclaration();
    next.setName("Next");
    next.addInterface(base.toType());
    MethodDeclaration over = next.declareOverride(base.getMethods().get(0)); // method from above
    over.addModifier(Modifier.DEFAULT);
    over.setBody(new Block());
    assertEquals("@Override\ndefault Object method(String text) {\n}\n", over.list("\n"));
  }
}
