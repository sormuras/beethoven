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

package de.sormuras.beethoven.script;

class Part {

  final Tag tag;
  final String selector;
  final String snippet;

  Part(String literal) {
    this.tag = null;
    this.selector = null;
    this.snippet = literal;
  }

  Part(Tag tag, String key, String snippet) {
    this.tag = tag;
    this.selector = key;
    this.snippet = snippet;
  }

  @Override
  public String toString() {
    String quoted = "`" + snippet + "`";
    return tag == null ? quoted : quoted + " -> " + tag + "[:" + selector + "]";
  }
}
