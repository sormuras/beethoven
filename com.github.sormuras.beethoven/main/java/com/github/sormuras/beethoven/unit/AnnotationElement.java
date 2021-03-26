package com.github.sormuras.beethoven.unit;

import com.github.sormuras.beethoven.Listable;
import com.github.sormuras.beethoven.Listing;
import com.github.sormuras.beethoven.type.Type;
import java.lang.annotation.ElementType;

/**
 * The body of an annotation type declaration may contain method declarations, each of which defines
 * an <b>element</b> of the annotation type. An annotation type has no elements other than those
 * defined by the methods it explicitly declares.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.6.1">JSL
 *     9.6.1</a>
 */
public class AnnotationElement extends NamedMember {

  private Listable defaultValue;
  private Type returnType;

  @Override
  public Listing apply(Listing listing) {
    listing.newline();
    applyAnnotations(listing);
    listing.add(getReturnType()).add(' ').add(getName()).add("()");
    if (defaultValue != null) {
      listing.add(' ').add("default").add(' ').add(getDefaultValue());
    }
    listing.add(';').newline();
    return listing;
  }

  @Override
  public ElementType getAnnotationsTarget() {
    return ElementType.METHOD;
  }

  public Listable getDefaultValue() {
    return defaultValue;
  }

  public Type getReturnType() {
    return returnType;
  }

  public void setDefaultValue(Listable defaultValue) {
    this.defaultValue = defaultValue;
  }

  public void setReturnType(Type type) {
    this.returnType = type;
  }
}
