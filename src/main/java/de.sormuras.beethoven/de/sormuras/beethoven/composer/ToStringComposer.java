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

package de.sormuras.beethoven.composer;

import de.sormuras.beethoven.unit.ClassDeclaration;
import de.sormuras.beethoven.unit.FieldDeclaration;
import de.sormuras.beethoven.unit.MethodDeclaration;
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
