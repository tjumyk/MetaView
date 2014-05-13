package org.tjumyk.metaview.media;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;

import org.tjumyk.metaview.Main;
import org.tjumyk.metaview.data.DataLoader;

public class VideoFrameCapture {
	private static File ffmpeg;

	public static Image getCache(String video, double second, int height)
			throws IOException, InterruptedException,
			VideoFrameCaptureException {
		File tmp = new File(Main.TEMP_DIR + video.hashCode() + "_" + second
				+ "_" + height + ".jpg");
		Image img = null;
		if (tmp.exists())
			img = new Image(tmp.toURI().toURL().toExternalForm());
		return img;
	}

	public static Image capture(String video, double second, int height)
			throws IOException, InterruptedException,
			VideoFrameCaptureException {
		File tmp = new File(Main.TEMP_DIR + video.hashCode() + "_" + second
				+ "_" + height + ".jpg");
		if (!tmp.exists()) {
			checkLoadFFmpeg();
			final ProcessBuilder pb = new ProcessBuilder();
			pb.redirectErrorStream(true);

			List<String> cmdList = new ArrayList<>();
			cmdList.add(ffmpeg.getAbsolutePath());
			cmdList.add("-ss");
			cmdList.add(Double.toString(second));
			cmdList.add("-i");
			cmdList.add(video);
			cmdList.add("-y");
			cmdList.add("-vframes");
			cmdList.add("1");
			cmdList.add("-filter:v");
			cmdList.add("scale=\"-1:" + height + "\"");
			cmdList.add(tmp.getAbsolutePath());

			pb.command(cmdList);
			Process p = pb.start();
			String output = read(p.getInputStream());
			if (p.waitFor() != 0) {
				throw new VideoFrameCaptureException("FFmpeg error:" + output);
			}
		}
		Image img = new Image(tmp.toURI().toURL().toExternalForm());
		return img;
	}

	public synchronized static void checkLoadFFmpeg() throws IOException {
		if (ffmpeg == null || !ffmpeg.exists()) {
			ffmpeg = DataLoader.loadToTemp("ffmpeg.exe");
			ffmpeg.deleteOnExit();
		}
	}

	private static String read(InputStream is) {
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		boolean firstLine = true;
		try {
			br = new BufferedReader(new InputStreamReader(is), 500);

			String line = "";
			while ((line = br.readLine()) != null) {
				// System.out.println(line);
				if (firstLine) {
					firstLine = false;
					continue;
				}
				if (line.startsWith("  "))
					continue;

				if (!firstLine)
					sb.append('\n');
				sb.append(line);
			}
			br.close();
		} catch (Exception e) {
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (Exception e) {
			}
		}
		return sb.toString();
	}

	public static class VideoFrameCaptureException extends Exception {
		private static final long serialVersionUID = -6041667451915158117L;

		public VideoFrameCaptureException() {
			super();
		}

		public VideoFrameCaptureException(String message) {
			super(message);
		}
	}
}
