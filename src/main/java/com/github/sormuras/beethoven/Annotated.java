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

import java.lang.annotation.ElementType;
import java.util.Collections;
import java.util.List;

/** Base {@link Annotation}-collecting implementation. */
public abstract class Annotated implements Listable {

  private final List<Annotation> annotations;

  public Annotated(List<Annotation> annotations) {
    this.annotations = Collections.unmodifiableList(annotations);
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

  /** Return listable separator depending on the annotation target element type. */
  public Listable getAnnotationSeparator() {
    ElementType target = getAnnotationTarget();
    boolean inline =
        target == ElementType.TYPE_PARAMETER
            || target == ElementType.TYPE_USE
            || target == ElementType.PARAMETER;
    return inline ? Listable.SPACE : Listable.NEWLINE;
  }

  public abstract ElementType getAnnotationTarget();

  @Override
  public int hashCode() {
    return list().hashCode();
  }

  public boolean isAnnotated() {
    return !annotations.isEmpty();
  }

  public Listable toAnnotationsListable() {
    if (isAnnotated()) {
      Listable separator = getAnnotationSeparator();
      return listing -> listing.add(getAnnotations(), separator).add(separator);
    }
    return Listable.IDENTITY;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{@" + (isAnnotated() ? getAnnotations().size() : 0) + "}";
  }
}
