package com.github.object.persistence.sql.impl;

import com.github.object.persistence.common.ConfigDataSource;
import com.github.object.persistence.common.ConnectionInstaller;
import com.github.object.persistence.common.DataSourceWrapper;
import com.github.object.persistence.exception.InstallConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SqlConnectionInstallerImpl implements ConnectionInstaller<Connection> {
    private final String url;
    private final Properties properties;

    public SqlConnectionInstallerImpl() {
        try {
            Class.forName(ConfigDataSource.INSTANCE.getDriver());
            this.url = ConfigDataSource.INSTANCE.getDataSourceUrl();
            Properties datasourceProperties = new Properties();
            datasourceProperties.setProperty("user", ConfigDataSource.INSTANCE.getUsername());
            datasourceProperties.setProperty("password", ConfigDataSource.INSTANCE.getPassword());
            this.properties = datasourceProperties;
        } catch (ClassNotFoundException exception) {
            throw new InstallConnectionException("The specified driver was not found", exception);
        }
    }

    @Override
    public DataSourceWrapper<Connection> installConnection() {
        try {
            Connection connection = DriverManager.getConnection(url, properties);
            return new DataSourceWrapperImpl<>(connection);
        } catch (SQLException exception) {
            throw new InstallConnectionException("Exception while trying to connect to database", exception);
        }
    }
}
