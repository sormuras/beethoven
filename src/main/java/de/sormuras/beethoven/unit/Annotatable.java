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

package de.sormuras.beethoven.unit;

import de.sormuras.beethoven.Annotated;
import de.sormuras.beethoven.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Default {@link Annotation}-collecting support. */
public abstract class Annotatable extends Annotated {

  private Map<Object, Object> tags = Collections.emptyMap();

  public void addAnnotation(Annotation annotation) {
    getAnnotations().add(annotation);
  }

  public void addAnnotation(
      Class<? extends java.lang.annotation.Annotation> type, Object... values) {
    addAnnotation(Annotation.annotation(type, values));
  }

  public void addAnnotations(Annotation... annotations) {
    addAnnotations(Arrays.asList(annotations));
  }

  public void addAnnotations(Collection<Annotation> annotations) {
    getAnnotations().addAll(annotations);
  }

  /** Return modifiable list of annotations. */
  public List<Annotation> getAnnotations() {
    if (annotations == Collections.EMPTY_LIST) {
      annotations = new ArrayList<>();
    }
    return annotations;
  }

  public boolean isTagged() {
    return tags != null && !tags.isEmpty();
  }

  public Optional<Object> getTag(Object key) {
    if (tags == Collections.EMPTY_MAP) {
      return Optional.empty();
    }
    return Optional.ofNullable(tags.get(key));
  }

  public Map<Object, Object> getTags() {
    if (tags == Collections.EMPTY_MAP) {
      tags = new HashMap<>();
    }
    return tags;
  }
}
