package ai.dragon.util.langchain4j.web.search.searxng;

import java.io.IOException;
import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import dev.langchain4j.internal.Utils;
import lombok.Builder;
import lombok.Getter;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class SearXNGClient {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private final SearXNGApi searXNGApi;

    @Getter
    private String baseUrl;

    @Builder
    public SearXNGClient(String baseUrl, Duration timeout) {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                // .addInterceptor(new OkHttpClientInterceptor())
                .callTimeout(Utils.getOrDefault(timeout, Duration.ofSeconds(10)))
                .connectTimeout(Utils.getOrDefault(timeout, Duration.ofSeconds(10)))
                .readTimeout(Utils.getOrDefault(timeout, Duration.ofSeconds(10)))
                .writeTimeout(Utils.getOrDefault(timeout, Duration.ofSeconds(10)));

        this.baseUrl = Utils.ensureTrailingForwardSlash(baseUrl);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.baseUrl)
                .client(okHttpClientBuilder.build())
                .addConverterFactory(JacksonConverterFactory.create(OBJECT_MAPPER))
                .build();

        this.searXNGApi = retrofit.create(SearXNGApi.class);
    }

    public SearXNGResponse search(SearXNGSearchRequest searchRequest) {
        try {
            Response<SearXNGResponse> retrofitResponse = searXNGApi
                    .search(searchRequest.getQ(),
                            searchRequest.getCategories(),
                            searchRequest.getEngines(),
                            searchRequest.getLanguage(),
                            searchRequest.getPageno(),
                            searchRequest.getTimeRange(),
                            searchRequest.getFormat(),
                            searchRequest.getSafeSearch())
                    .execute();
            if (retrofitResponse.isSuccessful()) {
                return retrofitResponse.body();
            } else {
                throw toException(retrofitResponse);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static RuntimeException toException(Response<?> response) throws IOException {
        int code = response.code();
        String body = response.errorBody().string();
        String errorMessage = String.format("status code: %s; body: %s", code, body);
        return new RuntimeException(errorMessage);
    }
}
