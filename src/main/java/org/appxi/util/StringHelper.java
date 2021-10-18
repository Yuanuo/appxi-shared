package org.appxi.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface StringHelper {
    static String concat(final Object... items) {
        if (items.length == 0)
            return "";

        if (items.length == 1)
            return String.valueOf(items[0]);

        final StringBuilder buf = new StringBuilder();
        for (final Object obj : items) {
            if (null == obj)
                continue;
            if (obj instanceof Throwable t)
                buf.append('\n').append(getThrowableAsString(t)).append('\n');
            else buf.append(obj);
        }
        return buf.toString();
    }

    static Supplier<String> concat2(Object... items) {
        return () -> StringHelper.concat(items);
    }

    static <T> String join(String separator, T... items) {
        if (items.length == 0)
            return "";

        if (items.length == 1)
            return String.valueOf(items[0]);

        final StringBuilder buf = new StringBuilder();
        for (Object itm : items) {
            if (null == itm)
                continue;
            if (null != separator && buf.length() > 0)
                buf.append(separator);
            buf.append(itm);
        }
        return buf.toString();
    }

    static String join(String separator, Collection<?> items) {
        return join(separator, items.toArray(new Object[0]));
    }

    static String joinLines(Collection<?> items) {
        return join(System.lineSeparator(), items.toArray(new Object[0]));
    }

    /**
     * @deprecated use {@link #join(String, Object[])}
     */
    static String joinArray(String separator, Object... items) {
        return join(separator, items);
    }

    static boolean indexOf(String value, String... values) {
        for (String val : values) {
            if (Objects.equals(value, val))
                return true;
        }
        return false;
    }

    static boolean containsAny(String string, String... items) {
        for (String item : items)
            if (string.contains(item))
                return true;
        return false;
    }

    /**
     * Improved version of java.lang.String.split() that supports escape.
     * Example: if you want to split a string with comma "," as separator and
     * with double quotes as escape characters, use
     * <code>split("one, two, \"a,b,c\"", ",", "\"");</code>. Result is a list
     * of 3 strings "one", "two", "a,b,c".
     * <p>
     * <code>split("a (b c   d) ef", " ", "[\\(\\)]");</code>. Result is a list
     * of 3 strings "a", "b c   d", "ef".
     * <p>
     * <b>Note:</b> keep in mind to escape the chars [b]\()[]{^$|?*+.[/b] that
     * are special regular expression operators!
     *
     * @param string    String to split up by the given <i>separator</i>.
     * @param separator Split separator.
     * @param escape    Optional escape character to enclose substrings that can
     *                  contain separators.
     * @return Separated substrings of <i>string</i>.
     * @since 2.6.0
     */
    static String[] split(final String string, final String separator, final String escape) {
        if (null == string)
            return new String[0];

        if (null == separator || separator.isEmpty())
            return new String[]{string};

        if (null == escape || escape.isEmpty())
            return string.split(separator);

        final StringBuilder regexBuilder = new StringBuilder();
        regexBuilder.append("\\s*"); // all matches with optional
        // leading white
        // spaces
        regexBuilder.append(escape); // enclosed in escape
        // character
        regexBuilder.append("(.*?)"); // with any character
        regexBuilder.append(escape);
        regexBuilder.append("\\s*"); // and optional trailing
        // white spaces
        regexBuilder.append("|"); // or
        regexBuilder.append("(?<=^|"); // beginning of line (via
        // zero-width
        // positive lookbehind) or
        regexBuilder.append(separator); // separator
        regexBuilder.append(")");
        regexBuilder.append("[^"); // any character except
        regexBuilder.append(separator); // separator
        regexBuilder.append("]*"); // zero or more times

        final List<String> result = new ArrayList<>();

        final Pattern p = Pattern.compile(regexBuilder.toString());
        final Matcher m = p.matcher(string);
        while (m.find()) {
            // strip off quotes:
            result.add(m.group(1) != null ? m.group(1) : m.group());
        } // next sequence
        return result.toArray(new String[0]);
    }

    /**
     * This function splits the String s into multiple Strings using the
     * splitChar.  However, it provides a quoting facility: it is possible to
     * quote strings with the quoteChar.
     * If the quoteChar occurs within the quotedExpression, it must be prefaced
     * by the escapeChar.
     * This routine can be useful for processing a line of a CSV file.
     *
     * @param str         The String to split into fields. Cannot be null.
     * @param splitChar The character to split on
     * @param quoteChar The character to quote items with
     * @param escapeChar The character to escape the quoteChar with
     * @return An array of Strings that s is split into
     */
    static String[] splitOnCharWithQuoting(String str, char splitChar, char quoteChar, char escapeChar) {
        List<String> result = new ArrayList<>();
        int i = 0;
        int length = str.length();
        StringBuilder b = new StringBuilder();
        while (i < length) {
            char curr = str.charAt(i);
            if (curr == splitChar) {
                // add last buffer
                // cdm 2014: Do this even if the field is empty!
                // if (b.length() > 0) {
                result.add(b.toString());
                b = new StringBuilder();
                // }
                i++;
            } else if (curr == quoteChar) {
                // find next instance of quoteChar
                i++;
                while (i < length) {
                    curr = str.charAt(i);
                    // mrsmith: changed this condition from
                    // if (curr == escapeChar) {
                    if ((curr == escapeChar) && (i+1 < length) && (str.charAt(i+1) == quoteChar)) {
                        b.append(str.charAt(i + 1));
                        i += 2;
                    } else if (curr == quoteChar) {
                        i++;
                        break; // break this loop
                    } else {
                        b.append(str.charAt(i));
                        i++;
                    }
                }
            } else {
                b.append(curr);
                i++;
            }
        }
        // RFC 4180 disallows final comma. At any rate, don't produce a field after it unless non-empty
        if (b.length() > 0) {
            result.add(b.toString());
        }
        return result.toArray(new String[0]);
    }

    static String[] stripBlanks(String... args) {
        final List<String> result = new ArrayList<>(args.length);
        for (String arg : args) {
            if (null != arg && !arg.isBlank())
                result.add(arg.strip());
        }
        return result.toArray(new String[0]);
    }

    static boolean isBlank(String string) {
        return !isNotBlank(string);
    }

    static boolean isNotBlank(String string) {
        return null != string && !string.isBlank();
    }

    static int length(String string) {
        if (null == string)
            return -1;
        int result = 0;
        for (char c : string.toCharArray()) {
            result += c <= 0xFF ? 1 : 2;
        }
        return result;
    }

    static String trimChars(String string, int maxChars) {
        return trimChars(string, maxChars, "...");
    }

    static String trimChars(String string, int maxChars, String ellipses) {
        if (string.length() <= maxChars)
            return string;
        maxChars = Character.isLowSurrogate(string.charAt(maxChars)) && maxChars > 0 ? maxChars - 1 : maxChars;
        return concat(string.substring(0, maxChars), ellipses);
    }

    static String trimBytes(String string, int maxBytes) {
        return trimBytes(string, maxBytes, "...");
    }

    static String trimBytes(String string, int maxBytes, String ellipses) {
        final StringBuilder buf = new StringBuilder(maxBytes + (null == ellipses ? 0 : ellipses.length()));
        int numBytes = 0;
        for (char charItm : string.toCharArray()) {
            numBytes += (charItm > 0xFF ? 2 : 1);
            if (numBytes > maxBytes) {
                if (null != ellipses)
                    buf.append(ellipses);
                break;
            }
            buf.append(charItm);
        }
        return buf.toString();
    }

    static String padLeft(Object object, int size, char pad) {
        final String string = String.valueOf(object);
        final int len = string.length();
        if (len >= size)
            return string;
        return concat(String.valueOf(pad).repeat(size - len), string);
    }

    static String getThrowableAsString(Throwable throwable) {
        final StringWriter writer = new StringWriter();
        try (PrintWriter print = new PrintWriter(writer)) {
            throwable.printStackTrace(print);
        }
        return writer.toString();
    }

    static List<String> getThrowableAsLines(Throwable throwable) {
        final String string = getThrowableAsString(throwable);
        return new ArrayList<>(Arrays.asList(string.split(System.lineSeparator())));
    }

    static String unicode(int codePoint) {
        return unicode(codePoint, true);
    }

    static String unicode(int codePoint, boolean unicodeFlag) {
        char[] arr = Character.toChars(codePoint);
        StringBuilder buf = new StringBuilder(arr.length * 4);
        for (char c : arr) {
            if (unicodeFlag && buf.length() > 0)
                buf.append("\\u");
            String tmp = Integer.toHexString(c);
            if (c <= 0xF)
                buf.append("000");
            else if (c <= 0xFF)
                buf.append("00");
            else if (c <= 0xFFF)
                buf.append("0");
//            if (tmp.length() < 4)
//                buf.append(padLeft(tmp, 4, '0'));
            buf.append(tmp);
        }
        return buf.toString();
    }

    static List<String> unicodeList(String str, boolean unicodeFlag) {
        final List<String> result = new ArrayList<>();
        if (null != str && !str.isEmpty())
            str.codePoints().forEach(v -> result.add(unicode(v, unicodeFlag)));
        return result;
    }

    static String unicodeJoined(String str, String joinSep, boolean unicodeFlag) {
        if (null == str || str.isEmpty())
            return "";
        if (str.length() == 1)
            return unicode(str.codePointAt(0));
        return join(joinSep, unicodeList(str, unicodeFlag));
    }

    static List<String> getFlatPaths(String path) {
        final List<String> result = new ArrayList<>();
        path = path.replace("\\", "/");
        for (int idx = 0; idx < path.length(); ) {
            idx = path.indexOf('/', idx);
            if (idx == -1) {
                result.add(path);
                break;
            }
            result.add(path.substring(0, idx));
            idx++; // to skip '/'
        }
        return result;
    }

    static List<String> getFlatParents(String path) {
        final List<String> result = getFlatPaths(path);
        if (!result.isEmpty())
            result.remove(result.size() - 1);
        return result;
    }


    static boolean split2PartsAndEqualsPart1(String str1, String str2, String splitter) {
        return str1.contains(splitter) && str2.contains(splitter)
                && str1.split(splitter, 2)[0].equals(str2.split(splitter, 2)[0]);
    }

    static boolean split2PartsAndEqualsPart2(String str1, String str2, String splitter) {
        return str1.contains(splitter) && str2.contains(splitter)
                && str1.split(splitter, 2)[1].equals(str2.split(splitter, 2)[1]);
    }

}
