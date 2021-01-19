package org.appxi.util;

import org.appxi.util.ext.Node;
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
    Object AK_VISITORS = new Object();

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
            private Node<String> node = new Node<>();

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes)
                    throws SAXException {
                if (null == qName || qName.isBlank())
                    throw new RuntimeException();

                node = node.add(qName);
                List<XmlSaxVisitor> filteredVisitors = new ArrayList<>();
                for (XmlSaxVisitor visitor : visitors) {
                    if (visitor.accept(node, attributes)) {
                        filteredVisitors.add(visitor);
                    }
                }
                if (!filteredVisitors.isEmpty()) {
                    filteredVisitors.forEach(visitor -> visitor.preVisitElement(node, attributes));
                    node.attr(AK_VISITORS, filteredVisitors);
                }
            }

            @Override
            public void characters(char[] chars, int start, int length) throws SAXException {
                if (node.hasAttr(AK_VISITORS)) {
                    String text = String.valueOf(chars, start, length);
                    ((List<XmlSaxVisitor>) node.attr(AK_VISITORS)).forEach(visitor -> visitor.visitElementContent(node, text));
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                if (node.hasAttr(AK_VISITORS)) {
                    ((List<XmlSaxVisitor>) node.removeAttr(AK_VISITORS)).forEach(visitor -> visitor.postVisitElement(node));
                }
                node = node.parent();
            }
        });
    }

    interface XmlSaxVisitor {

        default boolean accept(Node<String> node, Attributes attributes) {
            return true;
        }

        default void preVisitElement(Node<String> node, Attributes attributes) {
        }

        default void visitElementContent(Node<String> node, String text) {
        }

        default void postVisitElement(Node<String> node) {
        }
    }
}
