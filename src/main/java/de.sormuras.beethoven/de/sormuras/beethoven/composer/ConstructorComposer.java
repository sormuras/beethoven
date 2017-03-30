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
import de.sormuras.beethoven.unit.MethodDeclaration;
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
