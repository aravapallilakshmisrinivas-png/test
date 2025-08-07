package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigurationManager {
    private static ConfigurationManager instance;
    private final ConcurrentHashMap<String, Object> configCache = new ConcurrentHashMap<>();
    private final Properties properties = new Properties();

    private ConfigurationManager() {
        loadConfiguration();
    }

    public static ConfigurationManager getInstance() {
        if (instance == null) {
            synchronized (ConfigurationManager.class) {
                if (instance == null) {
                    instance = new ConfigurationManager();
                }
            }
        }
        return instance;
    }

    private void loadConfiguration() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
                properties.forEach((key, value) -> configCache.put(key.toString(), value));
            }
        } catch (IOException e) {
            System.err.println("Failed to load configuration: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key, Class<T> type) {
        Object value = configCache.get(key);
        if (value == null) return null;
        
        if (type == String.class) return (T) value.toString();
        if (type == Integer.class) return (T) Integer.valueOf(value.toString());
        if (type == Double.class) return (T) Double.valueOf(value.toString());
        if (type == Boolean.class) return (T) Boolean.valueOf(value.toString());
        
        return (T) value;
    }

    public void reloadConfiguration() {
        configCache.clear();
        loadConfiguration();
    }
}