package com.github.sormuras.beethoven.unit;

import com.github.sormuras.beethoven.Listing;

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
