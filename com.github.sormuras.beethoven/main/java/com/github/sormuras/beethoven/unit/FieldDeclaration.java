package com.github.sormuras.beethoven.unit;

import com.github.sormuras.beethoven.Listable;
import com.github.sormuras.beethoven.Listing;
import com.github.sormuras.beethoven.type.Type;
import java.lang.annotation.ElementType;

/**
 * The variables of a class type are introduced by field declarations.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.3">JLS 8.3</a>
 */
public class FieldDeclaration extends ClassMember {

  private Listable initializer;
  private Type type;

  @Override
  public Listing apply(Listing listing) {
    listing.newline();
    applyAnnotations(listing);
    applyModifiers(listing);
    listing.add(getType());
    listing.add(' ');
    listing.add(getName());
    if (initializer != null) {
      listing.add(" = ").add(getInitializer());
    }
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
