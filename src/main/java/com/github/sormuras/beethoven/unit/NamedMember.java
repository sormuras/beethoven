package com.github.sormuras.beethoven.unit;

/** Named, annotatable and enclose-able member base class. */
public abstract class NamedMember extends Annotatable {

  private CompilationUnit compilationUnit = null;
  private TypeDeclaration enclosing = null;
  private String name = null;

  public CompilationUnit getCompilationUnit() {
    return compilationUnit;
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
