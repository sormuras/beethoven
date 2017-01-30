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

package de.sormuras.beethoven;

import de.sormuras.beethoven.type.Type;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

public class Counter extends AbstractProcessor {

  public @interface Mark {}

  public final List<Element> marked = new ArrayList<>();
  public final Map<String, Type> types = new HashMap<>();
  public final List<Annotation> annotations = new ArrayList<>();
  public Elements elementUtils;
  public Types typeUtils;
  public boolean createAbc = true;

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latest();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> set = new HashSet<>();
    set.add(Mark.class.getCanonicalName());
    return set;
  }

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.elementUtils = processingEnv.getElementUtils();
    this.typeUtils = processingEnv.getTypeUtils();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    // System.out.println("\n" +roundEnv);
    if (roundEnv.processingOver()) {
      return true;
    }

    for (Element root : roundEnv.getRootElements()) {
      this.annotations.addAll(
          root.getAnnotationMirrors()
              .stream()
              .map(Type.Mirrors::annotation)
              .collect(Collectors.toList()));
    }
    roundEnv.getElementsAnnotatedWith(Mark.class).forEach(marked::add);
    for (Element element : marked) {
      TypeMirror mirror = element.asType();
      if (element instanceof ExecutableElement) {
        mirror = ((ExecutableElement) element).getReturnType();
      }
      String name = element.getSimpleName().toString();
      Type type = Type.type(mirror);
      types.put(name, type);
    }

    if (createAbc) {
      createAbc = false;
      try {
        JavaFileObject src = processingEnv.getFiler().createSourceFile("test.Abc");
        PrintWriter writer = new PrintWriter(src.openWriter());
        writer.println("package test; class Abc {}");
        writer.close();
      } catch (Exception e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "" + e);
      }
    }

    return true;
  }
}
