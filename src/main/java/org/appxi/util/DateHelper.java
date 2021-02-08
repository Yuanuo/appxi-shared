package org.appxi.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class DateHelper {
    static final DateFormat DEF_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static String format(Date date) {
        return DEF_FMT.format(date);
    }

    private DateHelper() {
    }
}
