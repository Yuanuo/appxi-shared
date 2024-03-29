package org.appxi.prefs;

import org.appxi.util.FileHelper;
import org.appxi.util.ext.OrderedProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

public class PreferencesInProperties implements Preferences {
    public static final String COMMENT = "PLEASE DO NOT EDIT THIS FILE";

    private static final Logger logger = LoggerFactory.getLogger(PreferencesInProperties.class);

    private final OrderedProperties prefs;
    private final Path file;
    private final Charset charset;

    public PreferencesInProperties(Path file) {
        this(file, true, null);
    }

    public PreferencesInProperties(Path file, boolean load) {
        this(file, load, null);
    }

    public PreferencesInProperties(Path file, Charset charset) {
        this(file, true, charset);
    }

    public PreferencesInProperties(Path file, boolean load, Charset charset) {
        this.file = Objects.requireNonNull(file);
        this.prefs = new OrderedProperties();
        this.charset = charset;
        //
        if (load && Files.exists(file)) {
            try (InputStream inStream = new BufferedInputStream(Files.newInputStream(file))) {
                if (file.getFileName().toString().toLowerCase().endsWith(".xml")) {
                    prefs.loadFromXML(inStream);
                } else if (null != charset) {
                    prefs.load(new InputStreamReader(inStream, charset));
                } else {
                    prefs.load(inStream);
                }
            } catch (IOException e) {
                logger.warn("load PreferencesInProperties failed", e);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public OrderedProperties getPrefs() {
        return prefs;
    }

    @Override
    public Preferences setProperty(String key, Object val) {
        prefs.setProperty(key, String.valueOf(val));
        return this;
    }

    @Override
    public Object getProperty(String key) {
        return prefs.getProperty(key);
    }

    @Override
    public Object removeProperty(String key) {
        return prefs.removeProperty(key);
    }

    @Override
    public Set<String> getPropertyKeys() {
        return prefs.stringPropertyNames();
    }

    @Override
    public boolean containsProperty(String key) {
        return prefs.containsProperty(key);
    }

    @Override
    public void save() {
        FileHelper.makeParents(file);
        try (OutputStream outStream = Files.newOutputStream(file)) {
            if (file.getFileName().toString().toLowerCase().endsWith(".xml")) {
                this.prefs.storeToXML(outStream, COMMENT);
            } else if (null != this.charset) {
                this.prefs.store(new OutputStreamWriter(outStream, this.charset), COMMENT);
            } else {
                this.prefs.store(outStream, COMMENT);
            }
        } catch (IOException e) {
            logger.warn("save PreferencesInProperties failed", e);
        }
    }

    @Override
    public void clear() {
        prefs.clear();
    }
}
