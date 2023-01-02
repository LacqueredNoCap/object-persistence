package com.github.object.persistence.sql.impl;

import com.github.object.persistence.api.session.Session;
import com.github.object.persistence.api.session.SessionFactory;
import com.github.object.persistence.common.ConnectionInstaller;
import org.atteo.classindex.ClassIndex;

import javax.persistence.Entity;
import java.sql.Connection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class SqlFactoryImpl implements SessionFactory {
    private final ConnectionInstaller<Connection> installer;

    public SqlFactoryImpl(ConnectionInstaller<Connection> installer) {
        this.installer = installer;
        initializeDatasource();
    }

    @Override
    public Session openSession() {
        return new SqlSession(installer.installConnection());
    }

    @Override
    public Session getCurrentSession() {
        return null;
    }

    @Override
    public void initializeDatasource() {
        Iterable<Class<?>> entityClasses = ClassIndex.getAnnotated(Entity.class);
        installer.installConnection().execute(validateAndCreateTables(entityClasses));
    }

    private String validateAndCreateTables(Iterable<Class<?>> entityClasses) {
        return StreamSupport
                .stream(entityClasses.spliterator(), false)
                .map(kClass -> SqlGenerator.getInstance().createTable(kClass))
                .collect(Collectors.joining(" "));
    }
}
