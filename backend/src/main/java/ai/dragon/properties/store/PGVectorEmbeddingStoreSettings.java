package ai.dragon.properties.store;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ai.dragon.util.SettingsExpressionParserUtil;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PGVectorEmbeddingStoreSettings {
    private String host;
    private Integer port;
    private String database;
    private String user;
    private String password;

    public PGVectorEmbeddingStoreSettings() {
        port = 5432;
    }

    public void setUser(String userOrExpression) {
        this.user = SettingsExpressionParserUtil.parse(userOrExpression, userOrExpression, String.class);
    }

    public void setPassword(String passwordOrExpression) {
        this.password = SettingsExpressionParserUtil.parse(passwordOrExpression, passwordOrExpression, String.class);
    }
}
