package org.appxi.util;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
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

    static void delete(Path file) {
        try {
            Files.delete(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static long fileSize(Path file) {
        try {
            return Files.size(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    static long fileTime(Path file) {
        if (exists(file)) {
            try {
                return Files.getLastModifiedTime(file).toMillis();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    static long fileTimeNewest(Path... files) {
        if (null == files || files.length == 0)
            return -1;
        final Long[] times = new Long[files.length];
        for (int i = 0; i < files.length; i++) {
            times[i] = fileTime(files[i]);
        }
        Arrays.sort(times);
        return times[times.length - 1];
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

    static boolean writeString(CharSequence string, Path file) {
        makeParents(file);
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

    static boolean writeObject(Object object, Path file) {
        makeParents(file);
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

    static boolean isTargetFileUpdatable(Path target, Path... sources) {
        if (notExists(target))
            return true;
        if (fileTime(target) < fileTimeNewest(sources)) {
            delete(target);
            return true;
        }
        return false;
    }
}
