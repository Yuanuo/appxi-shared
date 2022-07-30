package org.appxi.book;

import org.appxi.util.ext.Attributes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Chapter extends Attributes implements Serializable {
    private static final Object AK_PARAGRAPHS = new Object();

    public String type;
    public String id;
    public String title;
    public String path;

    public String anchor;

    public Chapter() {
    }

    public Chapter(String type, String id, String title, String path, String anchor) {
        this.type = type;
        this.id = id;
        this.title = title;
        this.path = path;
        this.anchor = anchor;
    }

    public Chapter setType(String type) {
        this.type = type;
        return this;
    }

    public Chapter setId(String id) {
        this.id = id;
        return this;
    }

    public Chapter setTitle(String title) {
        this.title = title;
        return this;
    }

    public Chapter setPath(String path) {
        this.path = path;
        return this;
    }

    public List<Paragraph> paragraphs() {
        return attr(AK_PARAGRAPHS);
    }

    public Chapter addParagraph(String caption, String content) {
        attrOr(AK_PARAGRAPHS, ArrayList<Paragraph>::new).add(new Paragraph(caption, content));
        return this;
    }

    public boolean hasParagraphs() {
        final List<Paragraph> paragraphs = this.paragraphs();
        return null != paragraphs && !paragraphs.isEmpty();
    }

    @Override
    public String toString() {
        return title;
    }
}
