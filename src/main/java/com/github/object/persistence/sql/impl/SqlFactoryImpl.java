package com.github.object.persistence.sql.impl;

import com.github.object.persistence.api.session.Session;
import com.github.object.persistence.api.session.SessionFactory;
import com.github.object.persistence.common.ConfigDataSource;
import com.github.object.persistence.common.ConnectionInstaller;
import com.github.object.persistence.common.DataSourceWrapper;
import org.atteo.classindex.ClassIndex;

import javax.persistence.Entity;
import java.sql.Connection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class SqlFactoryImpl implements SessionFactory {

    private final ConnectionInstaller<Connection> installer;
    private final FromSqlToObjectMapper<Connection> mapper;

    public SqlFactoryImpl(ConnectionInstaller<Connection> installer) {
        this.installer = installer;
        this.mapper = new FromSqlToObjectMapper<>(SqlGenerator.getInstance());
        initializeDatasource();
    }

    @Override
    public Session openSession() {
        DataSourceWrapper<Connection> wrapper = installer.installConnection();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1,
                1,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>()
        );
        return new SqlSession(wrapper, mapper, executor);
    }

    @Override
    public void initializeDatasource() {
        if (ConfigDataSource.getInstance().isInitializeNeeded()) {
            Iterable<Class<?>> entityClasses = ClassIndex.getAnnotated(Entity.class);
            installer.installConnection().execute(validateAndCreateTables(entityClasses));
        }
    }

    private String validateAndCreateTables(Iterable<Class<?>> entityClasses) {
        return StreamSupport
                .stream(entityClasses.spliterator(), false)
                .map(kClass -> SqlGenerator.getInstance().createTable(kClass))
                .collect(Collectors.joining(" "));
    }
}
