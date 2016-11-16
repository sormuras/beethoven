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

import de.sormuras.beethoven.Listing;

/** Compiled script execution command. */
public class Command {

  final String tag;
  final String selector;
  final Action action;

  public Command(String text) {
    this.tag = text;
    this.selector = null;
    this.action = null;
  }

  public Command(String tag, String selector, Action action) {
    this.tag = tag;
    this.selector = selector;
    this.action = action;
  }

  public Listing execute(Listing listing, Object argument) {
    if (action == null) {
      return listing.add(tag);
    }
    return action.execute(listing, tag, argument);
  }

  public boolean consumesArgument() {
    return action != null && action.consumes().arg();
  }

  @Override
  public String toString() {
    String quoted = "`" + tag + "`";
    if (action == null) {
      return quoted;
    }
    String quotedAndAction = quoted + " -> " + action;
    if (!consumesArgument()) {
      return quotedAndAction;
    }
    return quotedAndAction + "[:" + selector + "]";
  }
}
