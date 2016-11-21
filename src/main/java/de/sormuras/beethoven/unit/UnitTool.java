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

import de.sormuras.beethoven.Annotation;
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
