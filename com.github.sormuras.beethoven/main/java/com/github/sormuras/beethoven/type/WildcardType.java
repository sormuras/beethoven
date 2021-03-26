package com.github.sormuras.beethoven.type;

import com.github.sormuras.beethoven.Annotation;
import com.github.sormuras.beethoven.Listing;
import java.lang.annotation.ElementType;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;

/**
 * Wildcards are useful in situations where only partial knowledge about the type parameter is
 * required.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-Wildcard">JLS
 *     4</a>
 */
public class WildcardType extends Type {

  /** {@code @Tag ? extends java.lang.Runnable}. */
  public static WildcardType extend(List<Annotation> annotations, ReferenceType upperBound) {
    return new WildcardType(annotations, upperBound, null);
  }

  /** {@code ? extends java.lang.Runnable}. */
  public static WildcardType extend(java.lang.reflect.Type upperBound) {
    return extend(Collections.emptyList(), (ReferenceType) type(upperBound));
  }

  /** {@code @Tag ? super java.lang.String}. */
  public static WildcardType supertype(List<Annotation> annotations, ReferenceType lowerBound) {
    return new WildcardType(annotations, ClassType.OBJECT, lowerBound);
  }

  /** {@code ? super java.lang.String}. */
  public static WildcardType supertype(java.lang.reflect.Type lowerBound) {
    return supertype(Collections.emptyList(), (ReferenceType) type(lowerBound));
  }

  /** Unbounded (simple) wildcard, namely the {@code "?"} sign. */
  public static WildcardType wildcard() {
    return wildcard(Collections.emptyList());
  }

  /** Unbounded (simple) annotated wildcard, like {@code "@Tag ?"}. */
  public static WildcardType wildcard(List<Annotation> annotations) {
    return new WildcardType(annotations, ClassType.OBJECT, null);
  }

  private final ReferenceType boundExtends;
  private final ReferenceType boundSuper;

  WildcardType(List<Annotation> annotations, ReferenceType boundExtends, ReferenceType boundSuper) {
    super(annotations);
    this.boundExtends = boundExtends;
    this.boundSuper = boundSuper;
  }

  @Override
  public WildcardType annotated(IntFunction<List<Annotation>> annotationsSupplier) {
    return new WildcardType(annotationsSupplier.apply(0), boundExtends, boundSuper);
  }

  @Override
  public Listing apply(Listing listing) {
    applyAnnotations(listing);
    listing.add('?');
    if (!getBoundExtends().isJavaLangObject()) {
      return listing.add(" extends ").add(getBoundExtends());
    }
    return getBoundSuper().map(bound -> listing.add(" super ").add(bound)).orElse(listing);
  }

  @Override
  public String binary() {
    throw new UnsupportedOperationException("Wildcards have no binary class name.");
  }

  @Override
  public ElementType getAnnotationsTarget() {
    return ElementType.TYPE_PARAMETER;
  }

  public ReferenceType getBoundExtends() {
    return boundExtends;
  }

  public Optional<ReferenceType> getBoundSuper() {
    return Optional.ofNullable(boundSuper);
  }
}
