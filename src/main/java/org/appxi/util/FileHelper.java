package org.appxi.util;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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

    static void deleteDirectory(Path directory) {
        if (notExists(directory))
            return;
        try {
            Files.walk(directory)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (Exception e) {
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
        try (InputStream stream = Files.newInputStream(file)) {
            lines(stream, predicate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void lines(InputStream stream, Predicate<String> predicate) {
        lines(stream, StandardCharsets.UTF_8, predicate);
    }

    static void lines(InputStream stream, Charset charset, Predicate<String> predicate) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(stream), charset))) {
            String line;
            while ((line = reader.readLine()) != null)
                if (predicate.test(line))
                    return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void lines(Path file, Consumer<String> consumer) {
        try (InputStream stream = Files.newInputStream(file)) {
            lines(stream, consumer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void lines(InputStream stream, Consumer<String> consumer) {
        lines(stream, StandardCharsets.UTF_8, consumer);
    }

    static void lines(InputStream stream, Charset charset, Consumer<String> consumer) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(stream), charset))) {
            String line;
            while ((line = reader.readLine()) != null) consumer.accept(line);
        } catch (Exception e) {
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

    static void extract(URL resource, Path targetFile) {
        makeParents(targetFile);
        try (InputStream inStream = resource.openStream()) {
            Files.copy(inStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static List<Path> extractFiles(Function<String, InputStream> sourceSupplier,
                                   Function<String, Path> targetSupplier,
                                   Collection<String> files) {
        return extractFiles(sourceSupplier, targetSupplier, files.toArray(new String[0]));
    }

    static List<Path> extractFiles(Function<String, InputStream> sourceSupplier,
                                   Function<String, Path> targetSupplier,
                                   String... files) {
        final List<Path> result = new ArrayList<>(files.length);
        Path extracted;
        for (String file : files) {
            extracted = extractFile(sourceSupplier, targetSupplier, file);
            if (null != extracted)
                result.add(extracted);
        }
        return result;
    }

    static Path extractFile(Function<String, InputStream> sourceSupplier,
                            Function<String, Path> targetSupplier,
                            String file) {
        final Path target = targetSupplier.apply(file);
        if (exists(target))
            return target;
        makeParents(target);
        //
        final Path localFile = Paths.get(file);
        if (exists(localFile)) {
            try {
                Files.copy(localFile, target);
                return target;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //
        try (InputStream stream = sourceSupplier.apply(file)) {
            if (null != stream) {
                Files.copy(stream, target);
                return target;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static String subPath(Path path, int beginIndex) {
        return path.subpath(beginIndex, path.getNameCount()).toString().replace('\\', '/');
    }

    static boolean isNameValid(String name) {
        try {
            return Objects.equals(Path.of(name).getFileName().toString(), name);
        } catch (Throwable ignored) {
            return false;
        }
    }

    static String toValidName(String name) {
//        char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };
        return name.replaceAll("[/\n\r\t\0\f`?*\\\\<>|'\":;]", "");
    }

    static void walkZipFile(String file, BiConsumer<ZipFile, ZipEntry> consumer) {
        try (ZipFile zipFile = new ZipFile(file)) {
            zipFile.stream().forEach(zipEntry -> consumer.accept(zipFile, zipEntry));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static byte[] readFully(InputStream stream) {
        try {
            byte[] result = new byte[stream.available()];
            stream.read(result);
            return result;
        } catch (Throwable ignore) {
        }
        return new byte[0];
    }
}
