package de.sormuras.beethoven.script;

import de.sormuras.beethoven.Listing;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class Script {

  public static String eval(String source, Object... args) {
    return new Script(source).eval(new Listing(), args).toString();
  }

  public static String eval(String source, Map<String, Object> map) {
    return new Script(source).eval(new Listing(), map).toString();
  }

  private final String source;

  Script(String source) {
    this.source = source;
  }

  public Listing eval(Listing listing, Object... args) {
    Map<String, Object> map = new LinkedHashMap<>();
    IntStream.range(0, args.length).forEach(i -> map.put(Integer.toString(i), args[i]));
    return eval(listing, map);
  }

  public Listing eval(Listing listing, Map<String, Object> map) {
    return listing;
  }
}
