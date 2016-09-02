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

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import com.github.sormuras.beethoven.Annotated;
import com.github.sormuras.beethoven.Annotation;
import com.github.sormuras.beethoven.Listing;
import com.github.sormuras.beethoven.Listing.NameMode;
import com.github.sormuras.beethoven.Name;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Class or interface type.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-ClassType">JLS
 * ClassType</a>
 */
public class ClassType extends ReferenceType {

  /** Simple and(!) annotatable and(!) typed class or interface name. */
  public static class Simple extends Annotated {

    private final String name;
    private final List<TypeArgument> typeArguments;

    public Simple(List<Annotation> annotations, String name, List<TypeArgument> typeArguments) {
      super(annotations);
      this.name = name;
      this.typeArguments = typeArguments;
    }

    @Override
    public Listing apply(Listing listing) {
      listing.add(toAnnotationsListable());
      if (typeArguments.isEmpty()) {
        return listing.add(getName());
      }
      listing.add(getName()).add('<').add(getTypeArguments(), ", ").add('>');
      return listing;
    }

    @Override
    public ElementType getAnnotationTarget() {
      return ElementType.TYPE_USE;
    }

    public String getName() {
      return name;
    }

    public List<TypeArgument> getTypeArguments() {
      return typeArguments;
    }
  }

  private final List<Simple> names;
  private final String packageName;

  public ClassType(String packageName, List<Simple> names) {
    super(Collections.emptyList());
    this.packageName = packageName;
    this.names = Collections.unmodifiableList(names);
  }

  @Override
  public Listing apply(Listing listing) {
    NameMode mode = listing.getNameModeFunction().apply(getName());
    if (mode == NameMode.LAST) {
      return listing.add(getLastClassName());
    }
    if (mode == NameMode.SIMPLE) {
      return listing.add(getNames(), ".");
    }
    assert mode == NameMode.CANONICAL : "Unknown name mode: " + mode;
    if (!getPackageName().isEmpty()) {
      listing.add(getPackageName()).add('.');
    }
    return listing.add(getNames(), ".");
  }

  @Override
  public List<Annotation> getAnnotations() {
    return getLastClassName().getAnnotations();
  }

  public Optional<ClassType> getEnclosingClassType() {
    if (names.size() == 1) {
      return Optional.empty();
    }
    return Optional.of(new ClassType(getPackageName(), names.subList(0, names.size() - 1)));
  }

  public Simple getLastClassName() {
    return names.get(names.size() - 1);
  }

  /** Return simple {@link Name} for this {@link ClassType} instance. */
  public Name getName() {
    List<String> simpleNames = new ArrayList<>();
    if (!getPackageName().isEmpty()) {
      stream(getPackageName().split("\\.")).forEach(simpleNames::add);
    }
    int packageLevel = simpleNames.size();
    names.forEach(n -> simpleNames.add(n.getName()));
    return Name.name(packageLevel, simpleNames);
  }

  public List<Simple> getNames() {
    return names;
  }

  public String getPackageName() {
    return packageName;
  }

  @Override
  public boolean isAnnotated() {
    return names.stream().filter(Annotated::isAnnotated).findAny().isPresent();
  }

  @Override
  public boolean isJavaLangObject() {
    return getName().isJavaLangObject();
  }

  @Override
  public String toClassName() {
    StringBuilder builder = new StringBuilder();
    if (!getPackageName().isEmpty()) {
      builder.append(getPackageName()).append('.');
    }
    builder.append(getNames().stream().map(Simple::getName).collect(joining("$")));
    return builder.toString();
  }
}
