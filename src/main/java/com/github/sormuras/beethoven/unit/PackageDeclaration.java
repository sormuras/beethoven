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

package com.github.sormuras.beethoven.unit;

import com.github.sormuras.beethoven.Listing;
import com.github.sormuras.beethoven.Name;
import java.lang.annotation.ElementType;
import java.net.URI;
import java.util.List;

/**
 * Package declaration.
 *
 * <pre>
 * PackageDeclaration:<br>
 * {PackageModifier} package Identifier {. Identifier} ;
 * </pre>
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.4">JLS 7.4</a>
 */
public class PackageDeclaration extends Annotatable {

  public static PackageDeclaration of(Name packageName) {
    PackageDeclaration name = new PackageDeclaration();
    name.setName(packageName);
    return name;
  }

  public static PackageDeclaration of(String packageName) {
    if (packageName == null || packageName.isEmpty()) {
      throw new AssertionError("Package name must not be blank!");
    }
    List<String> names = List.of(Name.DOT.split(packageName));
    return of(Name.name(names.size(), names));
  }

  private Name name = null;

  @Override
  public Listing apply(Listing listing) {
    if (isUnnamed()) {
      return listing;
    }
    applyAnnotations(listing);
    listing.add("package ").add(getName().packageName()).add(';').newline();
    return listing;
  }

  @Override
  public ElementType getAnnotationsTarget() {
    return ElementType.PACKAGE;
  }

  public Name getName() {
    return name;
  }

  @Override
  public boolean isEmpty() {
    return isUnnamed();
  }

  public boolean isUnnamed() {
    return getName() == null;
  }

  public String resolve(String simpleName) {
    if (isUnnamed()) {
      return simpleName;
    }
    return getName().canonical() + '.' + simpleName;
  }

  public void setName(Name name) {
    this.name = name;
  }

  public URI toUri(String simpleName) {
    if (isUnnamed()) {
      return URI.create(simpleName);
    }
    return URI.create(getName().canonical().replace('.', '/') + '/' + simpleName);
  }
}
