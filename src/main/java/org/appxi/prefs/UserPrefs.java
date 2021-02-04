package org.appxi.prefs;

import java.nio.file.Path;

public abstract class UserPrefs {
    public static Preferences prefs = new PreferencesInMemory();

    public static Preferences recents = new PreferencesInMemory();

    public static Preferences favorites = new PreferencesInMemory();

    private static final Path _workDir = Path.of("").toAbsolutePath();
    private static Path _dataDir = Path.of(System.getProperty("user.home"));
    private static Path _confDir = _workDir.resolve(".config");
    private static Path _appDir = null;

    public static void setupDataDirectory(Path dataDir, String confDirName) {
        _dataDir = null != dataDir ? dataDir : _dataDir;
        _confDir = _dataDir.resolve(null == confDirName || confDirName.isBlank() ? ".config" : confDirName);
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

    public static Path appDir() {
        if (null != _appDir)
            return _appDir;

        synchronized (_workDir) {
            if (null != _appDir)
                return _appDir;

            String appDir = System.getenv("app-dir");
            if (null == appDir)
                appDir = System.getProperty("jpackage.app-dir");

            if (null == appDir) {
                appDir = System.getProperty("jpackage.app-path");
                if (null != appDir) {
                    String osName = System.getProperty("os.name").toLowerCase();
                    Path appPath = Path.of(appDir).getParent();
                    if (osName.contains("win")) {
                        appDir = appPath.resolve("app").toString();
                    } else if (osName.contains("mac")) {

                    } else {
                        appDir = appPath.resolve("lib").toString();
                    }
                }
            }
            if (null == appDir)
                appDir = "";
            _appDir = Path.of(appDir).toAbsolutePath();
        }
        return _appDir;
    }
}
