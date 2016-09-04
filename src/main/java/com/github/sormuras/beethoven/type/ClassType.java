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
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import com.github.sormuras.beethoven.Annotated;
import com.github.sormuras.beethoven.Annotation;
import com.github.sormuras.beethoven.Listing;
import com.github.sormuras.beethoven.Listing.NameMode;
import com.github.sormuras.beethoven.Name;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

/**
 * Class or interface type.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-ClassType">JLS
 * ClassType</a>
 */
public class ClassType extends ReferenceType {

  /** Single identifier, annotatable and typed class or interface name. */
  public static class Simple extends Annotated {

    private final String name;
    private final List<TypeArgument> typeArguments;

    Simple(List<Annotation> annotations, String name, List<TypeArgument> typeArguments) {
      super(annotations);
      this.name = name;
      this.typeArguments = unmodifiableList(typeArguments);
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

  public static final ClassType OBJECT = ClassType.type(Object.class);

  /** Create {@link ClassType} using raw type and attach type arguments to last simple name. */
  public static ClassType parameterized(Class<?> raw, java.lang.reflect.Type... arguments) {
    assert raw.getTypeParameters().length == arguments.length;
    ClassType rawClassType = type(raw);
    int last = rawClassType.getNames().size() - 1;
    IntFunction<List<Type>> function = i -> i == last ? Type.types(arguments) : emptyList();
    return rawClassType.toParameterizedType(function);
  }

  public static ClassType type(Class<?> type) {
    return new ClassType(Name.name(type).packageName(), simples(type));
  }

  /** Create list of simple names - potentially with annotations and type parameters. */
  public static List<Simple> simples(Class<?> type) {
    List<Simple> simples = new ArrayList<>();
    while (true) {
      List<Annotation> annotations = Annotation.annotations(type);
      String identifier = type.getSimpleName();
      List<TypeArgument> arguments = emptyList(); // TODO type.getTypeParameters()
      simples.add(0, new Simple(annotations, identifier, arguments));
      type = type.getEnclosingClass();
      if (type == null) {
        return simples;
      }
    }
  }

  private final List<Simple> names;
  private final String packageName;

  ClassType(String packageName, List<Simple> names) {
    super(emptyList());
    this.packageName = packageName;
    this.names = unmodifiableList(names);
  }

  @Override
  public Listing apply(Listing listing) {
    NameMode mode = listing.getNameModeFunction().apply(toName());
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

  public Simple getLastClassName() {
    return names.get(names.size() - 1);
  }

  /** Create simple {@link Name} for this {@link ClassType} instance. */
  public Name toName() {
    List<String> identifiers = new ArrayList<>();
    if (!getPackageName().isEmpty()) {
      stream(getPackageName().split("\\.")).forEach(identifiers::add);
    }
    int packageLevel = identifiers.size();
    names.forEach(simple -> identifiers.add(simple.getName()));
    return Name.name(packageLevel, identifiers);
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
    return packageName.equals("java.lang")
        && names.size() == 1
        && names.get(0).getName().equals("Object");
  }

  @Override
  public ClassType toAnnotatedType(List<Annotation> annotations) {
    Simple last = getLastClassName();
    List<Simple> simples = new ArrayList<>(names);
    simples.set(simples.size() - 1, new Simple(annotations, last.name, last.typeArguments));
    return new ClassType(packageName, simples);
  }

  /** Create new {@link ClassType} copied from this instance with supplied type arguments. */
  public ClassType toParameterizedType(IntFunction<List<Type>> typeArgumentsSupplier) {
    List<Simple> simples = new ArrayList<>();
    IntStream.range(0, names.size())
        .forEach(
            i -> {
              Simple source = names.get(i);
              List<Type> types = typeArgumentsSupplier.apply(i);
              List<TypeArgument> tas = types.stream().map(TypeArgument::argument).collect(toList());
              simples.add(new Simple(source.getAnnotations(), source.name, tas));
            });
    return new ClassType(packageName, simples);
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
