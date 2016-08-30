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

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;

/**
 * Names are used to refer to entities declared in a program.
 *
 * <p>
 * A declared entity is a package, class type (normal or enum), interface type (normal or annotation
 * type), member (class, interface, field, or method) of a reference type, type parameter (of a
 * class, interface, method or constructor), parameter (to a method, constructor, or exception
 * handler), or local variable.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-6.html">JLS 6</a>
 */
public final class Name {

  /** Compiled <code>"."</code> pattern used to split canonical package and type names. */
  public static final Pattern DOT = Pattern.compile("\\.");

  /** Cast/convert any object to an instance of {@link Name}. */
  public static Name cast(Object any) {
    if (any == null) {
      return null;
    }
    if (any instanceof Name) {
      return (Name) any;
    }
    if (any instanceof Class) {
      return name((Class<?>) any);
    }
    if (any instanceof Enum) {
      return name((Enum<?>) any);
    }
    if (any instanceof Member) {
      return name((Member) any);
    }
    if (any instanceof String[]) {
      return name((String[]) any);
    }
    if (any instanceof List<?>) {
      return name(((List<?>) any).stream().map(Object::toString).collect(Collectors.toList()));
    }
    throw new IllegalArgumentException("Can't cast/convert instance of " + any.getClass());
  }

  /** Create name instance for the given class instance. */
  public static Name name(Class<?> type) {
    String[] packageNames = DOT.split(type.getName()); // java[.]lang[.]Thread$State
    String[] identifiers = DOT.split(type.getCanonicalName()); // java[.]lang[.]Thread[.]State
    return new Name(packageNames.length - 1, Arrays.asList(identifiers));
  }

  /** Create new Name based on the class type and declared member name. */
  public static Name name(Class<?> declaringType, String declaredMemberName) {
    Name declaringName = name(declaringType);
    List<String> names = new ArrayList<>(declaringName.size + 1);
    names.addAll(declaringName.identifiers);
    names.add(declaredMemberName);
    return new Name(declaringName.packageLevel, names);
  }

  /** Create new Name based on type element instance. */
  public static Name name(Element element) {
    List<String> simpleNames = new ArrayList<>();
    for (Element e = element; true; e = e.getEnclosingElement()) {
      if (e.getKind() == ElementKind.PACKAGE) {
        PackageElement casted = (PackageElement) e;
        if (casted.isUnnamed()) {
          return new Name(0, simpleNames);
        }
        String[] packageNames = DOT.split(casted.getQualifiedName().toString());
        simpleNames.addAll(0, Arrays.asList(packageNames));
        return new Name(packageNames.length, simpleNames);
      }
      simpleNames.add(0, e.getSimpleName().toString());
    }
  }

  /** Create name instance for the given enum constant. */
  public static Name name(Enum<?> constant) {
    return name(constant.getDeclaringClass(), constant.name());
  }

  /**
   * Create name instance for the identifiers.
   *
   * <p>
   * The fully qualified class name {@code abc.xyz.Alphabet} can be created by:
   *
   * <pre>
   * name(2, "abc", "xyz", "Alphabet")
   * </pre>
   *
   * @throws AssertionError if any identifier is not a syntactically valid qualified name.
   */
  public static Name name(int packageLevel, List<String> names) {
    assert packageLevel >= 0 : "Package level must not be < 0, but is " + packageLevel;
    assert packageLevel <= names.size() : "Package level " + packageLevel + " too high: " + names;
    assert names.stream().allMatch(SourceVersion::isName) : "Non-name in " + names;
    return new Name(packageLevel, names);
  }

  /**
   * Create name instance for the identifiers by delegating to {@link #name(int, List)}.
   *
   * <p>
   * The package level is determined by the first capital name of the list.
   */
  public static Name name(List<String> names) {
    int size = names.size();
    IntPredicate uppercase = index -> Character.isUpperCase(names.get(index).codePointAt(0));
    int packageLevel = IntStream.range(0, size).filter(uppercase).findFirst().orElse(size);
    return name(packageLevel, names);
  }

  /** Create new Name based on the member instance. */
  public static Name name(Member member) {
    return name(member.getDeclaringClass(), member.getName());
  }

  /** Create name instance for the identifiers by delegating to {@link #name(List)}. */
  public static Name name(String... identifiers) {
    return name(Arrays.asList(identifiers));
  }

  /** Create new Name based on the class type and declared member name. */
  public static Name reflect(Class<?> type, String declaredName) {
    try {
      Member field = type.getDeclaredField(declaredName);
      return name(field);
    } catch (Exception expected) {
      // fall-through
    }
    for (Member method : type.getDeclaredMethods()) {
      if (method.getName().equals(declaredName)) {
        return name(method);
      }
    }
    throw new AssertionError(String.format("Member '%s' of %s not found!", declaredName, type));
  }

  private final String canonical;
  private final List<String> identifiers;
  private final int packageLevel;
  private final String packageName;
  private final String simpleNames;
  private final int size;

  Name(int packageLevel, List<String> identifiers) {
    this.packageLevel = packageLevel;
    this.identifiers = Collections.unmodifiableList(identifiers);
    this.size = identifiers.size();
    this.canonical = String.join(".", identifiers);
    this.packageName = String.join(".", identifiers.subList(0, packageLevel));
    this.simpleNames = String.join(".", simpleNames());
  }

  public String canonical() {
    return canonical;
  }

  /** Create new enclosing {@link Name} instance based on this identifiers. */
  public Name enclosing() {
    if (!isEnclosed()) {
      throw new IllegalStateException(String.format("Not enclosed: '%s'", this));
    }
    int shrunkByOne = size - 1;
    int newPackageLevel = Math.min(packageLevel, shrunkByOne);
    return new Name(newPackageLevel, identifiers.subList(0, shrunkByOne));
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    return hashCode() == other.hashCode();
  }

  public String getSimpleNames() {
    return simpleNames;
  }

  @Override
  public int hashCode() {
    return canonical.hashCode();
  }

  public List<String> identifiers() {
    return identifiers;
  }

  public boolean isEnclosed() {
    return size > 1;
  }

  public boolean isJavaLangObject() {
    return size == 3 && "java.lang.Object".equals(canonical);
  }

  public boolean isJavaLangPackage() {
    return packageLevel == 2 && "java.lang".equals(packageName);
  }

  public String lastName() {
    return identifiers.get(size - 1);
  }

  public String packageName() {
    return packageName;
  }

  public List<String> simpleNames() {
    if (packageLevel >= size) {
      return Collections.emptyList();
    }
    return identifiers.subList(packageLevel, size);
  }

  public int size() {
    return size;
  }

  public String topLevelName() {
    return identifiers.get(packageLevel);
  }

  @Override
  public String toString() {
    return String.format("Name{%s/%s}", packageName, String.join(".", simpleNames()));
  }
}
