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
 * <p>An array type is written as the name of an element type followed by some number of empty pairs
 * of square brackets []. The number of bracket pairs indicates the depth of array nesting. Each
 * bracket pair in an array type may be annotated by type annotations (§9.7.4). An annotation
 * applies to the bracket pair (or ellipsis, in a variable arity parameter declaration) that follows
 * it.
 *
 * <p>The element type of an array may be any type, whether primitive or reference.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-10.html#jls-10.1">JLS
 *     10.1</a>
 */
public class ArrayType extends ReferenceType {

  public static class Dimension extends Annotated {

    public Dimension(List<Annotation> annotations) {
      super(annotations);
    }

    @Override
    public Listing apply(Listing listing) {
      return applyAnnotations(listing).add("[]");
    }

    @Override
    public ElementType getAnnotationsTarget() {
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
    if (size <= 0) {
      throw new IllegalArgumentException("size <= 0 are illegal: " + size);
    }
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
  public ArrayType annotated(IntFunction<List<Annotation>> annotationsSupplier) {
    return new ArrayType(componentType, dimensions(dimensions.size(), annotationsSupplier));
  }

  @Override
  public Listing apply(Listing listing) {
    return listing.add(getComponentType()).addAll(getDimensions(), Listable.IDENTITY);
  }

  @Override
  public String binary() {
    StringBuilder builder = new StringBuilder();
    getDimensions().forEach(dimension -> builder.append('['));
    Type componentType = getComponentType();
    if (componentType instanceof PrimitiveType) {
      return builder.append(((PrimitiveType) componentType).getTypeChar()).toString();
    }
    return builder.append('L').append(componentType.binary()).append(';').toString();
  }

  @Override
  public List<Annotation> getAnnotations() {
    return getDimensions().get(getAnnotationsIndex()).getAnnotations();
  }

  @Override
  public int getAnnotationsIndex() {
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
    return dimensions.stream().anyMatch(Annotated::isAnnotated);
  }

  @Override
  public boolean isEmpty() {
    return false;
  }
}
