package ai.dragon.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import ai.dragon.entity.SiloEntity;
import ai.dragon.properties.embedding.EmbeddingSettings;
import ai.dragon.repository.SiloRepository;
import ai.dragon.util.KVSettingUtil;
import dev.langchain4j.model.embedding.EmbeddingModel;

@Service
public class EmbeddingModelService {
    @Autowired
    private SiloRepository siloRepository;

    public EmbeddingModel modelForSilo(UUID siloUuid) throws Exception {
        SiloEntity siloEntity = siloRepository.getByUuid(siloUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Silo not found"));
        return modelForSilo(siloEntity);
    }

    public EmbeddingModel modelForSilo(SiloEntity siloEntity) throws Exception {
        EmbeddingSettings embeddingSettings = KVSettingUtil.kvSettingsToObject(
                siloEntity.getEmbeddingSettings(), EmbeddingSettings.class);
        return siloEntity.getEmbeddingModel().getModelDefinition().getEmbeddingModelWithSettings()
                .apply(embeddingSettings);
    }
}
