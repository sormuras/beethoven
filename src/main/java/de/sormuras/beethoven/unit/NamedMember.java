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

/** Named, annotatable and enclose-able member base class. */
public abstract class NamedMember extends Annotatable {

  private CompilationUnit compilationUnit = null;
  private TypeDeclaration enclosing = null;
  private String name = "DefaultName";

  public CompilationUnit getCompilationUnit() {
    return compilationUnit;
  }

  @Override
  public String getDescription() {
    return name == null ? "<name>" : name;
  }

  public TypeDeclaration getEnclosingDeclaration() {
    return enclosing;
  }

  public String getName() {
    return name;
  }

  public void setCompilationUnit(CompilationUnit unit) {
    this.compilationUnit = unit;
  }

  public void setEnclosingDeclaration(TypeDeclaration enclosing) {
    this.enclosing = enclosing;
  }

  public void setName(String name) {
    this.name = name;
  }
}
