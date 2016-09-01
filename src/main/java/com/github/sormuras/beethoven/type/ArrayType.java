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

import com.github.sormuras.beethoven.Annotated;
import com.github.sormuras.beethoven.Annotation;
import com.github.sormuras.beethoven.Listable;
import com.github.sormuras.beethoven.Listing;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class ArrayType extends ReferenceType {

  public static class Dimension extends Annotated {

    @Override
    public Listing apply(Listing listing) {
      return listing.add(toAnnotationsListable()).add("[]");
    }

    @Override
    public ElementType getAnnotationTarget() {
      return ElementType.TYPE_USE;
    }
  }

  public static ArrayType array(Class<?> componentType, int size) {
    return array(Type.type(componentType), size);
  }

  public static ArrayType array(Type componentType, int size) {
    return array(componentType, dimensions(size));
  }

  public static ArrayType array(Type componentType, List<Dimension> dimensions) {
    ArrayType array = new ArrayType();
    array.setComponentType(componentType);
    array.setDimensions(dimensions);
    return array;
  }

  /** Create n array dimension(s). */
  public static List<Dimension> dimensions(int size) {
    List<Dimension> dimensions = new ArrayList<>();
    IntStream.range(0, size).forEach(i -> dimensions.add(new Dimension()));
    return dimensions;
  }
  private Type componentType;
  private List<Dimension> dimensions = Collections.emptyList();

  public void addAnnotations(int index, Annotation... annotations) {
    if (annotations.length == 0) {
      return;
    }
    dimensions.get(index).getAnnotations().addAll(Arrays.asList(annotations));
  }

  @Override
  public Listing apply(Listing listing) {
    return listing.add(getComponentType()).add(getDimensions(), Listable.IDENTITY);
  }

  @Override
  public List<Annotation> getAnnotations() {
    if (isEmpty()) {
      return Collections.emptyList();
    }
    return dimensions.get(0).getAnnotations();
  }

  public Type getComponentType() {
    return componentType;
  }

  public List<Dimension> getDimensions() {
    if (dimensions == Collections.EMPTY_LIST) {
      dimensions = new ArrayList<>();
    }
    return dimensions;
  }

  @Override
  public boolean isAnnotated() {
    if (isEmpty()) {
      return false;
    }
    return dimensions.get(0).isAnnotated();
  }

  @Override
  public boolean isEmpty() {
    return dimensions.isEmpty();
  }

  public void setComponentType(Type componentType) {
    this.componentType = componentType;
  }

  public void setDimensions(List<Dimension> dimensions) {
    this.dimensions = dimensions;
  }

  @Override
  public String toClassName() {
    StringBuilder builder = new StringBuilder();
    IntStream.range(0, getDimensions().size()).forEach(i -> builder.append('['));
    Type componentType = getComponentType();
    if (componentType instanceof PrimitiveType) {
      return builder.append(((PrimitiveType) componentType).toArrayClassNameIndicator()).toString();
    }
    return builder.append('L').append(componentType.toClassName()).append(';').toString();
  }
}
