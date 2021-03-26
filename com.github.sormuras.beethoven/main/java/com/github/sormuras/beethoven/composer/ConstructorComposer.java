package com.github.sormuras.beethoven.composer;

import com.github.sormuras.beethoven.unit.ClassDeclaration;
import com.github.sormuras.beethoven.unit.MethodDeclaration;
import java.util.function.Function;
import javax.lang.model.element.Modifier;

public class ConstructorComposer implements Function<ClassDeclaration, MethodDeclaration> {

  @Override
  public MethodDeclaration apply(ClassDeclaration declaration) {
    MethodDeclaration constructor = declaration.declareConstructor();
    constructor.setModifiers(Modifier.PUBLIC);
    declaration
        .getFields()
        .forEach(
            field -> {
              constructor.declareParameter(field.getType(), field.getName());
              constructor.addStatement("this.{{$:0}} = {{$:0}}", field.getName());
            });
    return constructor;
  }
}
