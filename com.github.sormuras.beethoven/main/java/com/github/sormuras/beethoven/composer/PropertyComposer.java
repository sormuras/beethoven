package com.github.sormuras.beethoven.composer;

import static java.util.Objects.requireNonNull;

import com.github.sormuras.beethoven.Listable;
import com.github.sormuras.beethoven.Name;
import com.github.sormuras.beethoven.type.Type;
import com.github.sormuras.beethoven.type.VoidType;
import com.github.sormuras.beethoven.unit.ClassDeclaration;
import com.github.sormuras.beethoven.unit.FieldDeclaration;
import com.github.sormuras.beethoven.unit.MethodDeclaration;
import java.util.Objects;
import java.util.function.UnaryOperator;
import javax.lang.model.element.Modifier;

public class PropertyComposer implements UnaryOperator<ClassDeclaration> {

  private Type type = null;
  private String name = null;
  private boolean setterAvailable = true;
  private boolean setterReturnsThis = false;
  private boolean setterRequiresNonNullValue = false;
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
      if (isSetterRequiresNonNullValue()) {
        Name requireNonNull = Name.reflect(Objects.class, "requireNonNull");
        String message = "Property `" + name + "` requires non-null instance of " + type.list();
        setter.addStatement(
            "this.{{$:0}} = {{N:1}}({{$:0}}, {{S:2}})", name, requireNonNull, message);
      } else {
        setter.addStatement("this.{{$}} = {{$}}", name, name);
      }
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
    return setType(Type.type(type));
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

  public boolean isSetterRequiresNonNullValue() {
    return setterRequiresNonNullValue;
  }

  public PropertyComposer setSetterRequiresNonNullValue(boolean setterRequiresNonNullValue) {
    this.setterRequiresNonNullValue = setterRequiresNonNullValue;
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
