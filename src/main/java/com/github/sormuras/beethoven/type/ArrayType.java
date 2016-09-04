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
import java.util.Collections;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

/**
 * Array type.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-10.html#jls-10.1">JLS 10</a>
 */
public class ArrayType extends ReferenceType {

  public static class Dimension extends Annotated {

    public Dimension(List<Annotation> annotations) {
      super(annotations);
    }

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
    return new ArrayType(componentType, dimensions);
  }

  /** Create n array dimension(s) without annotations. */
  public static List<Dimension> dimensions(int size) {
    return dimensions(size, i -> Collections.emptyList());
  }

  /** Create n array dimension(s) with annotations supplied by the given int-function. */
  public static List<Dimension> dimensions(int size, IntFunction<List<Annotation>> annotations) {
    List<Dimension> dimensions = new ArrayList<>();
    IntStream.range(0, size).forEach(i -> dimensions.add(new Dimension(annotations.apply(i))));
    return dimensions;
  }

  private final Type componentType;
  private final List<Dimension> dimensions;

  ArrayType(Type componentType, List<Dimension> dimensions) {
    super(Collections.emptyList());
    this.componentType = componentType;
    this.dimensions = dimensions;
  }

  @Override
  public ArrayType annotate(IntFunction<List<Annotation>> annotationsSupplier) {
    return new ArrayType(componentType, dimensions(dimensions.size(), annotationsSupplier));
  }

  @Override
  public Listing apply(Listing listing) {
    return listing.add(getComponentType()).add(getDimensions(), Listable.IDENTITY);
  }

  @Override
  public String binary() {
    StringBuilder builder = new StringBuilder();
    IntStream.range(0, getDimensions().size()).forEach(i -> builder.append('['));
    Type componentType = getComponentType();
    if (componentType instanceof PrimitiveType) {
      return builder.append(((PrimitiveType) componentType).getTypeChar()).toString();
    }
    return builder.append('L').append(componentType.binary()).append(';').toString();
  }

  @Override
  public int getAnnotationIndex() {
    return dimensions.size() - 1;
  }

  public Type getComponentType() {
    return componentType;
  }

  public List<Dimension> getDimensions() {
    return dimensions;
  }

  @Override
  public boolean isAnnotated() {
    return dimensions.stream().filter(Annotated::isAnnotated).findAny().isPresent();
  }

  @Override
  public boolean isEmpty() {
    return false;
  }
}
