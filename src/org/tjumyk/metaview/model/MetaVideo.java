package org.tjumyk.metaview.model;

import java.util.ArrayList;
import java.util.List;

public class MetaVideo {
	private String version, name, movieFile,frameImageFolder;
	private int totalFrames,fps;

	public int getFps() {
		return fps;
	}

	public void setFps(int fps) {
		this.fps = fps;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMovieFile() {
		return movieFile;
	}

	public void setMovieFile(String movieFile) {
		this.movieFile = movieFile;
	}

	public int getTotalFrames() {
		return totalFrames;
	}

	public void setTotalFrames(int totalFrames) {
		this.totalFrames = totalFrames;
	}

	public List<Segment> getSegments() {
		return segments;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public String getFrameImageFolder() {
		return frameImageFolder;
	}

	public void setFrameImageFolder(String frameImageFolder) {
		this.frameImageFolder = frameImageFolder;
	}

	private List<Segment> segments = new ArrayList<>();
	private List<Category> categories = new ArrayList<>();
}
