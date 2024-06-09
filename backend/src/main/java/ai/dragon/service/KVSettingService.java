package ai.dragon.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KVSettingService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public <T> T kvSettingsToObject(List<String> settings, Class<T> clazz) {
        Map<String, String> settingsMap = new HashMap<>();
        if (settings != null) {
            for (String setting : settings) {
                String[] parts = setting.split("=", 2);
                if (parts.length <= 1) {
                    logger.error("Invalid setting '{}' for class '{}'", setting, clazz.getName());
                    continue;
                }
                String key = parts[0].trim();
                String value = parts[1].trim();
                settingsMap.put(key, value);
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(settingsMap, clazz);
    }
}
