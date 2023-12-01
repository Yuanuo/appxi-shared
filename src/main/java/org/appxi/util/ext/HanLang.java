package org.appxi.util.ext;

import org.appxi.event.EventBus;
import org.appxi.event.EventType;
import org.appxi.prefs.Preferences;

import java.util.Objects;

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

    public static class Provider {
        final Preferences config;
        final EventBus eventBus;

        HanLang _hanLang;

        public Provider(Preferences config, EventBus eventBus) {
            this.config = config;
            this.eventBus = eventBus;
            //
            eventBus.addEventHandler(Event.CHANGED, event -> _hanLang = event.data());
        }

        public void apply(HanLang hanLang) {
            if (null == hanLang) return;
            //
            config.setProperty("display.han", hanLang.lang);

            if (Objects.equals(_hanLang, hanLang)) {
                return;
            }

            if (null == eventBus) {
                _hanLang = hanLang;
            } else {
                eventBus.fireEvent(new HanLang.Event(HanLang.Event.CHANGED, hanLang));
            }
        }

        public HanLang get() {
            if (null == _hanLang) {
                _hanLang = valueBy(config.getString("display.han", hans.lang));
            }
            return _hanLang;
        }
    }
}
