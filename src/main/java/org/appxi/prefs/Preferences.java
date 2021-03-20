package org.appxi.prefs;

import java.util.Set;
import java.util.function.Predicate;

public interface Preferences {

    <T> T getPrefs();

    Preferences setProperty(String key, Object val);

    Object getProperty(String key);

    Object removeProperty(String key);

    Set<String> getPropertyKeys();

    boolean containsProperty(String key);

    default String getString(String key, String defaultValue) {
        final Object val = getProperty(key);
        return null == val ? defaultValue : val.toString();
    }

    default int getInt(String key, int defaultValue) {
        final Object val = getProperty(key);
        return null == val ? defaultValue : Integer.parseInt(val.toString());
    }

    default int getInt(String key, int defaultValue, Predicate<Integer> validation) {
        final Object val = getProperty(key);
        if (null == val)
            return defaultValue;
        final int realVal = Integer.parseInt(val.toString());
        // validation not passed, also use default value
        if (null != validation && !validation.test(realVal))
            return defaultValue;
        return realVal;
    }

    default long getLong(String key, long defaultValue) {
        final Object val = getProperty(key);
        return null == val ? defaultValue : Long.parseLong(val.toString());
    }

    default double getDouble(String key, double defaultValue) {
        final Object val = getProperty(key);
        return null == val ? defaultValue : Double.parseDouble(val.toString());
    }

    default boolean getBoolean(String key, boolean defaultValue) {
        final Object val = getProperty(key);
        return null == val ? defaultValue : Boolean.parseBoolean(val.toString());
    }

    void save();

    void clear();
}
