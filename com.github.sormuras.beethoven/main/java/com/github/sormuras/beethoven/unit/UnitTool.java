package com.github.sormuras.beethoven.unit;

import com.github.sormuras.beethoven.Annotation;
import javax.lang.model.element.Modifier;

public interface UnitTool {

  static MethodParameter parameter(MethodParameter source, boolean copyAnnotations) {
    MethodParameter parameter = new MethodParameter();
    if (copyAnnotations) {
      parameter.addAnnotations(source.getAnnotations());
    }
    parameter.setFinal(source.isFinal());
    parameter.setType(source.getType());
    parameter.setName(source.getName());
    parameter.setVariable(source.isVariable());
    return parameter;
  }

  static MethodDeclaration override(MethodDeclaration source, boolean copyAnnotations) {
    MethodDeclaration declaration = new MethodDeclaration();
    if (copyAnnotations) {
      declaration.addAnnotations(source.getAnnotations());
      declaration.getAnnotations().remove(Annotation.annotation(Override.class));
    }
    declaration.addAnnotation(Override.class);
    declaration.getModifiers().addAll(source.getModifiers());
    declaration.getModifiers().remove(Modifier.ABSTRACT);
    declaration.setName(source.getName());
    declaration.setReturnType(source.getReturnType());
    if (!source.getParameters().isEmpty()) {
      source.getParameters().forEach(p -> declaration.addParameter(parameter(p, copyAnnotations)));
      assert declaration.isVarArgs() == source.isVarArgs();
    }
    declaration.getThrows().addAll(source.getThrows());
    return declaration;
  }
}
