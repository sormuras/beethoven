package com.github.sormuras.beethoven;

import com.github.sormuras.beethoven.type.Type;
import com.sun.source.tree.Tree;
import com.sun.source.util.Trees;
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

public class Counter extends AbstractProcessor {

  public @interface Mark {}

  public final List<Element> marked = new ArrayList<>();
  public final Map<String, Tree> trees = new HashMap<>();
  public final Map<String, Type> types = new HashMap<>();
  public final List<Annotation> annotations = new ArrayList<>();
  public Elements elementUtils;
  public Types typeUtils;

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
    Trees treeUtils = Trees.instance(processingEnv);

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
      Tree tree = treeUtils.getTree(element);
      types.put(name, type);
      trees.put(name, tree);
    }
    return true;
  }
}
