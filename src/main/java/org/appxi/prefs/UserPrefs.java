package org.appxi.prefs;

import java.nio.file.Files;
import java.nio.file.Path;

public abstract class UserPrefs {
    public static Preferences prefs = new PreferencesInMemory();

    public static Preferences recents = new PreferencesInMemory();

    public static Preferences favorites = new PreferencesInMemory();

    private static final Path _workDir = Path.of("").toAbsolutePath();
    private static Path _dataDir = Path.of(System.getProperty("user.home"));
    private static Path _confDir = _workDir.resolve(".config");

    public static void setupDataDirectory(Path dataDir, String confDirName) {
        _dataDir = null != dataDir ? dataDir : _dataDir;
        _confDir = _dataDir.resolve(null == confDirName || confDirName.isBlank() ? ".config" : confDirName);
    }

    public static void setupPortable(String dataDirName, String confDirName) {
        final Path dataDir = _workDir.resolve(dataDirName);
        setupDataDirectory(Files.isWritable(_workDir) && Files.exists(dataDir)
                ? dataDir : _dataDir.resolve(dataDirName), confDirName);
    }

    public static Path workDir() {
        return _workDir;
    }

    public static Path dataDir() {
        return _dataDir;
    }

    public static Path confDir() {
        return _confDir;
    }

    public static Path cacheDir() {
        return _dataDir.resolve(".cached");
    }
}
