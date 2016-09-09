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
}
