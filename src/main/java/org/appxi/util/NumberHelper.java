package org.appxi.util;

import java.util.Arrays;

public abstract class NumberHelper {
    public static int toInt(String str, int defaultValue) {
        try {
            return null == str || str.isBlank() ? defaultValue : Integer.parseInt(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static long toLong(String str, long defaultValue) {
        try {
            return null == str || str.isBlank() ? defaultValue : Long.parseLong(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static double toDouble(String str, double defaultValue) {
        try {
            return null == str || str.isBlank() ? defaultValue : Double.parseDouble(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static final String[] OLD_NUMBERS = {"〇", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};

    public static String toChineseNumberOld(int num) {
        if (num <= 0)
            return OLD_NUMBERS[0];
        if (num <= 10)
            return OLD_NUMBERS[num];
        if (num < 20)
            return OLD_NUMBERS[10] + OLD_NUMBERS[num - 10];
        if (num < 100) {
            int tmp = num % 10;
            return OLD_NUMBERS[num / 10] + (tmp == 0 ? OLD_NUMBERS[10] : OLD_NUMBERS[tmp]);
        }
        StringBuilder buff = new StringBuilder();
        for (char c : String.valueOf(num).toCharArray())
            buff.append(OLD_NUMBERS[Character.getNumericValue(c)]);
        return buff.toString();
    }

    private static char[] radix62Chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    public static String toRadix62(long value) {
        StringBuilder result = new StringBuilder();
        while (value > 61) {
            result.append(radix62Chars[(int) (value % 62)]);
            value = value / 62;
        }
        result.append(radix62Chars[(int) value]);
        return result.reverse().toString();
    }

    public static long fromRadix62(String str) {
        long result = 0;
        for (int i = 0; i < str.length(); i++) {
            result += (long) (Arrays.binarySearch(radix62Chars, str.charAt(i)) * Math.pow(62, str.length() - i - 1));
        }
        return result;
    }
}
