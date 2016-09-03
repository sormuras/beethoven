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

package com.github.sormuras.beethoven.type;

import com.github.sormuras.beethoven.Annotation;
import com.github.sormuras.beethoven.Listing;
import java.lang.annotation.ElementType;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Wildcards are useful in situations where only partial knowledge about the type parameter is
 * required.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-Wildcard">JLS
 * 4</a>
 */
public class WildcardType extends Type {

  /** {@code ? extends java.lang.Runnable}. */
  public static WildcardType subtype(List<Annotation> annotations, ReferenceType upperBound) {
    return new WildcardType(annotations, upperBound, true);
  }

  /** {@code ? extends java.lang.Runnable}. */
  public static WildcardType subtype(java.lang.reflect.Type upperBound) {
    return subtype(Collections.emptyList(), (ReferenceType) Type.type(upperBound));
  }

  /** {@code ? super java.lang.String}. */
  public static WildcardType supertype(List<Annotation> annotations, ReferenceType lowerBound) {
    return new WildcardType(annotations, lowerBound, false);
  }

  /** {@code ? super java.lang.String}. */
  public static WildcardType supertype(java.lang.reflect.Type lowerBound) {
    return supertype(Collections.emptyList(), (ReferenceType) Type.type(lowerBound));
  }

  public static WildcardType wild() {
    return wild(Collections.emptyList());
  }

  public static WildcardType wild(List<Annotation> annotations) {
    return new WildcardType(annotations, null, false);
  }

  private final ReferenceType boundExtends;
  private final ReferenceType boundSuper;

  WildcardType(List<Annotation> annotations, ReferenceType bound, boolean upper) {
    super(annotations);
    this.boundExtends = upper ? bound : ClassType.type(Object.class);
    this.boundSuper = upper ? null : bound;
  }

  WildcardType(List<Annotation> annotations, ReferenceType boundExtends, ReferenceType boundSuper) {
    super(annotations);
    this.boundExtends = boundExtends;
    this.boundSuper = boundSuper;
  }

  @Override
  public Listing apply(Listing listing) {
    listing.add(toAnnotationsListable());
    listing.add('?');
    if (!getBoundExtends().isJavaLangObject()) {
      return listing.add(" extends ").add(getBoundExtends());
    }
    Optional<ReferenceType> bound = getBoundSuper();
    if (bound.isPresent()) {
      return listing.add(" super ").add(bound.get());
    }
    return listing;
  }

  @Override
  public ElementType getAnnotationTarget() {
    return ElementType.TYPE_PARAMETER;
  }

  public ReferenceType getBoundExtends() {
    return boundExtends;
  }

  public Optional<ReferenceType> getBoundSuper() {
    return Optional.ofNullable(boundSuper);
  }

  @Override
  public WildcardType toAnnotatedType(List<Annotation> annotations) {
    return new WildcardType(annotations, boundExtends, boundSuper);
  }
}
