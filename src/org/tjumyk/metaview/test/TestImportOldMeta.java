package org.tjumyk.metaview.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;
import org.tjumyk.metaview.model.MetaVideo;
import org.tjumyk.metaview.model.MetaVideoImporter;

public class TestImportOldMeta {

	@Test
	public void test() {
		try {
			MetaVideo video = MetaVideoImporter.parseOldMeta(new File(
					"sample/som-old.txt"));
			assertTrue(video != null);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

}
