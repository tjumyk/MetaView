package org.tjumyk.metaview.test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.tjumyk.metaview.model.MetaVideo;
import org.tjumyk.metaview.model.MetaVideoParser;
import org.tjumyk.metaview.model.MetaVideoWriter;

public class TestWriteXML {

	@Test
	public void test() {
		try {
			MetaVideo video = MetaVideoParser.parse(new File("sample/som.mvd"));
			File tmpFile = File.createTempFile("temp", ".mvd");
			tmpFile.deleteOnExit();
			MetaVideoWriter.writeToFile(video, tmpFile);
			MetaVideo video2 = MetaVideoParser.parse(tmpFile);
			assertTrue(video2 != null);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

}
