package com.github.sormuras.beethoven.script;

import com.github.sormuras.beethoven.Listing;

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
