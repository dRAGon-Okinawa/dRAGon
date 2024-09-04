package ai.dragon.util.db.filters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.NitriteId;
import org.dizitart.no2.common.tuples.Pair;
import org.dizitart.no2.exceptions.FilterException;
import org.dizitart.no2.filters.FieldBasedFilter;

class ExtendedRegexFilter extends FieldBasedFilter {
    private final Pattern pattern;

    ExtendedRegexFilter(String field, String value) {
        super(field, value);
        pattern = Pattern.compile(value);
    }

    ExtendedRegexFilter(String field, String value, int tags) {
        super(field, value);
        pattern = Pattern.compile(value, tags);
    }

    @Override
    public boolean apply(Pair<NitriteId, Document> element) {
        Document document = element.getSecond();
        Object fieldValue = document.get(getField());
        if (fieldValue != null) {
            if (fieldValue instanceof String) {
                Matcher matcher = pattern.matcher((String) fieldValue);
                if (matcher.find()) {
                    return true;
                }
                matcher.reset();
            } else {
                throw new FilterException(getField() + " does not contain string value");
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + getField() + " regex " + getValue() + ")";
    }
}
