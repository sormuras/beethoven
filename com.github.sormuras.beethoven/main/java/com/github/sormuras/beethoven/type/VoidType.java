package com.github.sormuras.beethoven.type;

import com.github.sormuras.beethoven.Annotation;
import com.github.sormuras.beethoven.Listing;
import java.lang.annotation.ElementType;
import java.util.Collections;
import java.util.List;
import java.util.function.IntFunction;

public class VoidType extends Type {

  public static final VoidType INSTANCE = new VoidType();

  public static VoidType instance() {
    return INSTANCE;
  }

  private VoidType() {
    super(Collections.emptyList());
  }

  @Override
  public Type annotated(IntFunction<List<Annotation>> annotationsSupplier) {
    throw new UnsupportedOperationException("VoidType does not support annotated()!");
  }

  @Override
  public Listing apply(Listing listing) {
    return listing.add("void");
  }

  @Override
  public String binary() {
    return "void";
  }

  @Override
  public ElementType getAnnotationsTarget() {
    return null;
  }

  @Override
  public boolean isAnnotated() {
    return false;
  }

  @Override
  public boolean isVoid() {
    return true;
  }
}
