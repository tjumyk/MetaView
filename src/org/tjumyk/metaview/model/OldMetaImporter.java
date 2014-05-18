package org.tjumyk.metaview.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class OldMetaImporter {
	private static final int DEFAULT_FPS = 25;

	public static MetaVideo parseOldMeta(File metaFile) throws IOException {
		String content = FileUtils.readFileToString(metaFile);
		MetaVideo video = new MetaVideo();
		video.setName(FilenameUtils.getBaseName(metaFile.getName()));
		video.setFps(DEFAULT_FPS);
		Map<Integer, Segment> segMap = new HashMap<>();
		String[] lines = content.split("\n");
		for (int i = 0; i < lines.length; i++) {
			lines[i] = lines[i].trim();
		}
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.length() <= 0)
				continue;
			if (line.equals("TOTALFRAMES")) {
				video.setTotalFrames(Integer.parseInt(lines[++i]));
			} else if (line.equals("MOVIEPATH")) {
				String path = escapeQuotes(lines[++i]);
				String file = MetaVideoParser.parsePath(metaFile, path, true);
				video.setMovieFile(file);
			} else if (line.equals("KEYFRAMEFOLDER")) {
				String path = escapeQuotes(lines[++i]);
				String folder = MetaVideoParser
						.parsePath(metaFile, path, false);
				video.setFrameImageFolder(folder);
			} else if (line.equals("HEADERFILE")) {
				++i;// ignored
			} else if (line.equals("SEGMENT")) {
				int segCount = 0;
				while (true) {
					String segLine = lines[++i];
					if (segLine.equals("END"))
						break;
					String[] segParts = segLine.split(" +");
					Segment seg = new Segment();
					seg.setFrom(Integer.parseInt(segParts[1]));
					seg.setTo(Integer.parseInt(segParts[2]));
					int key = Integer.parseInt(segParts[3]);
					seg.setKey(key);
					seg.setIndex(segCount);
					segMap.put(key, seg);
					video.getSegments().add(seg);
					segCount++;
				}
			} else if (line.startsWith("CATEGORY")) {
				Category cat = new Category();
				video.getCategories().add(cat);
				cat.setName(escapeQuotes(line.split(" ")[1]));
				StringBuilder sb = new StringBuilder();
				int blockLines = readTextBlock(sb, lines, ++i);
				i += blockLines - 1;
				cat.setInfo(sb.toString());

				while (true) {
					String catLine = null;
					while (true) {
						catLine = lines[++i];
						if (catLine.length() > 0)
							break;
					}
					if (catLine.equals("END"))
						break;
					Group group = new Group();
					cat.getGroups().add(group);
					group.setKey(Integer.parseInt(catLine));
					group.setCategory(cat);
					group.setName(escapeQuotes(lines[++i]));
					String[] keyList = lines[++i].split(" +");
					for (String keyStr : keyList) {
						int key = Integer.parseInt(keyStr);
						group.getSegments().add(segMap.get(key));
					}
					StringBuilder sb2 = new StringBuilder();
					int blockLines2 = readTextBlock(sb2, lines, ++i);
					i += blockLines2 - 1;
					group.setInfo(sb2.toString());
				}
			}
		}
		return video;
	}

	private static int readTextBlock(StringBuilder sb, String[] lines,
			int startLine) {
		int currentLine = startLine;
		while (true) {
			if (currentLine >= lines.length)
				break;
			String line = lines[currentLine].trim();
			if (line.length() <= 0)
				break;
			if (line.equals("END"))
				break;
			sb.append(escapeQuotes(line));
			currentLine++;
		}
		return currentLine - startLine;
	}

	private static String escapeQuotes(String str) {
		if (str.startsWith("\"") && str.endsWith("\""))
			return str.substring(1, str.length() - 1);
		return str;
	}
}
