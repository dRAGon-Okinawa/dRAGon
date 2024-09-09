package ai.dragon.dto.api;

import ai.dragon.dto.api.backend.PagerTableApiData;
import ai.dragon.enumeration.ApiResponseCode;
import ai.dragon.repository.util.Pager;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DataTableApiResponse implements GenericApiResponse {
    @Builder.Default
    private TableApiData data = null;

    @Builder.Default
    private String code = ApiResponseCode.SUCCESS.toString();

    @Builder.Default
    private String msg = "OK";

    public static DataTableApiResponse fromPager(Pager<?> pager) {
        TableApiData data = new PagerTableApiData(pager);
        return DataTableApiResponse.builder().data(data).build();
    }
}
