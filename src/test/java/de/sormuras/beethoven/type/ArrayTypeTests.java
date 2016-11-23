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

package de.sormuras.beethoven.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.sormuras.beethoven.Annotation;
import de.sormuras.beethoven.Listing;
import de.sormuras.beethoven.Name;

import java.util.List;
import org.junit.jupiter.api.Test;

class ArrayTypeTests {

  @Test
  void arrayType() {
    assertEquals("byte[]", ArrayType.array(byte.class, 1).list());
    assertEquals("byte[][][]", ArrayType.array(Type.type(byte.class), 3).list());
    assertEquals("byte[][][]", Type.type(byte[][][].class).list());
  }

  @Test
  void arrayTypeWithAnnotatedDimensions() {
    ArrayType.Dimension[] dimensions = {
      new ArrayType.Dimension(List.of(Annotation.cast("A"))),
      new ArrayType.Dimension(List.of(Annotation.cast("B"), Annotation.cast("C"))),
      new ArrayType.Dimension(List.of(Annotation.cast("D")))
    };
    ArrayType actual = ArrayType.array(Type.type(byte.class), List.of(dimensions));
    assertEquals("byte@A []@B @C []@D []", actual.list());
  }

  @Test
  void arrayComponentTypeNameIsCollected() {
    Listing listing = new Listing();
    listing.add(Type.type(Byte[][][].class));
    assertTrue(listing.getCollectedNames().contains(Name.name(Byte.class)));
  }
}
