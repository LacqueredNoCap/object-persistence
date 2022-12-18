package com.github.object.persistence.common;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public final class ConfigDataSource {
    public static final ConfigDataSource INSTANCE = new ConfigDataSource();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String CONFIG_NAME = "persistence.properties";
    private final Properties properties = loadConfig();

    private ConfigDataSource() {
    }

    private Properties loadConfig() {
        try {
            Properties prop = new Properties();
            prop.load(this.getClass().getClassLoader().getResourceAsStream(CONFIG_NAME));
            return prop;
        } catch (IOException exception) {
            logger.error("Error while loading config: ", exception);
            throw new NullPointerException("Config is not present");
        }
    }

    public DbTypes getDataSourceType() {
        return DbTypes.getType(properties.getProperty("persistence.type"));
    }

    public String getDataSourceUrl() {
        return properties.getProperty("persistence.url");
    }

    public String getUsername() {
        return properties.getProperty("persistence.username");
    }

    public String getPassword() {
        return properties.getProperty("persistence.password");
    }

    public String getDriver() {
        return properties.getProperty("persistence.driver");
    }
}

