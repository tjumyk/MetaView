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

public class XMLParser {
	private static class ParserState {
		boolean ok = true;
		Exception exception = null;
	}

	public static Document parseValid(InputStream xmlStream) throws Exception {
		return parse(xmlStream, null, true);
	}

	public static Document parseValid(InputStream xmlStream,
			InputStream dtdStream) throws Exception {
		return parse(xmlStream, dtdStream, true);
	}

	public static Document parse(InputStream xmlStream) throws Exception {
		return parse(xmlStream, null, false);
	}

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
