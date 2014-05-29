package org.tjumyk.metaview.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The model class of the categories.
 * 
 * @author 宇锴
 */
public class Category {
	private String name, info;

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

	public List<Group> getGroups() {
		return groups;
	}

	private List<Group> groups = new ArrayList<>();
}