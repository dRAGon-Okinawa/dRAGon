package ai.dragon.service;

import org.springframework.stereotype.Service;

import ai.dragon.entity.SiloEntity;
import ai.dragon.properties.embedding.EmbeddingModelSettings;
import ai.dragon.util.IniSettingUtil;
import dev.langchain4j.model.embedding.EmbeddingModel;

@Service
public class EmbeddingModelService {
    public EmbeddingModel modelForEntity(SiloEntity siloEntity) throws Exception {
        EmbeddingModelSettings embeddingModelSettings = IniSettingUtil.convertIniSettingsToObject(
                siloEntity.getEmbeddingModelSettings(), EmbeddingModelSettings.class);
        return siloEntity.getEmbeddingModelType().getModelDefinition().getEmbeddingModelWithSettings()
                .apply(embeddingModelSettings);
    }
}
