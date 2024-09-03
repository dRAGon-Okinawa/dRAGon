package ai.dragon.repository.util;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Pager<T> {
    private int page;
    private int size;
    private long total;
    private List<T> data;
}
