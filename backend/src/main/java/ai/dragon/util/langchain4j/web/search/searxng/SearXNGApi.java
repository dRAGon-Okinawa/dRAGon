package ai.dragon.util.langchain4j.web.search.searxng;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SearXNGApi {
    @POST("/search")
    @FormUrlEncoded
    Call<SearXNGResponse> search(@Field("q") String query,
            @Field("categories") String categories,
            @Field("engines") String engines,
            @Field("language") String language,
            @Field("pageno") Integer pageno,
            @Field("time_range") String timeRange,
            @Field("format") String format,
            @Field("safesearch") Integer safeSearch);
}
