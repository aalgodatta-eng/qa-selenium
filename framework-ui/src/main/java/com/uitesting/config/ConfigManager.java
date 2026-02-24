package com.uitesting.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * ConfigManager - Reads configuration from config.properties or system properties
 */
public class ConfigManager {

    private static ConfigManager instance;
    private Properties properties;

    private static final String CONFIG_FILE = "src/test/resources/config.properties";

    private ConfigManager() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
        } catch (IOException e) {
            System.out.println("config.properties not found, using defaults / system properties");
        }
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    public String get(String key) {
        // System properties override file properties
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isEmpty()) {
            return sysProp;
        }
        return properties.getProperty(key);
    }

    public String get(String key, String defaultValue) {
        String value = get(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    public String getBaseUrl() {
        return get("baseUrl", "http://uitestingplayground.com");
    }

    public String getBrowser() {
        return get("browser", "chrome");
    }

    public boolean isHeadless() {
        return Boolean.parseBoolean(get("headless", "false"));
    }

    public int getImplicitWait() {
        return Integer.parseInt(get("implicitWait", "10"));
    }

    public int getExplicitWait() {
        return Integer.parseInt(get("explicitWait", "30"));
    }

    public int getPageLoadTimeout() {
        return Integer.parseInt(get("pageLoadTimeout", "60"));
    }
}
