package ai.dragon.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import ai.dragon.properties.loader.FileSystemIngestorLoaderSettings;

@ActiveProfiles("test")
public class KVSettingUtilTest {
    @Test
    void stringToField() {
        List<String> settingsList = new ArrayList<>();
        settingsList.add("pathMatcher=glob:**");
        FileSystemIngestorLoaderSettings settings = KVSettingUtil.kvSettingsToObject(settingsList,
                FileSystemIngestorLoaderSettings.class);
        assertEquals("glob:**", settings.getPathMatcher());
    }

    @Test
    void arrayToField() {
        List<String> settingsList = new ArrayList<>();
        settingsList.add("paths[]=/path1");
        settingsList.add("paths[]=/path2");
        FileSystemIngestorLoaderSettings settings = KVSettingUtil.kvSettingsToObject(settingsList,
                FileSystemIngestorLoaderSettings.class);
        assertNotNull(settings);
        assertEquals(2, settings.getPaths().size());
        assertEquals("/path1", settings.getPaths().get(0));
        assertEquals("/path2", settings.getPaths().get(1));
    }
}
