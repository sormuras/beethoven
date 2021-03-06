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

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.sormuras.beethoven.Listable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

class AnnotatableTests {

  @Test
  void annotatableExtendsListable() {
    assertTrue(Listable.class.isAssignableFrom(Annotatable.class));
  }

  private void annotatable(Annotatable annotatable) {
    assertFalse(annotatable.isAnnotated());
    annotatable.addAnnotation(Deprecated.class);
    assertTrue(annotatable.isAnnotated());
    assertFalse(annotatable.isTagged());
    assertFalse(annotatable.getTag("1").isPresent());
    annotatable.getTags();
    assertFalse(annotatable.isTagged());
    annotatable.getTags().put("1", "2");
    assertTrue(annotatable.isTagged());
    assertTrue(annotatable.getTag("1").isPresent());
  }

  @TestFactory
  Stream<DynamicTest> annotatables() {
    Function<Annotatable, String> name = a -> "annotatable(" + a.getClass().getSimpleName() + ")";
    List<Annotatable> annotatables =
        asList( //
            new AnnotationDeclaration(),
            new AnnotationElement(),
            new ConstantDeclaration(),
            new EnumConstant(),
            new EnumDeclaration(),
            new FieldDeclaration(),
            new InterfaceDeclaration(),
            new MethodDeclaration(),
            new MethodParameter(),
            new ModuleDeclaration(),
            new NormalClassDeclaration(),
            new PackageDeclaration(),
            new TypeParameter()
            //
            );
    return DynamicTest.stream(annotatables.iterator(), name, this::annotatable);
  }
}
