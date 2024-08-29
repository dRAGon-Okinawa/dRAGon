package ai.dragon.dto.api;

import java.util.List;

public interface TableApiData {
    public List<?> getRecords();
    public long getCurrent();
    public long getSize();
    public long getTotal();
}
