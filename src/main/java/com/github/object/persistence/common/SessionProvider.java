package com.github.object.persistence.common;

import com.github.object.persistence.api.session.Session;
import com.github.object.persistence.api.session.SessionFactory;
import com.github.object.persistence.sql.impl.SqlFactoryImpl;

/**
 * Знает о всех фабриках и умеет доставать из них сесиии
 *
 */
// предполагается, что этот класс создастся на каком-нибудь этапе инициализации
// не уверен насчет корректности реализации данного класса, мб синглтон должен быть реализован по другому.
// + подумать перенести ли сюда десайды из процессора
public class SessionProvider {
    private final SessionFactory factory;
    private static final SessionProvider INSTANCE = new SessionProvider(decideFactory());

    //static?
    public Session createSession() {
        return factory.openSession();
    }

    private SessionProvider(SessionFactory factory) {
        this.factory = factory;
    }

    private static SessionFactory decideFactory(){
        switch (ConfigDataSource.INSTANCE.getDataSourceType()) {
            case RELATIONAL:
                return new SqlFactoryImpl();
            default:
                throw new UnsupportedOperationException("Other db types are not supported yet");
        }
    }

    public static SessionProvider getInstance() {
        return INSTANCE;
    }
}
