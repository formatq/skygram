package org.formatko.skygram.util;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Map;

/**
 * Класс для работы с xml
 *
 * @author aivanov
 * @since 07.10.2015
 */
public class XmlHelper {

    public static String getXml(String path) throws IOException {
        return getXml(path, null);
    }

    public static String getXml(String path, ClassLoader classLoader) throws IOException {
        return IOUtils.toString(getResourceStream(path, classLoader), "UTF-8");
    }

    public static synchronized String transformXml(String xml, String xsltPath) throws TransformerException {
        StreamSource xmlIn = new StreamSource(new StringReader(xml));
        StreamSource xslt = new StreamSource(getResourceStream(xsltPath, null));

        return transformXml(xmlIn, xslt).getWriter().toString();
    }

    public static synchronized String transformXmlByXslt(String xml, String xslt) throws TransformerException {
        StreamSource xmlIn = new StreamSource(new StringReader(xml));
        StreamSource xsltIn = new StreamSource(new StringReader(xslt));

        return transformXml(xmlIn, xsltIn).getWriter().toString();
    }

    public static synchronized String transformXml(String xml, String xsltPath, Map<String, Object> parameters) throws TransformerException {
        StreamSource xmlIn = new StreamSource(new StringReader(xml));
        StreamSource xslt = new StreamSource(getResourceStream(xsltPath, null));

        return transformXml(xmlIn, xslt, parameters).getWriter().toString();
    }


    public static synchronized String transformXml(String xml, String xsltPath, ClassLoader classLoader) throws TransformerException {
        StreamSource xmlIn = new StreamSource(new StringReader(xml));
        StreamSource xslt = new StreamSource(getResourceStream(xsltPath, classLoader));

        return transformXml(xmlIn, xslt).getWriter().toString();
    }

    public static synchronized String transformXml(String xml, String xsltPath, Map<String, Object> parameters, ClassLoader classLoader) throws TransformerException {
        StreamSource xmlIn = new StreamSource(new StringReader(xml));
        StreamSource xslt = new StreamSource(getResourceStream(xsltPath, classLoader));

        return transformXml(xmlIn, xslt, parameters).getWriter().toString();
    }

    public static synchronized StreamResult transformXml(StreamSource xmlIn, StreamSource xslt) throws TransformerException {
        return transformXml(xmlIn, xslt, null);
    }

    public static synchronized StreamResult transformXml(StreamSource xmlIn, StreamSource xslt, Map<String, Object> parameters) throws TransformerException {
        StreamResult xmlOut = new StreamResult(new StringWriter());
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(xslt);
        if (parameters != null && parameters.size() > 0) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                transformer.setParameter(entry.getKey(), entry.getValue());
            }
        }
        transformer.transform(xmlIn, xmlOut);
        return xmlOut;
    }

    public static synchronized String xpath(String xml, String xPathExpression) throws Exception {
        return XPathFactory.newInstance().newXPath().evaluate(xPathExpression, new InputSource(new StringReader(xml)));

    }

    public static synchronized String xpathAsNode(String xml, String xPathExpression) throws Exception {
        try {
            InputSource inputSource = new InputSource(new StringReader(xml));
            Object result = XPathFactory.newInstance().newXPath().evaluate(xPathExpression, inputSource, XPathConstants.NODESET);
            return serialize((NodeList) result);
        } catch (Exception e) {
            throw e;
        }
    }

    private static String serialize(NodeList nodes) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nodes.getLength(); i++) {
            sb.append(serialize(nodes.item(i)));
        }
        return sb.toString();
    }

    private static String serialize(Node node) throws Exception {
        if (node.getNodeType() == Node.TEXT_NODE) {
            return node.getTextContent();
        } else if (node.getNodeType() == Node.ELEMENT_NODE) {
            return serialize((Element) node);
        } else {
            return "";
        }
    }

    private static String serialize(Element element) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        document.appendChild(document.importNode(element, true));
        return printXml(document);
    }

    private static String printXml(Document doc) throws Exception {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter out = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(out));
        return out.toString();
    }

    public static InputStream getResourceStream(String resourcePath, ClassLoader classLoader) {
        if (classLoader == null) {
            return ClassLoader.getSystemResourceAsStream(resourcePath);
        } else {
            return classLoader.getResourceAsStream(resourcePath);
        }
    }

    public static void validate(String xml, URL xsdPath) throws SAXException, IOException {
        Source source = new StreamSource(new StringReader(xml));
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(xsdPath);
        Validator validator = schema.newValidator();
        validator.validate(source);
    }

    public static void validate(String xml, String xsdPath) throws IOException, SAXException {
        validate(xml, ClassLoader.getSystemResource(xsdPath));
    }

}
