module appxi.shared {
    requires transitive java.xml;
    requires transitive org.slf4j;

    exports org.appxi.book;
    exports org.appxi.event;
    exports org.appxi.file;
    exports org.appxi.holder;
    exports org.appxi.prefs;
    exports org.appxi.property;
    exports org.appxi.util;
    exports org.appxi.util.ext;
}