package org.appxi.util;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public interface XmlW3cHelper {
    String lineSeparator = System.getProperty("line.separator", "\n");

    // private static transient Object lockObj = new Object();
    static Document parseText(final String text) {
        try {
            return readXmlDocument(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)), false, "Parser from text");
        } catch (final Exception e) {
            e.printStackTrace();
            return makeDocument();
        }
    }

    static void writeXmlDocument(final String filename, final Document document) throws Exception {
        writeXmlDocument(filename, document, "UTF-8");
    }

    static void writeXmlDocument(final String filename, final Document document, String encoding) throws Exception {
        if (document == null) {
            return;
        }
        if (filename == null) {
            return;
        }

        final File outFile = new File(filename);
        if (!outFile.getParentFile().exists())
            outFile.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(outFile)) {
            writeXmlDocument(fos, document, encoding, true);
        }
    }

    static void writeXmlDocument(final OutputStream os, final Document document, String encoding, boolean indent) throws Exception {
        if (document == null) {
            return;
        }
        if (os == null) {
            return;
        }
        // final OutputFormat format = new OutputFormat(document, encoding,
        // true);
        // format.setLineWidth(200);
        // format.setIndent(4);
        // Writer writer = new OutputStreamWriter(os, encoding);
        // final XMLSerializer serializer = new XMLSerializer(writer, format);
        // serializer.asDOMSerializer();
        // serializer.serialize(document.getDocumentElement());

        // 开始把Document映射到文件
        TransformerFactory transFactory = TransformerFactory.newInstance();
        transFactory.setAttribute("indent-number", indent ? 2 : 0);
        Transformer transFormer = transFactory.newTransformer();

        transFormer.setOutputProperty(OutputKeys.ENCODING, encoding);
        transFormer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transFormer.setOutputProperty(OutputKeys.INDENT, indent ? "yes" : "no");
        transFormer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", indent ? "2" : "0");
        // transFormer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount",
        // "4");
        // 设置输出结果
        DOMSource domSource = new DOMSource(document);
        // 设置输入源
        StreamResult xmlResult = new StreamResult(new OutputStreamWriter(os, encoding));
        // 输出xml文件
        transFormer.transform(domSource, xmlResult);

    }

    static Node adoptNode(final Document doc, final Node ele) {
        return doc.adoptNode(ele);
    }

    static Document readXmlDocument(final String content) throws Exception {
        return readXmlDocument(content, false);
    }

    static Document readXmlDocument(final String content, final boolean validate) throws Exception {
        if (content == null) {
            return null;
        }
        final ByteArrayInputStream bis = new ByteArrayInputStream(content.getBytes());
        return readXmlDocument(bis, validate, "Internal Content");
    }

    static Document readXmlDocument(final URL url) throws Exception {
        return readXmlDocument(url, false);
    }

    static Document readXmlDocument(final URL url, final boolean validate) throws Exception {
        if (url == null) {
            return null;
        }
        return readXmlDocument(url.openStream(), validate, url.toString());
    }

    static Document readXml(final String filePath) {
        try {
            return readXmlDocument(new FileInputStream(filePath));
        } catch (final Exception e) {
            System.err.println("XmlUtils.readXml failure:" + e.getMessage());
        }
        return null;
    }

    static Document readXmlDocument(final InputStream is) {
        try {
            return readXmlDocument(is, false, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static Document readXmlDocument(final InputStream is, final boolean validate, final String docDescription) throws Exception {
        try {
            if (is == null)
                return null;
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(validate);
            factory.setFeature("http://xml.org/sax/features/namespaces", false);
            factory.setFeature("http://xml.org/sax/features/validation", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            final DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(is);
        } finally {
            if (null != is)
                is.close();
        }
    }

    static Document makeDocument() {
        return makeDocument(null);
    }

    static Document makeDocument(final String rootElementName) {
        Document document = null;
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        try {
            final DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument();
        } catch (final Exception ignored) {
        }
        if (null != document && rootElementName != null) {
            final Element rootElement = document.createElement(rootElementName);
            document.appendChild(rootElement);
        }
        return document;
    }

    static Element addChildElement(final Element element, final String childElementName) {
        final Element newElement = element.getOwnerDocument().createElement(childElementName);
        element.appendChild(newElement);
        return newElement;
    }

    static void removeAllChild(final Element element) {
        final NodeList list = element.getChildNodes();
        for (final int i = 0; i < list.getLength(); ) {
            final Node node = list.item(i);
            element.removeChild(node);
        }
    }

    static Element addChildElementValue(final Element element, final String childElementName, final String childElementValue) {
        final Element newElement = addChildElement(element, childElementName);
        newElement.appendChild(element.getOwnerDocument().createTextNode(childElementValue));
        return newElement;
    }

    static Element addChildElementCDATAValue(final Element element, final String childElementName, final String childElementValue) {
        final Element newElement = addChildElement(element, childElementName);
        newElement.appendChild(element.getOwnerDocument().createCDATASection(childElementValue));
        return newElement;
    }

    static Element addChildElementCDATAValue(final Element element, final String childElementValue) {
        element.appendChild(element.getOwnerDocument().createCDATASection(childElementValue));
        return element;
    }

    static List<Element> childElementList(final Element element, final String childElementName) {
        final List<Element> result = new ArrayList<>(8);
        if (element == null) {
            return result;
        }
        final NodeList list = element.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            final Node node = list.item(i);
            if (node instanceof Element) {
                if (null == childElementName || ((Element) node).getTagName().equals(childElementName))
                    result.add((Element) node);

            }
        }
        return result;
    }

    static List<Element> childEleAllList(final Element element, final String childElementName) {
        final List<Element> result = new ArrayList<>(8);
        if (element == null) {
            return result;
        }
        final NodeList list = null == childElementName ? element.getChildNodes() : element.getElementsByTagName(childElementName);
        for (int i = 0; i < list.getLength(); i++) {
            final Node node = list.item(i);
            if (node instanceof Element)
                result.add((Element) node);
        }
        return result;
    }

    static Element firstChildElement(final Element element, final String childElementName) {
        if (element == null) {
            return null;
        }
        Node node = element.getFirstChild();
        if (node != null) {
            do {
                if (node.getNodeType() == Node.ELEMENT_NODE && (childElementName == null || childElementName.equals(node.getNodeName()))) {
                    return (Element) node;
                }
            } while ((node = node.getNextSibling()) != null);
        }
        return null;
    }

    static Element firstChildElement(final Element element, final String childElementName, final String attrName, final String attrValue) {
        if (element == null) {
            return null;
        }
        Node node = element.getFirstChild();
        if (node != null) {
            do {
                if (node.getNodeType() == Node.ELEMENT_NODE && (childElementName == null || childElementName.equals(node.getNodeName()))) {
                    final Element childElement = (Element) node;
                    final String value = childElement.getAttribute(attrName);
                    if (value != null && value.equals(attrValue)) {
                        return childElement;
                    }
                }
            } while ((node = node.getNextSibling()) != null);
        }
        return null;
    }

    static String childElementValue(final Element element, final String childElementName) {
        if (element == null) {
            return null;
        }
        final Element childElement = firstChildElement(element, childElementName);
        return elementValue(childElement);
    }

    static String elementValue(final Element element) {
        if (element == null) {
            return null;
        }
        element.normalize();
        final Node node = element.getFirstChild();
        if (node == null) {
            return null;
        }
        if (node instanceof CDATASection) {
            return ((CDATASection) node).getData();
        }
        return node.getNodeValue();
    }

    static String getCDATAValue(final Element element) {
        if (element == null) {
            return null;
        }
        element.normalize();
        final NodeList nodel = element.getChildNodes();
        Node node;
        for (int i = 0; i < nodel.getLength(); i++) {
            node = nodel.item(i);
            if (node instanceof CDATASection) {
                return ((CDATASection) node).getData();
            }
        }
        return null;
    }

    static Element firstChildElement(final Element element, final String attrName, final String attrValue) {
        if (element == null) {
            return null;
        }
        Node node = element.getFirstChild();
        if (node != null) {
            do {
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Element childElement = (Element) node;
                    final String value = childElement.getAttribute(attrName);
                    if (value != null && value.equalsIgnoreCase(attrValue)) {
                        return childElement;
                    }
                }
            } while ((node = node.getNextSibling()) != null);
        }
        return null;
    }

    static byte[] translateXML(final InputStream xml, final InputStream xsl) throws TransformerException {
        final ByteArrayOutputStream ret = new ByteArrayOutputStream();
        final TransformerFactory tf = TransformerFactory.newInstance();
        final Source xmlSource = new StreamSource(xml);
        final Source xslSource = new StreamSource(xsl);
        final Templates template = tf.newTemplates(xslSource);
        final Transformer transformer = template.newTransformer();
        transformer.transform(xmlSource, new StreamResult(ret));
        return ret.toByteArray();
    }

    static Document newDom(final String rootEleText) {
        return makeDocument(rootEleText);
    }

    static Element addEle(final Element parentNode, final String tagName) {
        final Element ele = parentNode.getOwnerDocument().createElement(tagName);
        parentNode.appendChild(ele);
        return ele;
    }

    static void clear(final Node node) {
        if (null == node)
            return;
        final NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            node.removeChild(nodes.item(i--));
        }
    }

    private static String normalize(final String s) {
        final StringBuilder buff = new StringBuilder();
        final int i = s == null ? 0 : s.length();
        for (int j = 0; j < i; j++) {
            final char c = s.charAt(j);
            switch (c) {
                case 60: // '<'
                    buff.append("&lt;");
                    break;

                case 62: // '>'
                    buff.append("&gt;");
                    break;

                case 38: // '&'
                    buff.append("&amp;");
                    break;

                case 34: // '"'
                    buff.append("&quot;");
                    break;

                case 10: // '\n'
                    if (j > 0) {
                        final char c1 = buff.charAt(buff.length() - 1);
                        if (c1 != '\r') {
                            buff.append(lineSeparator);
                        } else {
                            buff.append('\n');
                        }
                    } else {
                        buff.append(lineSeparator);
                    }
                    break;

                default:
                    buff.append(c);
                    break;
            }
        }

        return buff.toString();
    }

    private static void print(final Node node, final PrintWriter printwriter) {
        if (node == null) {
            return;
        }
        boolean flag = false;
        final short word0 = node.getNodeType();
        switch (word0) {
// '\t'
            case 9 -> {
                printwriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                final NodeList nodelist = node.getChildNodes();
                if (nodelist != null) {
                    final int i = nodelist.getLength();
                    for (int k = 0; k < i; k++) {
                        print(nodelist.item(k), printwriter);

                    }
                }
            }
// '\001'
            case 1 -> {
                printwriter.print('<' + node.getNodeName());
                final NamedNodeMap namednodemap = node.getAttributes();
                final int j = namednodemap == null ? 0 : namednodemap.getLength();
                for (int l = 0; l < j; l++) {
                    final Attr attr = (Attr) namednodemap.item(l);
                    printwriter.print(' ' + attr.getNodeName() + "=\"" + normalize(attr.getValue()) + '"');
                }
                final NodeList nodelist1 = node.getChildNodes();
                if (nodelist1 != null) {
                    final int i1 = nodelist1.getLength();
                    flag = i1 > 0;
                    if (flag) {
                        printwriter.print('>');
                    }
                    for (int j1 = 0; j1 < i1; j1++) {
                        print(nodelist1.item(j1), printwriter);

                    }
                } else {
                    flag = false;
                }
                if (!flag) {
                    printwriter.println("/>");
                }
            }
// '\005'
            case 5 -> {
                printwriter.print('&');
                printwriter.print(node.getNodeName());
                printwriter.print(';');
            }
// '\004'
            case 4 -> {
                printwriter.print("<![CDATA[");
                printwriter.print(node.getNodeValue());
                printwriter.println("]]>");
            }
// '\003'
            case 3 -> printwriter.print(normalize(node.getNodeValue()));
// '\b'
            case 8 -> {
                printwriter.print("<!--");
                printwriter.print(node.getNodeValue());
                printwriter.println("-->");
            }
// '\007'
            case 7 -> {
                printwriter.print("<?");
                printwriter.print(node.getNodeName());
                final String s = node.getNodeValue();
                if (s != null && s.length() > 0) {
                    printwriter.print(' ');
                    printwriter.print(s);
                }
                printwriter.println("?>");
            }
        }
        if (word0 == 1 && flag) {
            printwriter.print("</");
            printwriter.print(node.getNodeName());
            printwriter.println('>');
            // boolean flag1 = false;
        }
    }

    static String asXml(final Node node) {
        final StringWriter stringwriter = new StringWriter();
        asXml(node, stringwriter);
        return stringwriter.toString();
    }

    static void asXml(final Node node, final Writer writer) {
        print(node, new PrintWriter(writer));
    }

    static String asXml(final Node node, final String charSet) {
        final String str = asXml(node);
        try {
            return new String(str.getBytes(charSet));
            // return new String(str.getBytes("UTF-8"), charSet);
        } catch (final UnsupportedEncodingException e) {
            return str;
        }
    }

    static String asText(final Node node, boolean trimText) {
        final StringWriter stringwriter = new StringWriter();
        asText(node, trimText, stringwriter);
        return stringwriter.toString();
    }

    static void asText(Node node, boolean trimText, Writer writer) {
        if (null == node)
            return;
        NodeList nodes = node.getChildNodes();
        if (null != nodes && nodes.getLength() > 0) {
            for (int i = 0; i < nodes.getLength(); i++) {
                node = nodes.item(i);
                asText(node, trimText, writer);
            }
        } else
            printText(node, trimText, writer);
    }

    static void printText(Node node, boolean trimText, Writer writer) {
        String text = node.getNodeValue();
        if (null != text) {
            text = trimText ? text.trim() : text;
            if (text.length() > 0)
                try {
                    writer.write(text);
                    writer.write("\r\n");// as new line
                } catch (IOException ignored) {
                }
        }
    }

    static String safeGetText(final Element element) {
        if (null == element)
            return null;
        String text = element.getAttribute("value");
        String tmpTxt;
        if (null == text || "".equals(text)) {
            tmpTxt = element.getTextContent();
            if (null != tmpTxt)
                text = tmpTxt;
        }
        return null != text ? text.trim() : null;
    }

    /**
     * 添加Element并使用Text存储数据格式,添加前将移除已经存在的定义
     */
    static void addEle(final Element parentNode, final String tagName, final String name, final Object value) {
        final Element oldEle = firstChildElement(parentNode, tagName, "name", name);
        if (null != oldEle)
            parentNode.removeChild(oldEle);
        final Element ele = addEle(parentNode, tagName);
        ele.setAttribute("name", name);
        ele.appendChild(parentNode.getOwnerDocument().createTextNode(String.valueOf(value)));
    }

    /**
     * 添加Element并使用Text存储数据格式,添加前将移除已经存在的定义
     */
    static void addEle(final Element parentNode, final String tagName, final Object value) {
        final Element oldEle = firstChildElement(parentNode, tagName);
        if (null != oldEle)
            parentNode.removeChild(oldEle);
        final Element ele = addEle(parentNode, tagName);
        ele.appendChild(parentNode.getOwnerDocument().createTextNode(String.valueOf(value)));
    }
}
