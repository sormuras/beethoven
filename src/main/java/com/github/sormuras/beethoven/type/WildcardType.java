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

import com.github.sormuras.beethoven.Listing;
import java.lang.annotation.ElementType;
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
  public static WildcardType subtypeOf(Type upperBound) {
    WildcardType wildcard = new WildcardType();
    wildcard.setBoundExtends((ReferenceType) upperBound);
    return wildcard;
  }

  /** {@code ? extends java.lang.Runnable}. */
  public static WildcardType subtypeOf(java.lang.reflect.Type upperBound) {
    return subtypeOf(Type.type(upperBound));
  }

  /** {@code ? super java.lang.String}. */
  public static WildcardType supertypeOf(Type lowerBound) {
    WildcardType wildcard = new WildcardType();
    wildcard.setBoundSuper((ReferenceType) lowerBound);
    return wildcard;
  }

  /** {@code ? super java.lang.String}. */
  public static WildcardType supertypeOf(java.lang.reflect.Type lowerBound) {
    return supertypeOf(Type.type(lowerBound));
  }

  private ReferenceType boundExtends = ClassType.of(Object.class);
  private ReferenceType boundSuper = null;

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

  /** Set upper bound, read {@code extends}, type. */
  public void setBoundExtends(ReferenceType boundExtends) {
    this.boundExtends = boundExtends;
    this.boundSuper = null;
  }

  /** Set lower bound, read {@code super}, type. */
  public void setBoundSuper(ReferenceType boundSuper) {
    this.boundExtends = ClassType.of(Object.class);
    this.boundSuper = boundSuper;
  }
}
