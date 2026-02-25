package com.uitesting.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigManager - Reads configuration from classpath resources and system properties.
 *
 * <p>Priority chain (highest → lowest):
 * <ol>
 *   <li>JVM system properties (-Dkey=value passed via Maven Surefire)</li>
 *   <li>env/{env}.properties — loaded from the test classpath at runtime;
 *       active env determined by the system property {@code env} (default: "qa")</li>
 *   <li>config.properties — base defaults, packaged inside framework-ui.jar</li>
 *   <li>Hard-coded defaults in each getter method</li>
 * </ol>
 *
 * <p>Supported environments: local, dev, qa, uat, stage, prod
 * (activated via Maven profiles -Plocal / -Pdev / -Pqa / -Puat / -Pstage / -Pprod,
 *  which set -Denv=&lt;name&gt; through Surefire systemPropertyVariables)
 */
public class ConfigManager {

    private static ConfigManager instance;
    private final Properties properties;

    private ConfigManager() {
        properties = new Properties();

        // 1. Load base config from JAR classpath (config.properties in src/main/resources)
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (is != null) {
                properties.load(is);
            } else {
                System.out.println("[ConfigManager] config.properties not found on classpath — using hard-coded defaults");
            }
        } catch (IOException e) {
            System.out.println("[ConfigManager] Error loading config.properties: " + e.getMessage());
        }

        // 2. Overlay env-specific properties.
        //    env/{env}.properties lives in tests/src/test/resources/env/ which is on the
        //    Surefire classpath at runtime, so the classloader finds it even though this
        //    class is compiled into framework-ui.jar.
        String env = System.getProperty("env", "qa");
        try (InputStream envIs = getClass().getClassLoader()
                .getResourceAsStream("env/" + env + ".properties")) {
            if (envIs != null) {
                Properties envProps = new Properties();
                envProps.load(envIs);
                properties.putAll(envProps);   // env-specific values override base config
                System.out.println("[ConfigManager] Loaded env/" + env + ".properties");
            } else {
                System.out.println("[ConfigManager] env/" + env + ".properties not found — using config.properties defaults");
            }
        } catch (IOException e) {
            System.out.println("[ConfigManager] Error loading env/" + env + ".properties: " + e.getMessage());
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

    /**
     * Returns the value for {@code key}, preferring a JVM system property over the
     * loaded file properties.
     */
    public String get(String key) {
        // System properties (e.g. -DbaseUrl=...) always win
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
