package org.appxi.util;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Predicate;

public interface FileHelper {
    static String getIdentificationInfo(Path path, Object... additions) {
        final StringBuilder result = new StringBuilder(path.getFileName().toString());
        if (exists(path)) {
            try {
                final BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                result.append('|').append(attrs.lastModifiedTime().toMillis());
                if (attrs.isRegularFile())
                    result.append('|').append(attrs.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Object obj : additions)
            result.append('|').append(obj);
        return result.toString();
    }

    static boolean exists(Path path) {
        try {
            return Files.exists(path);
        } catch (Exception e) {
            return false;
        }
    }

    static boolean notExists(Path path) {
        return !exists(path);
    }

    static void makeParents(Path file) {
        makeDirs(file.getParent());
    }

    static void makeDirs(Path path) {
        if (Files.notExists(path))
            try {
                Files.createDirectories(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    static String makeEncodedPath(String input, String dotExt) {
        final StringBuilder buf = new StringBuilder();
        buf.append(DigestHelper.md5(input));
        buf.insert(4, '/');
        buf.insert(2, '/');
        if (null != dotExt)
            buf.append(dotExt);
        return buf.toString();
    }

    static String readString(Path file) {
        try {
            return Files.readString(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static boolean writeString(Path file, CharSequence string) {
        makeDirs(file.getParent());
        try {
            Files.writeString(file, string);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    static <T> T readObject(Path file) {
        if (Files.notExists(file))
            return null;
        try (ObjectInputStream objStream = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(file)))) {
            return (T) objStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static boolean writeObject(Path file, Object object) {
        try {
            Files.createDirectories(file.getParent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (ObjectOutputStream objStream = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(file)))) {
            objStream.writeObject(object);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    static void lines(Path file, Predicate<String> predicate) {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null)
                if (predicate.test(line))
                    return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void extract(URL resource, Path targetFile) {
        makeParents(targetFile);
        try (InputStream inStream = resource.openStream()) {
            Files.copy(inStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
