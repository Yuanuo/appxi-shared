package org.appxi.util.ext;

public enum HanLang {
    zh("zh", "中文"),
    hans("zh-Hans", "中文简体"),
    hant("zh-Hant", "中文繁体"),
    hansCN("zh-Hans-CN", "中文简体（大陆）"),
    hantTW("zh-Hant-TW", "中文繁体（台湾）"),
    hantHK("zh-Hant-HK", "中文繁体（香港）"),
    hantMO("zh-Hant-MO", "中文繁体（澳门）"),
    hantSG("zh-Hant-SG", "中文繁体（新加坡）");

    public final String lang, text;

    HanLang(String lang, String text) {
        this.lang = lang;
        this.text = text;
    }

    static HanLang valueBy(String lang) {
        lang = null != lang ? lang.toLowerCase() : "";
        return switch (lang) {
            case "zh-hans", "hans", "zh_cn", "zh-cn" -> hans;
            case "zh-hant", "hant" -> hant;
            case "zh_tw", "zh-tw", "zh-hant-tw" -> hantTW;
            case "zh_hk", "zh-hk", "zh-hant-hk" -> hantHK;
            case "zh_mo", "zh-mo", "zh-hant-mo" -> hantMO;
            case "zh_sg", "zh-sg", "zh-hant-sg" -> hantSG;
            default -> zh;
        };
    }
}
