package com.github.object.persistence.common;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;

@SupportedAnnotationTypes("javax.persistence.Entity")
@AutoService(Processor.class)
public class EntityProcessor extends AbstractProcessor {
    private final Messager logger = processingEnv.getMessager();
    private final EntityValidator validator = new EntityValidator();
    private static final String PACKAGE = "entity.metamodel";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            logger.printMessage(Diagnostic.Kind.NOTE, "Annotations not found");
            return false;
        }

        annotations.forEach(annotation -> {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : elements) {
                final TypeElement object = (TypeElement) element.getEnclosingElement();
                try {
                    Class<?> entityClass = Class.forName(object.getQualifiedName().toString());
                    validator.validateEntity(entityClass);
                    create(entityClass);
                } catch (ClassNotFoundException e) {
                    logger.printMessage(Diagnostic.Kind.ERROR, String.format("Class with name %s not found", object.getQualifiedName()));
                }
            }
        });

        return true;
    }

    /**
     * Создание мета-модели
     *
     * @param entityClass класс, помеченный аннотацией
     */
    /* hello world с использованием javapoet. изначально планировал кодогенерацию для технических нужд.
    * то есть мы сохраняем все мета-модели в определенном пакете и при запуске нашего runnable джарника класс-инициализатор
    * будет сканить эту директорию и пробовать создавать таблицы. Какие тогда нужно сохранять параметры?
    * мета модель должна содержать минимум данных и не должна копировать исходную ентити, иначе какой в ней смысл
    * подумал вместо типов использовать typeWrapper, но тогда это завязывание на sql. */
    private void create(Class<?> entityClass) {
        try {
            final TypeSpec typeSpec = TypeSpec.classBuilder(entityClass.getSimpleName() + "_")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(
                            AnnotationSpec.builder(Generated.class)
                                    .addMember("value", "$S", this.getClass().getName())
                                    .build()
                    )
                    .build();
            JavaFile javaFile = JavaFile.builder(PACKAGE, typeSpec).addFileComment("Generated meta model").build();
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException exception) {
            logger.printMessage(Diagnostic.Kind.ERROR, String.format("Error while creating meta model for class %s", entityClass.getName()));
        }
    }
}
