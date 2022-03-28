package org.appxi.util;

public abstract class OSVersions {
    public static final String osName = System.getProperty("os.name");
    public static final String osVersion = System.getProperty("os.version");

    public static final boolean isAndroid;
    public static final boolean isWindows;
    public static final boolean isWindows_vista_or_later;
    public static final boolean isWindows_7_or_later;
    public static final boolean isMac;
    public static final boolean isLinux;
    public static final boolean isSolaris;
    public static final boolean isIOS;

    private static boolean osVersionNumberGreaterThanOrEqualTo(float number) {
        try {
            return Float.parseFloat(osVersion) >= number;
        } catch (Exception e) {
            return false;
        }
    }

    static {
        isAndroid = "android".equals(System.getProperty("javafx.platform")) || "Dalvik".equals(System.getProperty("java.vm.name"));
        isWindows = osName.startsWith("Windows");
        isWindows_vista_or_later = isWindows && osVersionNumberGreaterThanOrEqualTo(6.0F);
        isWindows_7_or_later = isWindows && osVersionNumberGreaterThanOrEqualTo(6.1F);
        isMac = osName.startsWith("Mac");
        isLinux = osName.startsWith("Linux") && !isAndroid;
        isSolaris = osName.startsWith("SunOS");
        isIOS = osName.startsWith("iOS");
    }

    private OSVersions() {
    }
}
