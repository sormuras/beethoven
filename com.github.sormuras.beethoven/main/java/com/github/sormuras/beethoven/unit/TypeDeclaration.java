package com.github.sormuras.beethoven.unit;

import static java.util.Collections.addAll;

import com.github.sormuras.beethoven.Name;
import com.github.sormuras.beethoven.type.ClassType;
import com.github.sormuras.beethoven.type.Type;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.Modifier;

public abstract class TypeDeclaration extends ClassMember implements DeclarationContainer {

  private List<TypeDeclaration> declarations = Collections.emptyList();
  private final List<MethodDeclaration> methods = new ArrayList<>();

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
  public <T extends TypeDeclaration> T declare(T declaration, String name, Modifier... modifiers) {
    DeclarationContainer.super.declare(declaration, name, modifiers);
    declaration.setEnclosingDeclaration(this);
    declaration.setCompilationUnit(getCompilationUnit());
    return declaration;
  }

  /** Declare new method. */
  public MethodDeclaration declareMethod(Class<?> type, String name, Modifier... modifiers) {
    return declareMethod(Type.type(type), name, modifiers);
  }

  /** Declare new method. */
  public MethodDeclaration declareMethod(Type type, String name, Modifier... modifiers) {
    MethodDeclaration declaration = new MethodDeclaration();
    declaration.setReturnType(type);
    declaration.setName(name);
    if (modifiers.length > 0) {
      declaration.setModifiers(modifiers);
    }
    return declareMethod(declaration);
  }

  /** Add passed method declaration to list of declared methods. */
  public MethodDeclaration declareMethod(MethodDeclaration declaration) {
    declaration.setCompilationUnit(getCompilationUnit());
    declaration.setEnclosingDeclaration(this);
    int index = getMethods().size();
    // keep constructors in declaration order
    if (declaration.isConstructor()) {
      index = (int) getMethods().stream().filter(MethodDeclaration::isConstructor).count();
    }
    getMethods().add(index, declaration);
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

  public List<MethodDeclaration> getMethods() {
    return methods;
  }

  @Override
  public boolean isEmpty() {
    return isDeclarationsEmpty() && getMethods().isEmpty();
  }

  public boolean isDeclarationsEmpty() {
    return declarations.isEmpty();
  }

  /** Return simple {@link Name} representation of this type declaration. */
  public Name toName() {
    List<String> identifiers = new ArrayList<>();
    CompilationUnit unit = getCompilationUnit();
    if (unit != null && !unit.getPackageDeclaration().isUnnamed()) {
      addAll(identifiers, Name.DOT.split(unit.getPackageName()));
    }
    int packageLevel = identifiers.size();
    for (NamedMember member = this; member != null; member = member.getEnclosingDeclaration()) {
      identifiers.add(packageLevel, member.getName());
    }
    return Name.name(packageLevel, identifiers);
  }

  public ClassType toType() {
    return ClassType.type(toName());
  }
}
