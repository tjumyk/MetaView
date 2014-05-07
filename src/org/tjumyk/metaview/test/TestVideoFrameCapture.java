package org.tjumyk.metaview.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import javafx.scene.image.Image;

import org.junit.Test;
import org.tjumyk.metaview.media.VideoFrameCapture;

public class TestVideoFrameCapture {

	@Test
	public void test() {
		try {
			final Image img = VideoFrameCapture.capture("sample/somfinal3.mp4",
					12090 / 25, 50);
			assertTrue(img != null);
			while (img.getProgress() < 1.0) {
				Thread.sleep(100);
			}
			assertTrue(img.getWidth() > 0 && img.getHeight() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

}
