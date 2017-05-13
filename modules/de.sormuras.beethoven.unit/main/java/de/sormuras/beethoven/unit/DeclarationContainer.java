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

import de.sormuras.beethoven.Listable;
import java.util.List;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;

public interface DeclarationContainer extends Listable {

  /** Validate if the given name can be used as nested declaration name. */
  default void assertValidNestedDeclarationName(String name) {
    if (!SourceVersion.isName(name)) {
      throw new IllegalArgumentException("expected valid name, but got: \"" + name + "\"");
    }
    if (getDeclarations().stream().anyMatch(d -> d.getName().equals(name))) {
      throw new IllegalArgumentException("duplicate name for nested type " + name);
    }
  }

  /** Declare type as nested child. */
  default <T extends TypeDeclaration> T declare(T declaration, String name, Modifier... modifiers) {
    assertValidNestedDeclarationName(name);
    declaration.setName(name);
    if (modifiers.length > 0) {
      declaration.setModifiers(modifiers);
    }
    getDeclarations().add(declaration);
    return declaration;
  }

  /** Declare annotation as nested child. */
  default AnnotationDeclaration declareAnnotation(String name) {
    return declare(new AnnotationDeclaration(), name);
  }

  /** Declare normal class as nested child. */
  default NormalClassDeclaration declareClass(String name, Modifier... modifiers) {
    return declare(new NormalClassDeclaration(), name, modifiers);
  }

  /** Declare enum as nested child. */
  default EnumDeclaration declareEnum(String name) {
    return declare(new EnumDeclaration(), name);
  }

  /** Declare enum as nested child. */
  default InterfaceDeclaration declareInterface(String name) {
    return declare(new InterfaceDeclaration(), name);
  }

  List<TypeDeclaration> getDeclarations();
}
