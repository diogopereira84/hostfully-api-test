package utils;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class AppSettings {
    private static final Properties properties = new Properties();
    private static final String ENVIRONMENT = System.getenv("APP_ENV") != null ? System.getenv("APP_ENV") : "QA";

    static {
        String configFileName = "appsettings-" + ENVIRONMENT.toLowerCase() + ".properties";
        try (InputStream input = AppSettings.class.getClassLoader().getResourceAsStream(configFileName)) {
            if (input != null) {
                properties.load(input);
            } else {
                throw new RuntimeException("Configuration file not found: " + configFileName);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load application settings from " + configFileName, e);
        }
    }

    public static String get(String key) {
        return System.getenv(key) != null ? System.getenv(key) : properties.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return get(key) != null ? get(key) : defaultValue;
    }
}
