package org.tjumyk.metaview.viewmodel;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Duration;

import org.tjumyk.metaview.model.Category;
import org.tjumyk.metaview.model.Group;
import org.tjumyk.metaview.model.MetaVideo;
import org.tjumyk.metaview.model.Segment;

public class PlayerModel {
	private MetaVideo video;
	private ObservableList<Segment> segmentPlayList = FXCollections
			.observableArrayList();
	private Property<Segment> playingSegment = new SimpleObjectProperty<>();
	private Property<Object> activeNode = new SimpleObjectProperty<>();
	private Property<Object> hoverNode = new SimpleObjectProperty<>();
	private Property<Duration> currentTime = new SimpleObjectProperty<>();
	private Property<Duration> seekTime = new SimpleObjectProperty<>();
	private ObservableList<Group> relatedGroupsInPlayList = FXCollections
			.observableArrayList();

	public PlayerModel(MetaVideo video) {
		this.video = video;

		segmentPlayList.addListener(new ListChangeListener<Segment>() {
			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends Segment> c) {
				relatedGroupsInPlayList.clear();
				List<Group> list = new ArrayList<>();
				for (Segment seg : segmentPlayList) {
					for (Group group : getRelatedGroups(seg)) {
						if (!list.contains(group)) {
							list.add(group);
						}
					}
				}
				for (Category cat : video.getCategories()) {
					for (Group group : list) {
						if (group.getCategory() == cat)
							relatedGroupsInPlayList.add(group);
					}
				}
			}
		});
	}

	private List<Group> getRelatedGroups(Segment seg) {
		List<Group> list = new ArrayList<>();
		for (Category cat : video.getCategories()) {
			for (Group group : cat.getGroups()) {
				if (group.getSegments().contains(seg))
					list.add(group);
			}
		}
		return list;
	}

	public MetaVideo getVideo() {
		return video;
	}

	public ObservableList<Segment> getSegmentPlayList() {
		return segmentPlayList;
	}

	public Property<Segment> getPlayingSegment() {
		return playingSegment;
	}

	public Property<Object> getActiveNode() {
		return activeNode;
	}

	public Property<Object> getHoverNode() {
		return hoverNode;
	}

	public Property<Duration> getCurrentTime() {
		return currentTime;
	}

	public Property<Duration> getSeekTime() {
		return seekTime;
	}

	public void applyLogic(LogicUnit unit) {
		Object obj = unit.getObject();
		if (obj instanceof Segment) {
			Segment seg = (Segment) obj;
			switch (unit.getAction()) {
			case ADD:
				if (!segmentPlayList.contains(seg))
					segmentPlayList.add(seg);
				break;
			case MULTIPLY:
				segmentPlayList.retainAll(seg);
				break;
			case SUBTRACT:
				if (segmentPlayList.contains(seg))
					segmentPlayList.remove(seg);
				break;
			}
			getSegmentPlayList().add(seg);
		} else if (obj instanceof Group) {
			Group group = (Group) obj;
			switch (unit.getAction()) {
			case ADD:
				for (Segment seg : group.getSegments()) {
					if (!segmentPlayList.contains(seg))
						segmentPlayList.add(seg);
				}
				break;
			case MULTIPLY:
				segmentPlayList.retainAll(group.getSegments());
				break;
			case SUBTRACT:
				for (Segment seg : group.getSegments()) {
					if (segmentPlayList.contains(seg))
						segmentPlayList.remove(seg);
				}
				break;
			}
		}
	}

	public ObservableList<Group> getRelatedGroupsInPlayList() {
		return relatedGroupsInPlayList;
	}
}
