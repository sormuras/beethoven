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

package de.sormuras.beethoven.composer;

import de.sormuras.beethoven.Listing;
import de.sormuras.beethoven.Name;
import de.sormuras.beethoven.Style;
import de.sormuras.beethoven.unit.CompilationUnit;
import de.sormuras.beethoven.unit.ImportDeclarations;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.UnaryOperator;

public class ImportsComposer implements UnaryOperator<CompilationUnit> {

  private boolean removeUnused = true;

  public boolean isRemoveUnused() {
    return removeUnused;
  }

  public ImportsComposer setRemoveUnused(boolean removeUnused) {
    this.removeUnused = removeUnused;
    return this;
  }

  @Override
  public CompilationUnit apply(CompilationUnit unit) {
    Listing listing = new Listing("  ", "\n", Style.CANONICAL.styling());
    unit.list(listing);

    ImportDeclarations imports = unit.getImportDeclarations();
    Set<String> simpleNames = new TreeSet<>();
    Map<Name, Style> map = new LinkedHashMap<>();

    for (Name name : listing.getCollectedNames()) {
      String simpleName = name.simpleNames();
      if (simpleNames.contains(simpleName)) {
        map.put(name, Style.CANONICAL);
        continue;
      }
      simpleNames.add(simpleName);
      map.put(name, Style.SIMPLE);
      if (name.isMemberReference()) {
        // System.out.println("member:" + name);
        // map.put(name, Style.LAST);
        // imports.getSingleStaticImports().add(name);
        map.put(name.enclosing(), Style.SIMPLE);
        addSingleTypeImport(unit, name.enclosing());
      } else {
        addSingleTypeImport(unit, name);
      }
    }

    // remove unused imports -- i.e. remove all not mapped ones.
    if (isRemoveUnused()) {
      imports.getSingleTypeImports().retainAll(map.keySet());
    }

    unit.setNameStyleMap(map);
    return unit;
  }

  private void addSingleTypeImport(CompilationUnit unit, Name name) {
    if (unit.getImportDeclarations().getSingleTypeImports().contains(name)) {
      return;
    }
    if (name.isJavaLangPackage()) {
      return;
    }
    if (name.packageName().equals(unit.getPackageName())) {
      return;
    }
    unit.getImportDeclarations().addSingleTypeImport(name);
  }
}
