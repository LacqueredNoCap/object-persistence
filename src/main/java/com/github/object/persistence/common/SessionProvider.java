package com.github.object.persistence.common;

import com.github.object.persistence.api.session.Session;
import com.github.object.persistence.api.session.SessionFactory;
import com.github.object.persistence.sql.impl.SqlConnectionInstallerImpl;
import com.github.object.persistence.sql.impl.SqlFactoryImpl;

import java.util.Map;

/**
 * Знает о всех фабриках и умеет доставать из них сесиии
 */
public class SessionProvider {
    private static final SessionProvider INSTANCE = new SessionProvider();
    private final Map<DbTypes, SessionFactory> factories = initFactories();

    private Map<DbTypes, SessionFactory> initFactories() {
        return Map.ofEntries(
                Map.entry(DbTypes.RELATIONAL, new SqlFactoryImpl(new SqlConnectionInstallerImpl()))
        );
    }

    public static SessionProvider getInstance() {
        return INSTANCE;
    }

    public Session createSession() {
        return decideSession().openSession();
    }

    private SessionFactory decideSession() {
        SessionFactory factory = factories.get(ConfigDataSource.INSTANCE.getDataSourceType());
        if (factory == null) {
            String message = String.format("Factory with name %s is not present in provider", DbTypes.RELATIONAL.typeName);
            throw new IllegalStateException(message);
        }
        return factory;
    }
}
