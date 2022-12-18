package com.github.object.persistence.sql.impl;

import com.github.object.persistence.api.session.Session;
import com.github.object.persistence.api.session.SessionFactory;

public class SqlFactoryImpl implements SessionFactory {
    private final SqlConnectionInstallerImpl connectionProvider;

    //тред-пул с сессиями?

    public SqlFactoryImpl(){
        connectionProvider = new SqlConnectionInstallerImpl();
    }

    // доставание из пула сессии и открытие соединения через датасурс враппер?
    @Override
    public Session openSession() {

        return null;
    }
}
