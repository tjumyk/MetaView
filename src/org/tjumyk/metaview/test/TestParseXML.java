package org.tjumyk.metaview.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;
import org.tjumyk.metaview.model.MetaVideo;
import org.tjumyk.metaview.model.MetaVideoParser;

public class TestParseXML {

	@Test
	public void test() {
		try {
			MetaVideo video = MetaVideoParser.parse(new File("sample/som.mvd"));
			assertTrue(video != null);

			MetaVideo video2 = MetaVideoParser.parse(new File(
					"sample/som-local-image.mvd"));
			assertTrue(video2 != null);

			MetaVideo video3 = MetaVideoParser.parse(new File(
					"sample/som-online.mvd"));
			assertTrue(video3 != null);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

}
