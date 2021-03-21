package org.appxi.prefs;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 用于记录用户数据（通常称为偏好设置）等的通用工具类
 */
public abstract class UserPrefs {
    /**
     * 用于记录用户数据的通用实现，默认使用内存存储，实际环境使用时建议初始化
     * <code>UserPrefs.prefs = new PreferencesInProperties(UserPrefs.confDir().resolve(".prefs"));</code>
     */
    public static Preferences prefs = new PreferencesInMemory();

    public static Preferences recents = new PreferencesInMemory();

    public static Preferences favorites = new PreferencesInMemory();

    private static final Path _workDir = Path.of("").toAbsolutePath();
    private static Path _dataDir = Path.of(System.getProperty("user.home"));
    private static Path _confDir = _workDir.resolve(".config");

    /**
     * 设置用户数据存储目录，需要在程序启动时调用
     *
     * @param dataDir     指定的主目录位置，默认为<code>${user.home}</code>
     * @param confDirName 指定的存储配置数据的目录名称，默认为<code>.config</code>
     */
    public static void setupDataDirectory(Path dataDir, String confDirName) {
        _dataDir = null != dataDir ? dataDir : _dataDir;
        _confDir = _dataDir.resolve(null == confDirName || confDirName.isBlank() ? ".config" : confDirName);
    }

    /**
     * 设置用户数据存储目录，如果当前程序主目录可写入时优先使用主程序启动目录，否则使用<code>${user.home}/dataDirName</code>
     *
     * @param dataDirName 指定的存储主目录名称
     * @param confDirName 指定的配置目录名称，默认为<code>.config</code>
     */
    public static void localDataDirectory(String dataDirName, String confDirName) {
        final Path dataDir = _workDir.resolve(dataDirName);
        setupDataDirectory(Files.isWritable(_workDir) && Files.exists(dataDir)
                ? dataDir : _dataDir.resolve(dataDirName), confDirName);
    }

    /**
     * 获得当前程序主目录
     *
     * @return
     */
    public static Path workDir() {
        return _workDir;
    }

    /**
     * 获得默认或已设置的数据存储主目录
     *
     * @return
     */
    public static Path dataDir() {
        return _dataDir;
    }

    /**
     * 获得默认或已设置的配置数据主目录，默认为 ${dataDir}/.config
     *
     * @return
     */
    public static Path confDir() {
        return _confDir;
    }

    /**
     * 获得默认或已设置的缓存数据主目录，默认为 ${dataDir}/.cached
     *
     * @return
     */
    public static Path cacheDir() {
        return _dataDir.resolve(".cached");
    }
}
