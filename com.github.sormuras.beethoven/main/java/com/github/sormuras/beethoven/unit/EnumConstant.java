package com.github.sormuras.beethoven.unit;

import com.github.sormuras.beethoven.Listable;
import com.github.sormuras.beethoven.Listing;
import java.lang.annotation.ElementType;

/**
 * An enum constant defines an instance of the enum type.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.9.1">JLS
 *     8.9.1</a>
 */
public class EnumConstant extends NamedMember {

  private Listable arguments;
  private ClassDeclaration body;

  @Override
  public Listing apply(Listing listing) {
    listing.newline();
    // {EnumConstantModifier}
    applyAnnotations(listing);
    // Identifier
    listing.add(getName());
    // [( [ArgumentList] )]
    if (arguments != null) {
      listing.add('(');
      listing.add(getArguments());
      listing.add(')');
    }
    // [ClassBody]
    if (getBody() != null) {
      getBody().applyClassBody(listing);
    }
    return listing;
  }

  @Override
  public ElementType getAnnotationsTarget() {
    return ElementType.TYPE;
  }

  public Listable getArguments() {
    return arguments;
  }

  public ClassDeclaration getBody() {
    return body;
  }

  public void setArguments(Listable arguments) {
    this.arguments = arguments;
  }

  public void setBody(ClassDeclaration body) {
    this.body = body;
  }
}
