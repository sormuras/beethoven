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

package de.sormuras.beethoven.script;

import de.sormuras.beethoven.Listing;

@FunctionalInterface
public interface Action {

  enum Consumes {
    NONE,
    TAG,
    ARG,
    ALL;

    public boolean arg() {
      return this == ARG || this == ALL;
    }
  }

  Listing execute(Listing listing, String tag, Object arg);

  default Consumes consumes() {
    return Consumes.ALL;
  }

  default boolean handles(String tag) {
    return false;
  }
}
