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

package de.sormuras.beethoven.unit;

import de.sormuras.beethoven.Listing;

/**
 * An <b>instance</b> initializer declared in a class is executed when an instance of the class is
 * created and a <b>static</b> initializer declared in a class is executed when the class is
 * initialized.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.6">JLS 8.6</a>
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.7">JLS 8.7</a>
 */
public class Initializer extends Block {

  private ClassDeclaration enclosing;
  private boolean isStatic = false;

  @Override
  public Listing apply(Listing listing) {
    if (isStatic()) {
      listing.add("static ");
    }
    return super.apply(listing);
  }

  public ClassDeclaration getEnclosing() {
    return enclosing;
  }

  public void setEnclosing(ClassDeclaration enclosing) {
    this.enclosing = enclosing;
  }

  public void setStatic(boolean isStatic) {
    this.isStatic = isStatic;
  }

  public boolean isStatic() {
    return isStatic;
  }
}
