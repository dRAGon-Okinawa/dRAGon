package ai.dragon.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class KVSettingUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(KVSettingUtil.class);

    @SuppressWarnings("unchecked")
    public static <T> T kvSettingsToObject(List<String> settings, Class<T> clazz) {
        Map<String, Object> settingsMap = new HashMap<>();
        if (settings != null) {
            for (String setting : settings) {
                String[] parts = setting.split("=", 2);
                if (parts.length <= 1) {
                    LOGGER.error("Invalid setting '{}' for class '{}'", setting, clazz.getName());
                    continue;
                }
                String key = parts[0].trim();
                String value = parts[1].trim();
                if (key.endsWith("[]")) {
                    String arrayKey = key.substring(0, key.length() - 2);
                    List<String> values = (List<String>) settingsMap.get(arrayKey);
                    if (values == null) {
                        values = new ArrayList<>();
                        settingsMap.put(arrayKey, values);
                    }
                    values.add(value);
                } else {
                    settingsMap.put(key, value);
                }
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(settingsMap, clazz);
    }
}
