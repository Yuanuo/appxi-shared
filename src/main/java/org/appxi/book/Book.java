package org.appxi.book;

import org.appxi.util.ext.Attributes;
import org.appxi.util.ext.Node;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

public class Book extends Attributes implements Serializable {
    public String id;
    public String path;
    public String title;
    public String authorInfo;
    public String library;
    public String catalog;
    public String location;
    public String copyright;

    public String summary;
    //

    public final Node<Chapter> chapters = new Node<>();
    //
    public final Collection<String> periods = new HashSet<>();
    public final Collection<String> authors = new HashSet<>();

    public Book setId(String id) {
        this.id = id;
        return this;
    }

    public Book setTitle(String title) {
        this.title = title;
        return this;
    }

    public Book setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public String authorInfo() {
        return this.authorInfo;
    }

    @Override
    public String toString() {
        return this.title;
    }

    public Book clone() {
        Book book = ofBook();
        this.copyTo(book);
        return book;
    }

    public void copyTo(Book book) {
        book.id = this.id;
        book.path = this.path;
        book.title = this.title;
        book.authorInfo = this.authorInfo;
        book.library = this.library;
        book.catalog = this.catalog;
        book.location = this.location;
        book.copyright = copyright;
        book.summary = this.summary;
    }

    public Book ofBook() {
        return new Book();
    }

    public Chapter ofChapter() {
        return new Chapter();
    }
}
