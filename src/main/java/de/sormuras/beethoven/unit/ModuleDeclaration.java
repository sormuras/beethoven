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

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import de.sormuras.beethoven.Listable;
import de.sormuras.beethoven.Listing;
import de.sormuras.beethoven.Name;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Java module declaration.
 *
 * <p>A compilation unit (JLS 7.3) may contain a module declaration. In this case, the filename of
 * the compilation unit is typically {@code module-info.java}.
 *
 * <p>Example:
 *
 * <pre>
 * module M @ 1.0 {
 *   requires A @ &gt;= 2.0; // use v2 or above
 *   requires B for compilation, reflection;
 *   requires service S1;
 *   requires optional service S2;
 *
 *   provides MI @ 4.0;
 *   provides service MS with C;
 *   exports  ME;
 *   permits  MF;
 *   class    MMain;
 *
 *   view N {
 *     provides NI @ 1.0;
 *     provides service NS with D;
 *     exports  NE;
 *     permits  MF;
 *     class    NMain;
 *   }
 * }
 * </pre>
 *
 * @see <a href="http://openjdk.java.net/projects/jigsaw/doc/lang-vm.html#jigsaw-1.3.2">module</a>
 */
public class ModuleDeclaration implements Listable {

  public enum Scope {
    COMPILATION,
    REFLECTION,
    EXECUTION;

    public String literal() {
      return name().toLowerCase();
    }
  }

  private Name name;
  private String version;
  private List<Listable> directives = new ArrayList<>();
  private List<Listable> views = new ArrayList<>();

  @Override
  public Listing apply(Listing listing) {
    listing.add("module").add(' ').add(name).add(' ');
    getVersion().ifPresent(version -> listing.add('@').add(' ').add(version).add(' '));
    listing.add('{').newline().indent(1);
    directives.forEach(listing::add);
    if (!views.isEmpty()) {
      listing.newline();
      views.forEach(listing::add);
    }
    listing.indent(-1).add('}').newline();
    return listing;
  }

  @Override
  public boolean isEmpty() {
    return directives.isEmpty() && views.isEmpty();
  }

  public Name getName() {
    return name;
  }

  public Optional<String> getVersion() {
    return Optional.ofNullable(version);
  }

  public void setName(Name name) {
    this.name = name;
  }

  public void setName(String name) {
    setName(Name.name(name));
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public void requiresModule(String name, String versionQuery, Scope... scopes) {
    requiresModule(false, false, Name.name(name), versionQuery, scopes);
  }

  public void requiresModule(
      boolean localFlag, boolean publicFlag, Name name, String versionQuery, Scope... scopes) {
    directives.add(
        listing -> {
          listing.add("requires ");
          if (localFlag) {
            listing.add("local ");
          }
          if (publicFlag) {
            listing.add("public ");
          }
          listing.add(name);
          if (versionQuery != null) {
            listing.add(' ').add('@').add(' ').add(versionQuery);
          }
          if (scopes.length > 0) {
            listing.add(" for ");
            listing.add(String.join(", ", stream(scopes).map(Scope::literal).collect(toList())));
          }
          listing.add(';');
          listing.newline();
          return listing;
        });
  }

  public void requiresService(String name) {
    requiresService(false, Name.name(name));
  }

  public void requiresService(boolean optionalFlag, Name name) {
    directives.add(
        listing -> {
          listing.add("requires ");
          if (optionalFlag) {
            listing.add("optional ");
          }
          listing.add("service ");
          listing.add(name);
          listing.add(';');
          listing.newline();
          return listing;
        });
  }
}
