module appxi.shared {
    requires java.logging;
    requires transitive java.xml;

    exports org.appxi.file;
    exports org.appxi.holder;
    exports org.appxi.prefs;
    exports org.appxi.util;
    exports org.appxi.util.ext;
}