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

package de.sormuras.beethoven.composer;

import de.sormuras.beethoven.Listing;
import de.sormuras.beethoven.Name;
import de.sormuras.beethoven.Style;
import de.sormuras.beethoven.unit.CompilationUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.UnaryOperator;

public class ImportsComposer implements UnaryOperator<CompilationUnit> {

  @Override
  public CompilationUnit apply(CompilationUnit unit) {
    Listing listing = new Listing("  ", "\n", Style.CANONICAL.styling());
    unit.list(listing);

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
      if (name.isJavaLangPackage()) {
        continue;
      }
      if (name.packageName().equals(unit.getPackageName())) {
        continue;
      }
      unit.getImportDeclarations().addSingleTypeImport(name);
    }

    unit.setNameStyleMap(map);
    return unit;
  }
}
