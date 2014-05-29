package org.tjumyk.metaview.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Utility class for parsing XMLs. It parse the text content of XMLs and build
 * the {@link Document} object. It support DTD validation, and offers some
 * support functions for document visit.
 * 
 * @author 宇锴
 */
public class XMLParser {
	/**
	 * The encapsulation class of the parse result and possibly the exception
	 * thrown from the parse process.
	 * 
	 * @author 宇锴
	 */
	private static class ParserState {
		boolean ok = true;
		Exception exception = null;
	}

	/**
	 * Parse the XML, and use the inline DTD in XML to validate it.
	 * 
	 * @param xmlStream
	 *            XML input stream
	 * @return document object
	 * @throws Exception
	 *             if parse error
	 */
	public static Document parseValid(InputStream xmlStream) throws Exception {
		return parse(xmlStream, null, true);
	}

	/**
	 * Parse the XML, and use standalone DTD to validate it.
	 * 
	 * @param xmlStream
	 *            XML input stream
	 * @param dtdStream
	 *            DTD input stream
	 * @return document object
	 * @throws Exception
	 *             if parse error
	 */
	public static Document parseValid(InputStream xmlStream,
			InputStream dtdStream) throws Exception {
		return parse(xmlStream, dtdStream, true);
	}

	/**
	 * Parse the XML, no validation.
	 * 
	 * @param xmlStream
	 *            XML input stream
	 * @return document object
	 * @throws Exception
	 *             if parse error
	 */
	public static Document parse(InputStream xmlStream) throws Exception {
		return parse(xmlStream, null, false);
	}

	/**
	 * Parse the XML
	 * 
	 * @param xmlStream
	 *            XML input stream
	 * @param dtdStream
	 *            standalone DTD input stream
	 * @param validate
	 *            if use DTD to validate the XML
	 * @return document object
	 * @throws Exception
	 *             if parse error
	 */
	private static Document parse(InputStream xmlStream, InputStream dtdStream,
			boolean validate) throws Exception {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setValidating(validate);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		if (dtdStream != null) {
			builder.setEntityResolver((publicId, systemId) -> {
				if ((publicId != null && publicId.endsWith(".dtd"))
						|| (systemId != null && systemId.endsWith(".dtd")))
					return new InputSource(dtdStream);
				return null;
			});
		}
		final ParserState state = new ParserState();
		builder.setErrorHandler(new ErrorHandler() {

			@Override
			public void warning(SAXParseException exception)
					throws SAXException {
				state.ok = false;
				state.exception = exception;
			}

			@Override
			public void fatalError(SAXParseException exception)
					throws SAXException {
				state.ok = false;
				state.exception = exception;
			}

			@Override
			public void error(SAXParseException exception) throws SAXException {
				state.ok = false;
				state.exception = exception;
			}
		});
		Document document = builder.parse(new InputSource(xmlStream));
		if (!state.ok && state.exception != null)
			throw state.exception;
		return document;
	}

	/**
	 * Under the given parent node, find the FIRST direct child node with the
	 * given tag name.
	 * 
	 * @param element
	 *            parent element
	 * @param name
	 *            tag name
	 * @return element object
	 */
	public static Element getFirstDirectChildElementByName(Element element,
			String name) {
		NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node instanceof Element) {
				Element ele = (Element) node;
				if (name.equalsIgnoreCase(ele.getTagName())) {
					return ele;
				}
			}
		}
		return null;
	}

	/**
	 * Under the given parent node, find ALL the direct child nodes with the
	 * given tag name.
	 * 
	 * @param element
	 *            parent element
	 * @param name
	 *            tag name
	 * @return element object
	 */
	public static List<Element> getDirectChildElementsByName(Element element,
			String name) {
		List<Element> list = new ArrayList<Element>();
		NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node instanceof Element) {
				Element ele = (Element) node;
				if (name.equalsIgnoreCase(ele.getTagName())) {
					list.add(ele);
				}
			}
		}
		return list;
	}
}
