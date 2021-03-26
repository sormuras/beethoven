package test.integration.type;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.sormuras.beethoven.Annotation;
import com.github.sormuras.beethoven.Listing;
import com.github.sormuras.beethoven.Name;
import com.github.sormuras.beethoven.type.ArrayType;
import com.github.sormuras.beethoven.type.Type;
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
      new ArrayType.Dimension(singletonList(Annotation.cast("A"))),
      new ArrayType.Dimension(asList(Annotation.cast("B"), Annotation.cast("C"))),
      new ArrayType.Dimension(singletonList(Annotation.cast("D")))
    };
    ArrayType actual = ArrayType.array(Type.type(byte.class), asList(dimensions));
    assertEquals("byte@A []@B @C []@D []", actual.list());
  }

  @Test
  void arrayComponentTypeNameIsCollected() {
    Listing listing = new Listing();
    listing.add(Type.type(Byte[][][].class));
    assertTrue(listing.getCollectedNames().contains(Name.name(Byte.class)));
  }

  @Test
  void dimensions() {
    assertThrows(IllegalArgumentException.class, () -> ArrayType.dimensions(0, null));
  }

  @Test
  void empty() {
    assertFalse(ArrayType.array(int.class, 1).isEmpty());
  }
}
