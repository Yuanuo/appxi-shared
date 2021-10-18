package org.appxi.util;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.zip.CRC32C;

public abstract class DigestHelper {
    private DigestHelper() {
    }

    public static String crc32c(String str, String salt) {
        return crc32c(str, salt, StandardCharsets.UTF_8);
    }

    public static String crc32c(String str, String salt, Charset charset) {
        if (null == salt)
            return crc32c(str, charset);
        return crc32c(StringHelper.concat(str, '@', salt), charset);
    }

    public static String crc32c(String input) {
        return crc32c(input, StandardCharsets.UTF_8);
    }

    public static String crc32c(String input, Charset charset) {
        final CRC32C checksum = new CRC32C();
        checksum.update(input.getBytes(charset));
        return Long.toHexString(checksum.getValue());
    }

    /**
     * @param input
     * @return
     * @apiNote 注意大批量使用时性能不及CRC32
     */
    public static String md5(String input) {
        try {
            final byte[] md5sum = MessageDigest.getInstance("MD5").digest(input.getBytes(StandardCharsets.UTF_8));
            return String.format("%032X", new BigInteger(1, md5sum));
        } catch (Throwable e) {
            return crc32c(input);
        }
    }

    public static String md5(Path file) {
        try {
            final byte[] md5sum = MessageDigest.getInstance("MD5").digest(Files.readAllBytes(file));
            return String.format("%032X", new BigInteger(1, md5sum));
        } catch (Throwable e) {
            return null;
        }
    }

    public static String uid() {
        return new UID().toString();
    }

    public static String uid(String sep) {
        return new UID().toString(sep);
    }

    /**
     * 产生一个ID,在同一个虚拟机下产生一个唯一的ID，其格式为[time] - [counter]
     */
    static class UID {
        private static final Object LOCK = new Object();
        private static long COUNTER = 0;
        private final long time;
        private final long id;

        public UID() {
            time = System.currentTimeMillis();
            synchronized (UID.LOCK) {
                id = UID.COUNTER++;
            }
        }

        @Override
        public String toString() {
            return Long.toString(time, Character.MAX_RADIX) + Long.toString(id, Character.MAX_RADIX);
        }

        public String toString(final String dim) {
            return Long.toString(time, Character.MAX_RADIX) + dim + Long.toString(id, Character.MAX_RADIX);
        }

        @Override
        public int hashCode() {
            return (int) ((time ^ id) >> 32);
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj != null && obj.getClass() == getClass()) {
                final UID uid = (UID) obj;
                return uid.time == time && uid.id == id;
            }
            return false;
        }
    }
}
