package ai.dragon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.dragon.entity.SiloEntity;
import ai.dragon.properties.embedding.EmbeddingSettings;
import dev.langchain4j.model.embedding.EmbeddingModel;

@Service
public class EmbeddingModelService {
    @Autowired
    private KVSettingService kvSettingService;

    public EmbeddingModel modelForEntity(SiloEntity siloEntity) throws Exception {
        EmbeddingSettings embeddingSettings = kvSettingService.kvSettingsToObject(
                siloEntity.getEmbeddingSettings(), EmbeddingSettings.class);
        return siloEntity.getEmbeddingModel().getModelDefinition().getEmbeddingModelWithSettings()
                .apply(embeddingSettings);
    }
}
