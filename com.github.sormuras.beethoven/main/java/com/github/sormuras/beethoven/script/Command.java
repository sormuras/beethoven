package com.github.sormuras.beethoven.script;

import com.github.sormuras.beethoven.Listing;

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
