package org.tjumyk.metaview.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.tjumyk.metaview.data.DataLoader;
import org.tjumyk.metaview.util.XMLParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MetaVideoParser {
	public static MetaVideo parse(File file) throws Exception {
		InputStream xmlStream = new FileInputStream(file);
		Document doc = XMLParser.parseValid(xmlStream,
				DataLoader.load("MetaVideo.dtd"));
		Element root = doc.getDocumentElement();

		MetaVideo video = new MetaVideo();
		video.setVersion(root.getAttribute("version"));
		video.setName(root.getAttribute("name"));
		video.setTotalFrames(Integer.parseInt(root.getAttribute("total_frames")));
		video.setFps(Integer.parseInt(root.getAttribute("fps")));
		video.setWidth(Integer.parseInt(root.getAttribute("width")));
		video.setHeight(Integer.parseInt(root.getAttribute("height")));

		String filePath = root.getAttribute("movie_file");
		File movieFile = new File(filePath);
		if (!movieFile.exists() || !movieFile.isFile()) {
			movieFile = new File(file.getParent() + File.separatorChar
					+ filePath);
			if (!movieFile.exists() || !movieFile.isFile())
				movieFile = null;
		}
		if (movieFile != null)
			video.setMovieFile(movieFile.getAbsolutePath());

		Element segmentsElement = XMLParser.getDirectChildElementsByName(root,
				"segments").get(0);
		Element categoriesElement = XMLParser.getDirectChildElementsByName(
				root, "categories").get(0);

		Map<Integer, Segment> segmentMap = new HashMap<>();
		int segCount = 0;
		for (Element segmentElement : XMLParser.getDirectChildElementsByName(
				segmentsElement, "segment")) {
			Segment seg = new Segment();
			seg.setIndex(segCount);
			seg.setFrom(Integer.parseInt(segmentElement.getAttribute("from")));
			seg.setTo(Integer.parseInt(segmentElement.getAttribute("to")));
			int key = Integer.parseInt(segmentElement.getAttribute("key"));
			seg.setKey(key);
			video.getSegments().add(seg);
			segmentMap.put(key, seg);
			++segCount;
		}

		for (Element categoryElement : XMLParser.getDirectChildElementsByName(
				categoriesElement, "category")) {
			Category cat = new Category();
			cat.setName(categoryElement.getAttribute("name"));
			cat.setInfo(XMLParser.getFirstDirectChildElementByName(
					categoryElement, "info").getTextContent());

			for (Element groupElement : XMLParser.getDirectChildElementsByName(
					categoryElement, "group")) {
				Group group = new Group();
				group.setKey(Integer.parseInt(groupElement.getAttribute("key")));
				group.setName(groupElement.getAttribute("name"));
				group.setInfo(XMLParser.getFirstDirectChildElementByName(
						groupElement, "info").getTextContent());

				for (Element segmentElement : XMLParser
						.getDirectChildElementsByName(groupElement, "segment")) {
					int key = Integer.parseInt(segmentElement
							.getAttribute("key"));
					group.getSegments().add(segmentMap.get(key));
				}
				cat.getGroups().add(group);
			}

			video.getCategories().add(cat);
		}

		return video;
	}
}
