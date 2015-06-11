package ru.lightsoft.bugster;

import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.Map;

public class LsDateFormatter {
    private static Map<String, LsDateFormatter> cache = Maps.newConcurrentMap();
    private DateTimeFormatter fmt;

    private LsDateFormatter() {
    }

    private static LsDateFormatter create(String pattern) {
        LsDateFormatter lsDateFormatter = new LsDateFormatter();
        lsDateFormatter.fmt = DateTimeFormat.forPattern(pattern);
        return lsDateFormatter;
    }

    public static LsDateFormatter getInstance(String pattern) {
        if (cache.containsKey(pattern)) {
            return cache.get(pattern);
        } else {
            LsDateFormatter lsDateFormatter = create(pattern);
            cache.put(pattern, lsDateFormatter);
            return lsDateFormatter;
        }
    }

    public String format(Date date) {
        return new DateTime(date).toString(fmt);
    }
}
