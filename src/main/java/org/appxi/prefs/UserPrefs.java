package org.appxi.prefs;

import java.nio.file.Path;

public abstract class UserPrefs {
    public static Preferences prefs = new PreferencesInMemory();

    public static Preferences recents = new PreferencesInMemory();

    public static Preferences favorites = new PreferencesInMemory();

    private static Path _workDir = Path.of("").toAbsolutePath();
    private static Path _confDir = _workDir.resolve(".config");

    public static void setupWorkDirectory(Path workDir, String confDirName) {
        _workDir = null != workDir ? workDir : _workDir;
        _confDir = _workDir.resolve(null == confDirName || confDirName.isBlank() ? ".config" : confDirName);
    }

    public static Path workDir() {
        return _workDir;
    }

    public static Path confDir() {
        return _confDir;
    }

    public static Path cacheDir() {
        return _workDir.resolve(".cached");
    }
}
