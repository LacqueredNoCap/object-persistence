package com.github.object.persistence.common;

import com.github.object.persistence.exception.InstallConnectionException;
import com.github.object.persistence.sql.impl.SqlAnnotationParser;
import com.github.object.persistence.sql.impl.SqlConnectionInstallerImpl;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("javax.persistence.Entity")
@AutoService(Processor.class)
public class EntityProcessor extends AbstractProcessor {
    private final Messager logger = processingEnv.getMessager();

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
                    prepareEntity(entityClass);
                } catch (ClassNotFoundException e) {
                    logger.printMessage(Diagnostic.Kind.ERROR, String.format("Class with name %s not found", object.getQualifiedName()));
                }
            }
        });

        return true;
    }

    /*с выполнением стейтмента возникнут проблемы, если будут какие-то связи  в базах и мы попытаемся
    * установить foreign key от несуществующей таблицы. Поэтому надо либо всё это выполнять вместе транзакцией либо на данном
    * этапе проверять только корректную взаимосвязь между entity, тогда не надо будет завязываться на разных реализациях
    * AnnotationProcessor'ов. Ну и это кажется корректный вариант, потому что процессинг идет на этапе компиляции, а создание таблиц
    * будет в данном случае сайд эффектом. Также было бы неплохо построить что-то типа дерева зависимостей на данном этапе*/
    private <T> void prepareEntity(Class<T> entityClass) {
        try (DataSourceWrapper<?> dataSourceWrapper = decideInstaller()) {
            dataSourceWrapper.execute(decideParser().prepareTable(entityClass));
        } catch (InstallConnectionException exception) {
            logger.printMessage(Diagnostic.Kind.ERROR, exception.getMessage());
        }
    }

    private DataSourceWrapper<?> decideInstaller() {
        ConnectionInstaller<?> installer;
        switch (ConfigDataSource.INSTANCE.getDataSourceType()) {
            case RELATIONAL:
                installer = new SqlConnectionInstallerImpl();
                return installer.installConnection();
            default:
                throw new UnsupportedOperationException("Other db types are not supported yet");
        }
    }

    private AnnotationParser decideParser() {
        switch (ConfigDataSource.INSTANCE.getDataSourceType()) {
            case RELATIONAL:
                return new SqlAnnotationParser();
            default:
                throw new UnsupportedOperationException("Other db types are not supported yet");
        }
    }
}
