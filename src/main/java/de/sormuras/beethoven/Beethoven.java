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

package de.sormuras.beethoven;

import java.lang.module.ModuleDescriptor;

public interface Beethoven {

  String VERSION = "1.0-SNAPSHOT";

  static void main(String[] args) {
    System.out.println("# Base package name and version constant");
    System.out.println(Beethoven.class.getPackage().getName() + " " + VERSION);
    System.out.println("# Module description");
    ModuleDescriptor descriptor = Beethoven.class.getModule().getDescriptor();
    System.out.format("Name and version: %s%n", descriptor.toNameAndVersion());
    System.out.format("Requires modules: %s%n", descriptor.requires());
    System.out.format("Exports packages: %s%n", descriptor.exports());
  }
}
