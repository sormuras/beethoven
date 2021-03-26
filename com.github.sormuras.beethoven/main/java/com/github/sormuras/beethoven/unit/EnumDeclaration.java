package com.github.sormuras.beethoven.unit;

import com.github.sormuras.beethoven.Listable;
import com.github.sormuras.beethoven.Listing;
import java.util.ArrayList;
import java.util.List;

/**
 * An enum declaration specifies a new enum type, a special kind of class type.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.9">JLS 8.9</a>
 */
public class EnumDeclaration extends ClassDeclaration {

  private final List<EnumConstant> constants = new ArrayList<>();

  @Override
  public Listing apply(Listing listing) {
    if (!isLocal()) {
      listing.newline();
    }
    // {ClassModifier}
    applyAnnotations(listing);
    applyModifiers(listing);
    // enum Identifier
    listing.add("enum").add(' ').add(getName());
    // [Superinterfaces]
    if (!isInterfacesEmpty()) {
      listing.add(" implements ").addAll(getInterfaces(), ", ");
    }
    listing.add(' ').add('{').newline();
    listing.indent(1);
    // [EnumConstantList]
    listing.addAll(getConstants(), l -> l.add(',').newline());
    if (!isEmpty()) {
      listing.add(';');
      listing.newline();
      applyClassBodyElements(listing);
    } else if (!getConstants().isEmpty()) {
      listing.newline();
    }
    listing.indent(-1).add('}').newline();
    return listing;
  }

  /** Add new enum constant. */
  public EnumConstant declareConstant(String name) {
    return declareConstant(name, null);
  }

  /** Add new enum constant. */
  public EnumConstant declareConstant(String name, Listable arguments) {
    return declareConstant(name, arguments, null);
  }

  /** Add new enum constant. */
  public EnumConstant declareConstant(String name, Listable arguments, ClassDeclaration body) {
    EnumConstant constant = new EnumConstant();
    constant.setEnclosingDeclaration(this);
    constant.setCompilationUnit(getCompilationUnit());
    constant.setName(name);
    constant.setArguments(arguments);
    constant.setBody(body);
    getConstants().add(constant);
    return constant;
  }

  public List<EnumConstant> getConstants() {
    return constants;
  }
}
