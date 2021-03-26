package com.github.sormuras.beethoven;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import java.lang.annotation.ElementType;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Base {@link Annotation}-collection implementation. */
public abstract class Annotated implements Listable {

  protected List<Annotation> annotations;
  private Map<Object, Object> tags = Collections.emptyMap();

  /** Initialize this instance. */
  protected Annotated() {
    this.annotations = emptyList();
  }

  /** Initialize this instance with an unmodifiable list of annotations. */
  protected Annotated(List<Annotation> annotations) {
    this.annotations = annotations.isEmpty() ? emptyList() : unmodifiableList(annotations);
  }

  /** Add all annotations to the given {@link Listing} instance. */
  protected Listing applyAnnotations(Listing listing) {
    if (isAnnotated()) {
      Listable annotationsSeparator = getAnnotationsSeparator();
      listing.addAll(annotations, annotationsSeparator);
      listing.add(annotationsSeparator);
    }
    return listing;
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

  /**
   * Return listable separator textually separating annotations from each other.
   *
   * <p>This implementation returns a separator depending on the annotation target element type.
   */
  public Listable getAnnotationsSeparator() {
    ElementType target = getAnnotationsTarget();
    boolean inline =
        target == ElementType.TYPE_PARAMETER
            || target == ElementType.TYPE_USE
            || target == ElementType.PARAMETER;
    return inline ? SPACE : NEWLINE;
  }

  /** The designated target element type of the annotations. */
  public abstract ElementType getAnnotationsTarget();

  /** Used by {@link #toString()} as an instance description hint. */
  public String getDescription() {
    return list() + " // 0x" + Integer.toHexString(System.identityHashCode(this));
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

  @Override
  public int hashCode() {
    return list().hashCode();
  }

  /** Return {@code true} if there is at least one annotation available. */
  public boolean isAnnotated() {
    return !annotations.isEmpty();
  }

  public boolean isTagged() {
    return tags != null && !tags.isEmpty();
  }

  @Override
  public String toString() {
    String className = getClass().getSimpleName();
    String description = getDescription();
    return String.format("%s {@%d %s}", className, getAnnotations().size(), description);
  }
}
