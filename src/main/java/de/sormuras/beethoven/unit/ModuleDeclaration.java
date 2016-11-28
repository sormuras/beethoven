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

import static java.lang.String.join;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import de.sormuras.beethoven.Compilation;
import de.sormuras.beethoven.Listable;
import de.sormuras.beethoven.Listing;
import de.sormuras.beethoven.Name;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
  private List<Listable> exports = new ArrayList<>();
  private List<Listable> opens = new ArrayList<>();
  private List<Listable> uses = new ArrayList<>();
  private List<Listable> provides = new ArrayList<>();

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
    AtomicBoolean needsNewline = new AtomicBoolean(false);
    listing.addAll(requires, needsNewline);
    listing.addAll(exports, needsNewline);
    if (!isOpen()) {
      listing.addAll(opens, needsNewline);
    }
    listing.addAll(uses, needsNewline);
    listing.addAll(provides, needsNewline);

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

  public void requires(Name moduleName, RequiresModifier... mods) {
    requires.add(
        listing -> {
          listing.add("requires ");
          if (mods.length > 0) {
            listing.add(join(" ", stream(mods).map(RequiresModifier::literal).collect(toList())));
            listing.add(' ');
          }
          listing.add(moduleName);
          listing.add(';');
          listing.newline();
          return listing;
        });
  }

  public void exports(Name packageName, Name... toModuleNames) {
    exportsOrOpens(exports, packageName, List.of(toModuleNames));
  }

  public void opens(Name packageName, Name... toModuleNames) {
    exportsOrOpens(opens, packageName, List.of(toModuleNames));
  }

  private void exportsOrOpens(List<Listable> list, Name packageName, List<Name> toModuleNames) {
    String keyword = list == exports ? "exports" : "opens";
    list.add(
        listing -> {
          listing.add(keyword);
          listing.add(' ');
          listing.add(packageName);
          if (!toModuleNames.isEmpty()) {
            listing.add(" to ");
            listing.addAll(toModuleNames, ", ");
          }
          listing.add(';');
          listing.newline();
          return listing;
        });
  }

  public void uses(Name serviceInterfaceName) {
    uses.add(listing -> listing.add("uses ").add(serviceInterfaceName).add(';').newline());
  }

  public void provides(Name serviceInterfaceName, Name with, Name... moreWiths) {
    provides.add(
        listing -> {
          listing.add("provides ");
          listing.add(serviceInterfaceName);
          listing.add(" with ");
          listing.add(with);
          if (moreWiths.length > 0) {
            listing.add(", ");
            listing.addAll(List.of(moreWiths), ", ");
          }
          listing.add(';');
          listing.newline();
          return listing;
        });
  }

  public void compile() throws Exception {
    Compilation.compile(Compilation.source("/module-info.java", list()));
  }
}
