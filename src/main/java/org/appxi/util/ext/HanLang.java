package org.appxi.util.ext;

import org.appxi.event.EventBus;
import org.appxi.event.EventType;
import org.appxi.prefs.UserPrefs;

import java.util.Objects;
import java.util.function.BiFunction;

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

    @Override
    public String toString() {
        return text;
    }

    public static HanLang valueBy(String lang) {
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

    public static class Event extends org.appxi.event.Event {
        public static final EventType<Event> CHANGED = new EventType<>(org.appxi.event.Event.ANY, "HAN_LANG_CHANGED");

        public Event(EventType<Event> eventType, Object data) {
            super(eventType, data);
        }
    }

    private static HanLang _hanLang;
    private static EventBus _eventBus;
    private static BiFunction<String, HanLang, String> _hanTextConvertor;

    public static void setup(EventBus eventBus, BiFunction<String, HanLang, String> hanTextConvertor) {
        _eventBus = eventBus;
        _hanTextConvertor = hanTextConvertor;
        //
        eventBus.addEventHandler(Event.CHANGED, event -> _hanLang = event.data());
    }

    public static void apply(HanLang hanLang) {
        if (null == hanLang || Objects.equals(_hanLang, hanLang)) return;
        //

        UserPrefs.prefs.setProperty("display.han", hanLang.lang);

        if (null == _eventBus) {
            _hanLang = hanLang;
        } else {
            _eventBus.fireEvent(new HanLang.Event(HanLang.Event.CHANGED, hanLang));
        }
    }

    public static HanLang get() {
        if (null == _hanLang) {
            _hanLang = valueBy(UserPrefs.prefs.getString("display.han", hans.lang));
        }
        return _hanLang;
    }

    public static String convert(String text) {
        return null == text ? "" : (null == _hanTextConvertor ? text : _hanTextConvertor.apply(text, get()));
    }

}
