package org.appxi.util;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    public static String md5(String input) {
        try {
            final byte[] md5sum = MessageDigest.getInstance("MD5").digest(input.getBytes());
            return String.format("%032X", new BigInteger(1, md5sum));
        } catch (NoSuchAlgorithmException e) {
            return crc32c(input);
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
        protected static long COUNTER = 0;
        private static final Object lock = new Object();
        protected final long time;
        protected final long id;

        public UID() {
            time = System.currentTimeMillis();
            synchronized (UID.lock) {
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
