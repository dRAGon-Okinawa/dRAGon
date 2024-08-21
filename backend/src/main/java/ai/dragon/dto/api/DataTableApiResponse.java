package ai.dragon.dto.api;

import java.util.List;

import ai.dragon.repository.util.Pager;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DataTableApiResponse implements GenericApiResponse {
    @Builder.Default
    private TableApiData data = null;

    @Builder.Default
    private String code = "0000";

    @Builder.Default
    private String msg = "OK";

    public static DataTableApiResponse fromPager(Pager<?> pager) {
        TableApiData data = new TableApiData() {
            @Override
            public List<?> getRecords() {
                return pager.getData();
            }

            @Override
            public long getCurrent() {
                return pager.getPage();
            }

            @Override
            public long getSize() {
                return pager.getSize();
            }

            @Override
            public long getTotal() {
                return pager.getTotal();
            }
        };
        return DataTableApiResponse.builder().data(data).build();
    }
}
