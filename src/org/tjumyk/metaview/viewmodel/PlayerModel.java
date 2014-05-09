package org.tjumyk.metaview.viewmodel;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;

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
	private ObservableList<LogicUnit> logicUnitList = FXCollections
			.observableArrayList();

	public PlayerModel(MetaVideo video) {
		this.video = video;
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

	public ObservableList<LogicUnit> getLogicUnitList() {
		return logicUnitList;
	}
}
