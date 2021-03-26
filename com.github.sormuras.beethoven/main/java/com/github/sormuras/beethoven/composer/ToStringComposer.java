package com.github.sormuras.beethoven.composer;

import com.github.sormuras.beethoven.unit.ClassDeclaration;
import com.github.sormuras.beethoven.unit.FieldDeclaration;
import com.github.sormuras.beethoven.unit.MethodDeclaration;
import java.util.Spliterator;
import java.util.function.Function;
import javax.lang.model.element.Modifier;

public class ToStringComposer implements Function<ClassDeclaration, MethodDeclaration> {

  @Override
  public MethodDeclaration apply(ClassDeclaration declaration) {
    MethodDeclaration method = declaration.declareMethod(String.class, "toString");
    method.addAnnotation(Override.class);
    method.setModifiers(Modifier.PUBLIC);
    if (declaration.getFields().isEmpty()) {
      method.addStatement("return super.toString()");
      return method;
    }
    method.addStatement("StringBuilder builder = new StringBuilder()");
    method.addStatement("builder.append({{S}})", declaration.getName());
    method.addStatement("builder.append('[')");
    String first = "builder.append({{S:0}}).append('=').append({{$:0}})";
    Spliterator<FieldDeclaration> fields = declaration.getFields().spliterator();
    fields.tryAdvance(field -> method.addStatement(first, field.getName()));
    String line = "builder.append(\", \").append({{S:0}}).append('=').append({{$:0}})";
    fields.forEachRemaining(field -> method.addStatement(line, field.getName()));
    method.addStatement("builder.append(']')");
    method.addStatement("return builder.toString()");
    return method;
  }
}
