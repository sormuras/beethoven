package com.github.sormuras.beethoven.unit;

import com.github.sormuras.beethoven.Annotated;
import com.github.sormuras.beethoven.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** Default {@link Annotation}-collecting support. */
public abstract class Annotatable extends Annotated {

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
}
