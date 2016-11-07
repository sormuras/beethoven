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

package de.sormuras.beethoven.unit;

import de.sormuras.beethoven.Compilation;
import de.sormuras.beethoven.Listable;
import de.sormuras.beethoven.Listing;
import de.sormuras.beethoven.Name;

import java.lang.annotation.ElementType;
import java.lang.module.ModuleFinder;
import java.lang.reflect.Module;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

/**
 * Java module declaration.
 *
 * <p>A compilation unit (JLS 7.3) may contain a module declaration. In this case, the filename of
 * the compilation unit is typically {@code module-info.java}.
 *
 * <p>Example:
 *
 * <pre>
 * ModuleDeclaration:
 *   {Annotation} [open] module ModuleName { {ModuleStatement} }
 *
 * ModuleName:
 *   Identifier
 *   ModuleName . Identifier
 *
 * ModuleStatement:
 *   requires {RequiresModifier} ModuleName ;
 *   exports PackageName [to ModuleName {, ModuleName}] ;
 *   opens PackageName [to ModuleName {, ModuleName}] ;
 *   uses TypeName ;
 *   provides TypeName with TypeName {, TypeName} ;
 *
 * RequiresModifier: one of
 *   transitive static
 * </pre>
 *
 * @see <a href="http://cr.openjdk.java.net/~mr/jigsaw/spec/lang-vm.html">JIGSAW SPEC</a>
 */
public class ModuleDeclaration extends Annotatable {

  public enum RequiresModifier {
    STATIC,
    TRANSITIVE;

    public String literal() {
      return name().toLowerCase();
    }
  }

  private boolean open;
  private Name name;
  private List<Listable> requires = new ArrayList<>();

  @Override
  public boolean isEmpty() {
    return requires.isEmpty();
  }

  @Override
  public Listing apply(Listing listing) {
    applyAnnotations(listing);
    if (isOpen()) {
      listing.add("open ");
    }
    listing.add("module").add(' ').add(getName()).add(' ').add('{').newline();
    listing.indent(1);
    boolean newlineNeeded = false;
    if (!requires.isEmpty()) {
      requires.forEach(listing::add);
      newlineNeeded = true;
    }
    //    if (!requiredServices.isEmpty()) {
    //      if (newlineNeeded) {
    //        listing.newline();
    //      }
    //      requiredServices.forEach(listing::add);
    //      newlineNeeded = true;
    //    }
    //    if (!getDirectives().isEmpty()) {
    //      if (newlineNeeded) {
    //        listing.newline();
    //      }
    //      getDirectives().forEach(listing::add);
    //      newlineNeeded = true;
    //    }
    //    if (!views.isEmpty()) {
    //      if (newlineNeeded) {
    //        listing.newline();
    //      }
    //      views.forEach(listing::add);
    //    }
    listing.indent(-1).add('}').newline();
    return listing;
  }

  @Override
  public ElementType getAnnotationsTarget() {
    return ElementType.TYPE;
  }

  public Name getName() {
    return name;
  }

  public boolean isOpen() {
    return open;
  }

  public void setName(Name name) {
    this.name = name;
  }

  public void setOpen(boolean open) {
    this.open = open;
  }

  public void requires(Name name, RequiresModifier... mods) {
    requires.add(
        listing -> {
          listing.add("requires ");
          if (mods.length > 0) {
            listing.add(
                String.join(" ", stream(mods).map(RequiresModifier::literal).collect(toList())));
          }
          listing.add(name);
          listing.add(';');
          listing.newline();
          return listing;
        });
  }

  public void compile() throws Exception {
    ClassLoader loader = Compilation.compile(Compilation.source("/module-info.java", list()));
  }
}
