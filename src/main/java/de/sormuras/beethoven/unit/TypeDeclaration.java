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

import static java.util.Collections.addAll;

import de.sormuras.beethoven.Name;
import de.sormuras.beethoven.type.ClassType;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class TypeDeclaration extends ClassMember implements DeclarationContainer {

  private List<TypeDeclaration> declarations = Collections.emptyList();

  @Override
  public void assertValidNestedDeclarationName(String name) {
    DeclarationContainer.super.assertValidNestedDeclarationName(name);
    for (NamedMember member = this; member != null; member = member.getEnclosingDeclaration()) {
      if (name.equals(member.getName())) {
        throw new IllegalArgumentException("nested " + name + " hides an enclosing type");
      }
    }
  }

  @Override
  public <T extends TypeDeclaration> T declare(T declaration, String name) {
    DeclarationContainer.super.declare(declaration, name);
    declaration.setEnclosingDeclaration(this);
    declaration.setCompilationUnit(getCompilationUnit());
    return declaration;
  }

  @Override
  public ElementType getAnnotationsTarget() {
    return ElementType.TYPE;
  }

  @Override
  public List<TypeDeclaration> getDeclarations() {
    if (declarations == Collections.EMPTY_LIST) {
      declarations = new ArrayList<>();
    }
    return declarations;
  }

  @Override
  public boolean isEmpty() {
    return isDeclarationsEmpty();
  }

  public boolean isDeclarationsEmpty() {
    return declarations.isEmpty();
  }

  /** Return simple {@link Name} representation of this type declaration. */
  public Name toName() {
    List<String> identifiers = new ArrayList<>();
    if (getCompilationUnit() != null) {
      addAll(identifiers, getCompilationUnit().getPackageName().split("\\."));
    }
    int packageLevel = identifiers.size();
    for (NamedMember member = this; member != null; member = member.getEnclosingDeclaration()) {
      identifiers.add(0, member.getName());
    }
    return Name.name(packageLevel, identifiers);
  }

  public ClassType toType() {
    return ClassType.type(toName());
  }
}
