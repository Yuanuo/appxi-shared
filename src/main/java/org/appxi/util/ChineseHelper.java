package org.appxi.util;

import java.util.regex.Pattern;

public interface ChineseHelper {

    /**
     * 使用UnicodeScript方法判断
     *
     * @param codePoint
     * @return
     */
    static boolean isChinese(int codePoint) {
        Character.UnicodeScript sc = Character.UnicodeScript.of(codePoint);
        return sc == Character.UnicodeScript.HAN;
    }

    static int firstChineseIndex(char[] charArr) {
        for (int i = 0; i < charArr.length; i++) {
            if (isChinese(charArr[i]))
                return i;
        }
        return -1;
    }

    static String getChineseText(char[] charArr) {
        return getChineseText(charArr, 0, charArr.length);
    }

    static String getChineseText(char[] charArr, int startIdx) {
        return getChineseText(charArr, 0, charArr.length);
    }

    static String getChineseText(char[] charArr, int startIdx, int endIdx) {
        StringBuilder buf = new StringBuilder();
        for (int i = startIdx; i < endIdx; i++) {
            if (isChinese(charArr[i]))
                buf.append(charArr[i]);
        }
        return buf.toString();
    }

    // 根据UnicodeBlock方法判断中文标点符号
    static boolean isChinesePunctuation(int codePoint) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(codePoint);
        return ub == Character.UnicodeBlock.GENERAL_PUNCTUATION || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
                || ub == Character.UnicodeBlock.VERTICAL_FORMS;
    }

    // 使用UnicodeBlock方法判断
    static boolean isChineseByBlock(int codePoint) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(codePoint);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT;
    }

    // 使用Unicode编码范围来判断汉字；这个方法不准确,因为还有很多汉字不在这个范围之内
    static boolean isChineseByRange(String str) {
        if (str == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("[\\u4E00-\\u9FCC]+");
        return pattern.matcher(str.trim()).find();
    }

}
