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

package de.sormuras.beethoven.unit;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import de.sormuras.beethoven.Listing;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import javax.lang.model.element.Modifier;

/** Default {@code Modifier} set support. */
public interface Modifiable {

  /** Convert an integer consisting of modification bits into a set of {@code Modifier}s. */
  static Set<Modifier> modifiers(int mod) {
    Set<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
    if (java.lang.reflect.Modifier.isAbstract(mod)) {
      modifiers.add(Modifier.ABSTRACT);
    }
    if (java.lang.reflect.Modifier.isFinal(mod)) {
      modifiers.add(Modifier.FINAL);
    }
    if (java.lang.reflect.Modifier.isNative(mod)) {
      modifiers.add(Modifier.NATIVE);
    }
    if (java.lang.reflect.Modifier.isPrivate(mod)) {
      modifiers.add(Modifier.PRIVATE);
    }
    if (java.lang.reflect.Modifier.isProtected(mod)) {
      modifiers.add(Modifier.PROTECTED);
    }
    if (java.lang.reflect.Modifier.isPublic(mod)) {
      modifiers.add(Modifier.PUBLIC);
    }
    if (java.lang.reflect.Modifier.isStatic(mod)) {
      modifiers.add(Modifier.STATIC);
    }
    if (java.lang.reflect.Modifier.isStrict(mod)) {
      modifiers.add(Modifier.STRICTFP);
    }
    if (java.lang.reflect.Modifier.isSynchronized(mod)) {
      modifiers.add(Modifier.SYNCHRONIZED);
    }
    if (java.lang.reflect.Modifier.isTransient(mod)) {
      modifiers.add(Modifier.TRANSIENT);
    }
    if (java.lang.reflect.Modifier.isVolatile(mod)) {
      modifiers.add(Modifier.VOLATILE);
    }
    return modifiers;
  }

  /** Add modifier to the set. */
  default void addModifier(Modifier modifier) {
    validateModifiers(modifier);
    getModifiers().add(modifier);
  }

  /** Add variable array of modifier names to the set. */
  default void addModifier(String... modifiers) {
    asList(modifiers).forEach(name -> addModifier(Modifier.valueOf(name.toUpperCase())));
  }

  /** Add collection of modifiers to the set. */
  default void addModifiers(Collection<Modifier> modifiers) {
    validateModifiers(modifiers.toArray(new Modifier[modifiers.size()]));
    getModifiers().addAll(modifiers);
  }

  /** Add collection of modifiers to the set. */
  default void addModifiers(int mod) {
    addModifiers(modifiers(mod));
  }

  /** Add variable array of modifiers to the set. */
  default void addModifiers(Modifier... modifiers) {
    validateModifiers(modifiers);
    getModifiers().addAll(asList(modifiers));
  }

  /** Return set of modifiers indicating if the caller might mutate the set. */
  Set<Modifier> getModifiers();

  /**
   * Returns all modifiers that are applicable to this element kind.
   *
   * @return All modifiers defined in {@code Modifier}.
   */
  default Set<Modifier> getModifierValidationSet() {
    return EnumSet.allOf(Modifier.class);
  }

  /** Return {@code true} if modifier set is not empty, else {@code false}. */
  boolean isModified();

  /** Return {@code true} if {@code Modifier#PUBLIC} is part of modifier set, else {@code false}. */
  default boolean isPublic() {
    return isModified() && getModifiers().contains(Modifier.PUBLIC);
  }

  /** Return {@code true} if {@code Modifier#STATIC} is part of modifier set, else {@code false}. */
  default boolean isStatic() {
    return isModified() && getModifiers().contains(Modifier.STATIC);
  }

  /** Replace current modifiers by with new ones. */
  default void setModifiers(int mod) {
    getModifiers().clear();
    addModifiers(mod);
  }

  default void setModifiers(Set<Modifier> modifiers) {
    getModifiers().clear();
    addModifiers(modifiers);
  }

  /** Replace current modifiers by with new ones. */
  default void setModifiers(Modifier... modifiers) {
    getModifiers().clear();
    addModifiers(modifiers);
  }

  /** Apply modifiers inline. */
  default Listing applyModifiers(Listing listing) {
    if (isModified()) {
      getModifiers().forEach(m -> listing.add(m.name().toLowerCase()).add(' '));
    }
    return listing;
  }

  /**
   * Tests whether modifiers are applicable to this element kind.
   *
   * @param modifiers Modifiers to test.
   * @see #getModifierValidationSet()
   */
  default void validateModifiers(Modifier... modifiers) {
    Set<Modifier> set = getModifierValidationSet();
    for (Modifier modifier : modifiers) {
      requireNonNull(modifier, "null is not a valid modifier");
      if (!set.contains(modifier)) {
        String message = "Modifier %s not allowed at instance of %s, valid modifier(s): %s";
        throw new IllegalArgumentException(String.format(message, modifier, getClass(), set));
      }
    }
  }
}
