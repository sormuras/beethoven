/*
 * Copyright 2017 Christian Stein
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package de.sormuras.beethoven.type;

import de.sormuras.beethoven.Listable;
import de.sormuras.beethoven.Listing;
import java.util.Objects;

/**
 * Type arguments may be either reference types or wildcards.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.5.1">JLS 4.5</a>
 */
public class TypeArgument implements Listable {

  public static TypeArgument argument(Class<?> argument) {
    return argument(Type.type(argument));
  }

  /** Initializes this {@code TypeArgument} instance. */
  public static TypeArgument argument(Type argument) {
    Objects.requireNonNull(argument, "argument");
    if (argument instanceof WildcardType) {
      return new TypeArgument((WildcardType) argument);
    }
    if (argument instanceof ReferenceType) {
      return new TypeArgument((ReferenceType) argument);
    }
    throw new AssertionError("Neither reference nor wildcard type: " + argument);
  }

  private final ReferenceType reference;
  private final WildcardType wildcard;

  TypeArgument(ReferenceType reference) {
    this.reference = reference;
    this.wildcard = null;
  }

  TypeArgument(WildcardType wildcard) {
    this.reference = null;
    this.wildcard = wildcard;
  }

  @Override
  public Listing apply(Listing listing) {
    return getArgument().apply(listing);
  }

  public Type getArgument() {
    return reference != null ? reference : wildcard;
  }

  public ReferenceType getReference() {
    return reference;
  }

  public WildcardType getWildcard() {
    return wildcard;
  }

  @Override
  public String toString() {
    return "TypeArgument{" + getArgument() + "}";
  }
}
