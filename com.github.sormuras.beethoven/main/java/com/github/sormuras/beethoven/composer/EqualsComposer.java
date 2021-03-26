package com.github.sormuras.beethoven.composer;

import com.github.sormuras.beethoven.Listing;
import com.github.sormuras.beethoven.unit.ClassDeclaration;
import com.github.sormuras.beethoven.unit.MethodDeclaration;
import java.util.function.Function;
import javax.lang.model.element.Modifier;

public class EqualsComposer implements Function<ClassDeclaration, MethodDeclaration> {

  private static final String OTHER = "other";

  @Override
  public MethodDeclaration apply(ClassDeclaration declaration) {
    MethodDeclaration method = declaration.declareMethod(boolean.class, "equals");
    method.addAnnotation(Override.class);
    method.setModifiers(Modifier.PUBLIC);
    method.declareParameter(Object.class, OTHER);
    method.addStatement(this::apply);
    return method;
  }

  public Listing apply(Listing listing) {
    listing.add("if (this == ").add(OTHER).add(") {").newline();
    listing.indent(1).add("return true;").newline().indent(-1);
    listing.add("}").newline();
    listing
        .add("if (")
        .add(OTHER)
        .add(" == null || getClass() != ")
        .add(OTHER)
        .add(".getClass()) {")
        .newline();
    listing.indent(1).add("return false;").newline().indent(-1);
    listing.add("}").newline();
    listing.add("return hashCode() == ").add(OTHER).add(".hashCode()");
    return listing;
  }
}
