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

import de.sormuras.beethoven.Listable;
import de.sormuras.beethoven.Listing;
import de.sormuras.beethoven.Name;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

/**
 * Java module view declaration.
 *
 * <p>A compilation unit (JLS 7.3) may contain a module declaration. In this case, the filename of
 * the compilation unit is typically {@code module-info.java}.
 *
 * <p>Example from inside a {@code module}:
 *
 * <pre>
 *   view N {
 *     provides NI @ 1.0;
 *     provides service NS with D;
 *     exports  NE;
 *     permits  MF;
 *     class    NMain;
 *   }
 * </pre>
 *
 * @see <a href="http://openjdk.java.net/projects/jigsaw/doc/lang-vm.html#jigsaw-1.3.2">module</a>
 */
public class ModuleView implements Listable {

  private Name name;
  private List<Listable> directives = new ArrayList<>();

  @Override
  public Listing apply(Listing listing) {
    listing.add("view").add(' ').add(name).add(' ');
    listing.add('{').newline().indent(1);
    directives.forEach(listing::add);
    listing.indent(-1).add('}').newline();
    return listing;
  }

  @Override
  public boolean isEmpty() {
    return directives.isEmpty();
  }

  public List<Listable> getDirectives() {
    return directives;
  }

  public Name getName() {
    return name;
  }

  public void setName(Name name) {
    this.name = name;
  }

  public void setName(String name) {
    setName(Name.name(name));
  }

  public void providesModule(Name name, String version) {
    if (version == null || version.isEmpty()) {
      directives.add(listing -> listing.add("provides {N}{;}", name));
      return;
    }
    String format = "provides {N} @ {$}{;}";
    directives.add(listing -> listing.add(format, name, version));
  }

  public void providesService(Name serviceName, Name identifier) {
    String format = "provides service {N} with {N}{;}";
    directives.add(listing -> listing.add(format, serviceName, identifier));
  }

  public void exports(Name packageName) {
    directives.add(listing -> listing.add("exports {N}{;}", packageName));
  }

  public void permits(Name moduleName) {
    directives.add(listing -> listing.add("permits {N}{;}", moduleName));
  }

  public void entryPoint(Name typeName) {
    directives.add(listing -> listing.add("class {N}{;}", typeName));
  }
}
