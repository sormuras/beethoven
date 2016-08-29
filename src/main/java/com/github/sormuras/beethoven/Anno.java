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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * An annotation object denotes a specific invocation of an annotation type.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.7">JLS 9.7</a>
 */
public class Anno implements Listable {

  /**
   * Create new {@link Anno} instance by reflecting on the annotation found on given annotated
   * element calling {@link AnnotatedElement#getAnnotation(Class)}.
   */
  public static Anno anno(AnnotatedElement element, Class<? extends Annotation> annotationClass) {
    return anno(element.getAnnotation(annotationClass));
  }

  /**
   * Create new {@link Anno} instance by reflecting on the given annotation omitting default values.
   */
  public static Anno anno(Annotation annotation) {
    return anno(annotation, false);
  }

  /** Create new {@link Anno} instance by reflecting on the given annotation. */
  public static Anno anno(Annotation annotation, boolean includeDefaultValues) {
    Anno result = anno(annotation.annotationType());
    try {
      Method[] methods = annotation.annotationType().getDeclaredMethods();
      Arrays.sort(methods, (m1, m2) -> m1.getName().compareTo(m2.getName()));
      for (Method method : methods) {
        Object value = method.invoke(annotation);
        if (!includeDefaultValues) {
          if (Objects.deepEquals(value, method.getDefaultValue())) {
            continue;
          }
        }
        if (value.getClass().isArray()) {
          for (int i = 0; i < Array.getLength(value); i++) {
            result.addObject(method.getName(), Array.get(value, i));
          }
          continue;
        }
        if (value instanceof Annotation) {
          result.addMember(method.getName(), anno((Annotation) value, includeDefaultValues));
          continue;
        }
        result.addObject(method.getName(), value);
      }
    } catch (Exception exception) {
      String message = "Reflecting " + annotation + " failed: " + exception.getCause();
      throw new AssertionError(message, exception);
    }
    return result;
  }

  public static Anno anno(Class<? extends Annotation> type, Object... values) {
    return anno(Name.name(type), values);
  }

  public static Anno anno(Name name, Object... values) {
    Anno anno = new Anno(name);
    for (int i = 0; i < values.length; i++) {
      anno.addValue(values[i]);
    }
    return anno;
  }

  public static List<Anno> annos(Annotation... annotations) {
    return Arrays.stream(annotations).map(Anno::anno).collect(Collectors.toList());
  }

  /** Converts an object to a listable representation usable as an annotation value literal. */
  public static Listable value(Object object) {
    if (object instanceof Class) {
      return listing -> listing.add(Name.cast(object)).add(".class");
    }
    if (object instanceof Enum) {
      return listing -> listing.add(Name.cast(object));
    }
    if (object instanceof String) {
      return listing -> listing.add(Listable.escape((String) object));
    }
    if (object instanceof Float) {
      return listing -> listing.fmt(Locale.US, "%fF", object);
    }
    if (object instanceof Long) {
      return listing -> listing.fmt(Locale.US, "%dL", object);
    }
    if (object instanceof Character) {
      return listing -> listing.add("'").add(Listable.escape((char) object)).add("'");
    }
    if (object instanceof Listable) {
      return listing -> listing.add((Listable) object);
    }
    return listing -> listing.add(Objects.toString(object));
  }

  private Map<String, List<Listable>> members = Collections.emptyMap();
  private final Name name;

  Anno(Name name) {
    this.name = Objects.requireNonNull(name, "name");
  }

  /** Add the listable to the member specified by its name. */
  public void addMember(String name, Listable listable) {
    List<Listable> values = getMembers().get(name);
    if (values == null) {
      values = new ArrayList<>();
      getMembers().put(name, values);
    }
    values.add(listable);
  }

  public void addObject(String memberName, Object object) {
    Objects.requireNonNull(object, "constant non-null object expected as value: " + memberName);
    addMember(memberName, value(object));
  }

  public void addValue(Object object) {
    addObject("value", object);
  }

  @Override
  public Listing apply(Listing listing) {
    // always emit "@" and the typename
    listing.add('@').add(getTypeName());
    Map<String, List<Listable>> members = this.members;
    // trivial case: marker annotation w/o members
    if (members.isEmpty()) {
      return listing;
    }
    // simple case: single element annotation w/ member called "value"
    if (members.size() == 1 && members.containsKey("value")) {
      return listing.add('(').add(values(members.get("value"))).add(')');
    }
    // normal annotation: emit all "key = value" pairs
    Consumer<Entry<String, List<Listable>>> separate = entry -> listing.add(", ");
    Consumer<Entry<String, List<Listable>>> print =
        e -> listing.add(e.getKey()).add(" = ").add(values(e.getValue()));
    Spliterator<Entry<String, List<Listable>>> entries = members.entrySet().spliterator();
    listing.add('(');
    entries.tryAdvance(print);
    entries.forEachRemaining(separate.andThen(print));
    listing.add(')');
    return listing;
  }

  public Map<String, List<Listable>> getMembers() {
    if (members == Collections.EMPTY_MAP) {
      members = new LinkedHashMap<>();
    }
    return members;
  }

  public Name getTypeName() {
    return name;
  }

  @Override
  public String toString() {
    return "Anno{" + getTypeName() + ", members=" + members + "}";
  }

  /** Annotation array-aware value(s) appender. */
  private Listable values(List<Listable> values) {
    return (listing) -> {
      if (values.size() == 1) {
        return listing.add(values.get(0));
      }
      return listing.add('{').add(values, ", ").add('}');
    };
  }
}
