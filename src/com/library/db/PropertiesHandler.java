package com.library.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesHandler {
    private final Properties properties = new Properties();

    public PropertiesHandler(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Get a property by its key
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    // Get all properties
    public Properties getProperties() {
        return properties;
    }
}
