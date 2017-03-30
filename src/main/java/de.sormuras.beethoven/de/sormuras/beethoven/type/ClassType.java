/*
 * Copyright 2017 Christian Stein
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package de.sormuras.beethoven.type;

import static java.util.Arrays.stream;
import static java.util.Collections.addAll;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import de.sormuras.beethoven.Annotated;
import de.sormuras.beethoven.Annotation;
import de.sormuras.beethoven.Listing;
import de.sormuras.beethoven.Name;
import de.sormuras.beethoven.Style;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;

/**
 * Class or interface type.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-ClassType">JLS
 *     ClassType</a>
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
      applyAnnotations(listing);
      if (typeArguments.isEmpty()) {
        return listing.add(getName());
      }
      listing.add(getName()).add('<').addAll(getTypeArguments(), ", ").add('>');
      return listing;
    }

    @Override
    public ElementType getAnnotationsTarget() {
      return ElementType.TYPE_USE;
    }

    public String getName() {
      return name;
    }

    public List<TypeArgument> getTypeArguments() {
      return typeArguments;
    }

    public boolean isGeneric() {
      return !typeArguments.isEmpty();
    }
  }

  public static final ClassType OBJECT = ClassType.type(Object.class);

  /** Create simple {@code Name} for the given {@code ClassType} instance. */
  public static Name name(ClassType type) {
    List<String> identifiers = new ArrayList<>();
    if (!type.getPackageName().isEmpty()) {
      addAll(identifiers, type.getPackageName().split("\\."));
    }
    int packageLevel = identifiers.size();
    identifiers.addAll(type.getSimples().stream().map(Simple::getName).collect(toList()));
    return Name.name(packageLevel, identifiers);
  }

  /** Create {@code ClassType} using raw type and attach type arguments to last simple name. */
  public static ClassType parameterized(Class<?> raw, java.lang.reflect.Type... arguments) {
    assert raw.getTypeParameters().length == arguments.length;
    ClassType rawClassType = type(raw);
    int last = rawClassType.getSimples().size() - 1;
    IntFunction<List<Type>> function = i -> i == last ? types(arguments) : emptyList();
    return rawClassType.parameterized(function);
  }

  public static ClassType type(Class<?> type) {
    return new ClassType(Name.name(type).packageName(), simples(type));
  }

  public static ClassType type(Name name) {
    return new ClassType(name.packageName(), simples(name.simpleNames()));
  }

  /** Create {@code ClassType} with elements given as simple name strings. */
  public static ClassType type(String packageName, String topLevelName, String... nested) {
    if (nested.length == 0) {
      return new ClassType(packageName, singletonList(simple(topLevelName)));
    }
    List<Simple> simples = simples(nested);
    simples.add(0, simple(topLevelName));
    return new ClassType(packageName, simples);
  }

  public static ClassType type(String packageName, Simple topLevel, Simple... nested) {
    List<Simple> simples = new ArrayList<>();
    simples.add(topLevel);
    simples.addAll(Arrays.asList(nested));
    return new ClassType(packageName, simples);
  }

  public static Simple simple(String identifier, java.lang.reflect.Type... typeArguments) {
    List<TypeArgument> arguments = emptyList();
    if (typeArguments.length > 0) {
      arguments = types(typeArguments).stream().map(TypeArgument::argument).collect(toList());
    }
    return new Simple(emptyList(), identifier, arguments);
  }

  /** Create list of simple simples - potentially with annotations and type parameters. */
  public static List<Simple> simples(Class<?> type) {
    List<Simple> simples = new ArrayList<>();
    while (true) {
      simples.add(0, simple(type.getSimpleName()));
      type = type.getEnclosingClass();
      if (type == null) {
        return simples;
      }
    }
  }

  /** Create list of simple simples. */
  public static List<Simple> simples(String... names) {
    return stream(names).map(ClassType::simple).collect(toList());
  }

  private final String packageName;
  private final Name name;
  private final List<Simple> simples;

  ClassType(String packageName, List<Simple> simples) {
    super(emptyList());
    assert packageName != null : "packageName";
    assert !simples.isEmpty() : "simples must not be empty";
    this.packageName = packageName;
    this.simples = unmodifiableList(simples);
    this.name = name(this);
  }

  @Override
  public ClassType annotated(IntFunction<List<Annotation>> annotationsSupplier) {
    List<Simple> newSimples = new ArrayList<>();
    for (int i = 0; i < simples.size(); i++) {
      Simple source = simples.get(i);
      List<Annotation> annotations = annotationsSupplier.apply(i);
      newSimples.add(new Simple(annotations, source.getName(), source.getTypeArguments()));
    }
    return new ClassType(packageName, newSimples);
  }

  @Override
  public Listing apply(Listing listing) {
    listing.getCollectedNames().add(name);
    Style style = listing.getStyling().apply(getName());
    if (style == Style.LAST) {
      return listing.add(getLastSimple());
    }
    if (style == Style.SIMPLE) {
      return listing.addAll(getSimples(), ".");
    }
    assert style == Style.CANONICAL : "Unknown style: " + style;
    if (!getPackageName().isEmpty()) {
      listing.add(getPackageName()).add('.');
    }
    return listing.addAll(getSimples(), ".");
  }

  @Override
  public String binary() {
    StringBuilder builder = new StringBuilder();
    if (!getPackageName().isEmpty()) {
      builder.append(getPackageName()).append('.');
    }
    builder.append(getSimples().stream().map(Simple::getName).collect(joining("$")));
    return builder.toString();
  }

  @Override
  public List<Annotation> getAnnotations() {
    return getLastSimple().getAnnotations();
  }

  @Override
  public int getAnnotationsIndex() {
    return simples.size() - 1;
  }

  public Simple getLastSimple() {
    return simples.get(simples.size() - 1);
  }

  public Name getName() {
    return name;
  }

  public List<Simple> getSimples() {
    return simples;
  }

  public String getPackageName() {
    return packageName;
  }

  @Override
  public boolean isAnnotated() {
    return simples.stream().anyMatch(Annotated::isAnnotated);
  }

  public boolean isGeneric() {
    return simples.stream().anyMatch(Simple::isGeneric);
  }

  @Override
  public boolean isJavaLangObject() {
    return packageName.equals("java.lang")
        && simples.size() == 1
        && simples.get(0).getName().equals("Object");
  }

  /** Create new {@code ClassType} copied from this instance with supplied type arguments. */
  public ClassType parameterized(IntFunction<List<Type>> typeArgumentsSupplier) {
    List<Simple> newSimples = new ArrayList<>();
    for (int i = 0; i < simples.size(); i++) {
      Simple source = simples.get(i);
      List<Type> types = typeArgumentsSupplier.apply(i);
      List<TypeArgument> arguments = types.stream().map(TypeArgument::argument).collect(toList());
      newSimples.add(new Simple(source.getAnnotations(), source.getName(), arguments));
    }
    return new ClassType(packageName, newSimples);
  }
}
