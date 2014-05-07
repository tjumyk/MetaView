package org.tjumyk.metaview.model;

import java.util.ArrayList;
import java.util.List;

public class Group {
	private String name, info;
	private int key;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public List<Segment> getSegments() {
		return segments;
	}

	private List<Segment> segments = new ArrayList<>();
}