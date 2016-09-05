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

package com.github.sormuras.beethoven;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import java.lang.annotation.ElementType;
import java.util.List;

/** Base {@link Annotation}-collecting implementation. */
public abstract class Annotated implements Listable {

  private final List<Annotation> annotations;
  private final Listable annotationsListable;
  private final Listable annotationsSeparator;

  /** Initialize this instance. */
  protected Annotated(List<Annotation> annotations) {
    this.annotations = unmodifiableList(annotations.isEmpty() ? emptyList() : annotations);
    this.annotationsSeparator = buildAnnotationsSeparator();
    this.annotationsListable = annotations.isEmpty() ? Listable.IDENTITY : this::applyAnnotations;
  }

  /** Add all annotations to the given {@link Listing} instance. */
  protected Listing applyAnnotations(Listing listing) {
    return listing.add(annotations, annotationsSeparator).add(annotationsSeparator);
  }

  /**
   * Build listable annotation separator.
   *
   * <p>This implementation creates a separator depending on the annotation target element type.
   */
  protected Listable buildAnnotationsSeparator() {
    ElementType target = getAnnotationsTarget();
    boolean inline =
        target == ElementType.TYPE_PARAMETER
            || target == ElementType.TYPE_USE
            || target == ElementType.PARAMETER;
    return inline ? Listable.SPACE : Listable.NEWLINE;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    return hashCode() == obj.hashCode();
  }

  public List<Annotation> getAnnotations() {
    return annotations;
  }

  /** Return listable source snippet for all annotations. */
  public Listable getAnnotationsListable() {
    return annotationsListable;
  }

  /** Return listable separator used to textually separate annotations from each other. */
  public Listable getAnnotationsSeparator() {
    return annotationsSeparator;
  }

  /** The designated target element type of the annotations. */
  public abstract ElementType getAnnotationsTarget();

  /** Used by {@link #toString()} as an instance description hint. */
  public String getDescription() {
    return list() + " // 0x" + Integer.toHexString(System.identityHashCode(this));
  }

  @Override
  public int hashCode() {
    return list().hashCode();
  }

  /** Return {@code true} if there is at least one annotation available. */
  public boolean isAnnotated() {
    return !annotations.isEmpty();
  }

  @Override
  public String toString() {
    String className = getClass().getSimpleName();
    String description = getDescription();
    return String.format("%s {@%d %s}", className, getAnnotations().size(), description);
  }
}
