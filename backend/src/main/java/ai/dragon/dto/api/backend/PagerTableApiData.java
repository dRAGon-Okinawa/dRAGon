package ai.dragon.dto.api.backend;

import java.util.List;

import ai.dragon.dto.api.TableApiData;
import ai.dragon.repository.util.Pager;

public class PagerTableApiData implements TableApiData {
    private final Pager<?> pager;

    public PagerTableApiData(Pager<?> pager) {
        this.pager = pager;
    }

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
}
