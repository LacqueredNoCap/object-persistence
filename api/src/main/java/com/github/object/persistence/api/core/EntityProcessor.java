package com.github.object.persistence.api.core;

import com.google.auto.service.AutoService;
import org.atteo.classindex.processor.ClassIndexProcessor;

import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.persistence.Entity;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("javax.persistence.Entity")
@AutoService(Processor.class)
public class EntityProcessor extends ClassIndexProcessor {

    private final Messager logger;
    private final EntityValidator entityValidator;

    public EntityProcessor() {
        logger = processingEnv.getMessager();
        entityValidator = EntityValidator.getInstance();
        indexAnnotations(Entity.class);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            logger.printMessage(Diagnostic.Kind.NOTE, "Annotations not found");
            return false;
        }

        TypeElement annotation = annotations.iterator().next();

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
        for (Element element : elements) {
            final TypeElement object = (TypeElement) element.getEnclosingElement();
            try {
                Class<?> entityClass = Class.forName(object.getQualifiedName().toString());
                entityValidator.validateEntity(entityClass);
            } catch (ClassNotFoundException e) {
                logger.printMessage(Diagnostic.Kind.ERROR, String.format("Class with name %s not found", object.getQualifiedName()));
                return false;
            }
        }

        super.process(annotations, roundEnv);
        return true;
    }

}
