package org.appxi.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class DateHelper {
    static final DateFormat DEF_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    static final DateFormat DEF_FMT2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static final DateFormat DEF_FMT3 = new SimpleDateFormat("yyyyMMdd_HHmmss");

    public static String format(Date date) {
        return DEF_FMT.format(date);
    }

    public static String format2(Date date) {
        return DEF_FMT2.format(date);
    }

    public static String format3(Date date) {
        return DEF_FMT3.format(date);
    }

    private DateHelper() {
    }
}
