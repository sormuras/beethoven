package com.github.sormuras.beethoven.type;

import com.github.sormuras.beethoven.Annotation;
import com.github.sormuras.beethoven.Listing;
import java.lang.annotation.ElementType;
import java.util.Collections;
import java.util.List;
import java.util.function.IntFunction;

/**
 * A type variable is an unqualified identifier used as a type in class, interface, method, and
 * constructor bodies.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.4">JLS 4.4</a>
 */
public class TypeVariable extends ReferenceType {

  public static TypeVariable variable(String identifier) {
    return variable(Collections.emptyList(), identifier);
  }

  public static TypeVariable variable(List<Annotation> annotations, String identifier) {
    if (identifier.isEmpty()) {
      throw new IllegalArgumentException("TypeVariable identifier must not be empty!");
    }
    return new TypeVariable(annotations, identifier);
  }

  private final String identifier;

  TypeVariable(List<Annotation> annotations, String identifier) {
    super(annotations);
    this.identifier = identifier;
  }

  @Override
  public TypeVariable annotated(IntFunction<List<Annotation>> annotationsSupplier) {
    return new TypeVariable(annotationsSupplier.apply(0), identifier);
  }

  @Override
  public Listing apply(Listing listing) {
    return applyAnnotations(listing).add(getIdentifier());
  }

  @Override
  public String binary() {
    throw new UnsupportedOperationException("Type variables have no binary class name.");
  }

  @Override
  public ElementType getAnnotationsTarget() {
    return ElementType.TYPE_PARAMETER;
  }

  public String getIdentifier() {
    return identifier;
  }
}
