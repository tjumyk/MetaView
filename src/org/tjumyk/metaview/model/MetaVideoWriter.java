package org.tjumyk.metaview.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tool for exporting the meta-video model object into the meta-video
 * description file.
 * 
 * @author 宇锴
 */
public class MetaVideoWriter {
	public static void writeToFile(MetaVideo video, File file)
			throws ParserConfigurationException, MalformedURLException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();

		Element root = document.createElement("MetaVideo");
		root.setAttribute("version", "1.0");
		root.setAttribute("name", video.getName());
		root.setAttribute("total_frames",
				Integer.toString(video.getTotalFrames()));
		String baseUrl = file.getParentFile().toURI().toURL().toExternalForm();
		String movieFileUrl = video.getMovieFile();
		if (movieFileUrl.startsWith(baseUrl)) {
			movieFileUrl = movieFileUrl.substring(baseUrl.length());
		}
		root.setAttribute("movie_file", movieFileUrl);

		if (video.getFrameImageFolder() != null) {
			String imgFolderUrl = video.getFrameImageFolder();
			if (imgFolderUrl.startsWith(baseUrl)) {
				imgFolderUrl = imgFolderUrl.substring(baseUrl.length());
			}
			root.setAttribute("frame_image_folder", imgFolderUrl);
		} else
			root.setAttribute("frame_image_folder", "");
		root.setAttribute("fps", Integer.toString(video.getFps()));
		document.appendChild(root);

		Element segments = document.createElement("segments");
		root.appendChild(segments);
		for (Segment seg : video.getSegments()) {
			Element segElement = document.createElement("segment");
			segElement.setAttribute("key", Integer.toString(seg.getKey()));
			segElement.setAttribute("from", Integer.toString(seg.getFrom()));
			segElement.setAttribute("to", Integer.toString(seg.getTo()));
			segments.appendChild(segElement);
		}

		Element categories = document.createElement("categories");
		root.appendChild(categories);
		for (Category cat : video.getCategories()) {
			Element catElement = document.createElement("category");
			catElement.setAttribute("name", cat.getName());
			Element catInfo = document.createElement("info");
			catInfo.appendChild(document.createTextNode(cat.getInfo()));
			catElement.appendChild(catInfo);
			for (Group group : cat.getGroups()) {
				Element groupElement = document.createElement("group");
				groupElement.setAttribute("name", group.getName());
				groupElement.setAttribute("key",
						Integer.toString(group.getKey()));
				Element groupInfo = document.createElement("info");
				groupInfo.appendChild(document.createTextNode(group.getInfo()));
				groupElement.appendChild(groupInfo);
				for (Segment seg : group.getSegments()) {
					Element segElement = document.createElement("segment");
					segElement.setAttribute("key",
							Integer.toString(seg.getKey()));
					groupElement.appendChild(segElement);
				}
				catElement.appendChild(groupElement);
			}
			categories.appendChild(catElement);
		}
		saveXML(document, file);
	}

	private static void saveXML(Document document, File file) {
		TransformerFactory tf = TransformerFactory.newInstance();
		try {
			Transformer transformer = tf.newTransformer();
			DOMSource source = new DOMSource(document);
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
					"MetaVideo.dtd");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			PrintWriter pw = new PrintWriter(new FileOutputStream(file));
			StreamResult result = new StreamResult(pw);
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			System.out.println(e.getMessage());
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (TransformerException e) {
			System.out.println(e.getMessage());
		}
	}
}
