package ai.dragon.util.db.filters;

import java.util.regex.Pattern;

import org.dizitart.no2.filters.NitriteFilter;

public class ExtendedFluentFilter {
    public static ExtendedFluentFilter $ = where("$");

    private String field;

    private ExtendedFluentFilter() {
    }

    public static ExtendedFluentFilter where(String field) {
        ExtendedFluentFilter filter = new ExtendedFluentFilter();
        filter.field = field;
        return filter;
    }

    public NitriteFilter regex(String value) {
        return new ExtendedRegexFilter(field, value);
    }

    public NitriteFilter regex(String value, int flags) {
        return new ExtendedRegexFilter(field, value, flags);
    }

    public NitriteFilter nonsensitiveRegex(String value) {
        return new ExtendedRegexFilter(field, value, Pattern.CASE_INSENSITIVE);
    }
}
