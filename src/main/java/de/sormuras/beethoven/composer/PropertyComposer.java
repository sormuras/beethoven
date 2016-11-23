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

package de.sormuras.beethoven.composer;

import static java.util.Objects.requireNonNull;

import de.sormuras.beethoven.Listable;
import de.sormuras.beethoven.Name;
import de.sormuras.beethoven.type.Type;
import de.sormuras.beethoven.type.VoidType;
import de.sormuras.beethoven.unit.ClassDeclaration;
import de.sormuras.beethoven.unit.FieldDeclaration;
import de.sormuras.beethoven.unit.MethodDeclaration;
import java.util.Objects;
import java.util.function.UnaryOperator;
import javax.lang.model.element.Modifier;

public class PropertyComposer implements UnaryOperator<ClassDeclaration> {

  private Type type = null;
  private String name = null;
  private boolean setterAvailable = true;
  private boolean setterReturnsThis = false;
  private boolean fieldFinal = false;
  private Listable fieldInitializer = null;

  @Override
  public ClassDeclaration apply(ClassDeclaration declaration) {
    Type type = requireNonNull(getType(), "Property type must not be null!");
    String name = requireNonNull(getName(), "Property name must not be null!");
    // field
    FieldDeclaration field = declaration.declareField(type, name);
    field.setModifiers(Modifier.PRIVATE);
    if (isFieldFinal()) {
      field.addModifiers(Modifier.FINAL);
    }
    if (getFieldInitializer() != null) {
      field.setInitializer(getFieldInitializer());
    }
    // uppercase first character
    String property = name.substring(0, 1).toUpperCase() + name.substring(1);
    // getter
    MethodDeclaration getter = declaration.declareMethod(type, "get" + property);
    getter.setModifiers(Modifier.PUBLIC);
    getter.addStatement("return {{$}}", name);
    // optional setter
    if (isSetterAvailable()) {
      Type returnType = isSetterReturnsThis() ? declaration.toType() : VoidType.instance();
      MethodDeclaration setter = declaration.declareMethod(returnType, "set" + property);
      setter.setModifiers(Modifier.PUBLIC);
      setter.declareParameter(type, name);
      // setter.addStatement("this.{{$}} = {{$}}", name, name);
      Name requireNonNull = Name.reflect(Objects.class, "requireNonNull");
      String message = "Property `" + name + "` requires non `null` values!";
      setter.addStatement(
          "this.{{$:0}} = {{N:1}}({{$:0}}, {{S:2}})", name, requireNonNull, message);
      if (isSetterReturnsThis()) {
        setter.addStatement("return this");
      }
    }
    return declaration;
  }

  public Type getType() {
    return type;
  }

  public PropertyComposer setType(java.lang.reflect.Type type) {
    this.type = Type.type(type);
    return this;
  }

  public PropertyComposer setType(Type type) {
    this.type = type;
    return this;
  }

  public String getName() {
    return name;
  }

  public PropertyComposer setName(String name) {
    this.name = name;
    return this;
  }

  public boolean isSetterAvailable() {
    return setterAvailable;
  }

  public PropertyComposer setSetterAvailable(boolean setterAvailable) {
    this.setterAvailable = setterAvailable;
    return this;
  }

  public boolean isSetterReturnsThis() {
    return setterReturnsThis;
  }

  public PropertyComposer setSetterReturnsThis(boolean setterReturnsThis) {
    this.setterReturnsThis = setterReturnsThis;
    return this;
  }

  public boolean isFieldFinal() {
    return fieldFinal;
  }

  public PropertyComposer setFieldFinal(boolean fieldFinal) {
    this.fieldFinal = fieldFinal;
    return this;
  }

  public Listable getFieldInitializer() {
    return fieldInitializer;
  }

  public PropertyComposer setFieldInitializer(Listable fieldInitializer) {
    this.fieldInitializer = fieldInitializer;
    return this;
  }
}
