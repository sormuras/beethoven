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

import de.sormuras.beethoven.Listable;
import de.sormuras.beethoven.Listing;
import de.sormuras.beethoven.type.Type;
import java.lang.annotation.ElementType;

/**
 * Constant field.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.3">JLS 9.3</a>
 */
public class ConstantDeclaration extends NamedMember {

  private Listable initializer;
  private Type type;

  @Override
  public Listing apply(Listing listing) {
    listing.newline();
    applyAnnotations(listing);
    listing.add(getType()).add(' ').add(getName()).add(" = ").add(getInitializer());
    listing.add(';').newline();
    return listing;
  }

  @Override
  public ElementType getAnnotationsTarget() {
    return ElementType.FIELD;
  }

  public Listable getInitializer() {
    return initializer;
  }

  public Type getType() {
    return type;
  }

  public void setInitializer(Listable initializer) {
    this.initializer = initializer;
  }

  public void setType(Type type) {
    this.type = type;
  }
}
