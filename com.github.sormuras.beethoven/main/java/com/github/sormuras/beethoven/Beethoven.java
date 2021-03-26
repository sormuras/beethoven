package com.github.sormuras.beethoven;

public interface Beethoven {

  String VERSION = "1.0-SNAPSHOT";

  static void main(String... args) {
    System.out.println("# Base package name and version constant");
    System.out.println(Beethoven.class.getPackage().getName() + " " + VERSION);
  }
}
