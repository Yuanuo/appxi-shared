package org.appxi.prefs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PreferencesInMemory implements Preferences {
    private final Map<String, Object> prefs = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getPrefs() {
        return prefs;
    }

    @Override
    public Preferences setProperty(String key, Object val) {
        prefs.put(key, val);
        return this;
    }

    @Override
    public Object getProperty(String key) {
        return prefs.get(key);
    }

    @Override
    public Object removeProperty(String key) {
        return prefs.remove(key);
    }

    @Override
    public Set<String> getPropertyKeys() {
        return prefs.keySet();
    }

    @Override
    public boolean containsProperty(String key) {
        return prefs.containsKey(key);
    }

    @Override
    public void save() {
        // do nothing
    }

    @Override
    public void clear() {
        prefs.clear();
    }
}
