package org.appxi.util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public interface XmlSaxHelper {
    static void walk(Path file, XmlSaxVisitor... visitors) throws Exception {
        walk(file, true, visitors);
    }

    static void walk(Path file, boolean skipDomCheck, XmlSaxVisitor... visitors) throws Exception {
        try (InputStream input = new BufferedInputStream(Files.newInputStream(file))) {
            walk(input, skipDomCheck, visitors);
        }
    }

    static void walk(InputStream input, XmlSaxVisitor... visitors) throws Exception {
        walk(input, true, visitors);
    }

    static void walk(InputStream input, boolean skipDomCheck, XmlSaxVisitor... visitors) throws Exception {
        final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        if (skipDomCheck) {
            parserFactory.setValidating(false);
            parserFactory.setFeature("http://xml.org/sax/features/namespaces", false);
            parserFactory.setFeature("http://xml.org/sax/features/validation", false);
            parserFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            parserFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            parserFactory.setNamespaceAware(false);
        }

        parserFactory.newSAXParser().parse(input, new DefaultHandler() {
            final StringBuilder path = new StringBuilder("/");
            List<XmlSaxVisitor> filteredVisitors;
            String pathStr;

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes)
                    throws SAXException {
                if (null == qName || qName.isBlank())
                    throw new RuntimeException();
                path.append(qName).append('/');
                pathStr = path.toString();

                filteredVisitors = new ArrayList<>();
                for (XmlSaxVisitor visitor : visitors) {
                    if (visitor.accept(pathStr, qName)) {

                        filteredVisitors.add(visitor);
                        visitor.preVisitElement(pathStr, qName, attributes);
                    }
                }
            }

            @Override
            public void characters(char[] chars, int start, int length) throws SAXException {
                filteredVisitors
                        .forEach(visitor -> visitor.visitElementContent(pathStr, String.valueOf(chars, start, length)));
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                filteredVisitors.forEach(visitor -> visitor.postVisitElement(pathStr, qName));

                // relink to parent
                path.delete(pathStr.length() - qName.length() - 1, pathStr.length());
                pathStr = path.toString();
            }
        });
    }

    interface XmlSaxVisitor {

        default boolean accept(String path, String tagName) {
            return true;
        }

        default void preVisitElement(String path, String tagName, Attributes attributes) {
        }

        default void visitElementContent(String path, String text) {
        }

        default void postVisitElement(String path, String tagName) {
        }

    }
}
