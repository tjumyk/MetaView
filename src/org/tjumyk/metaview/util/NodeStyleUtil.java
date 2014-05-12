package org.tjumyk.metaview.util;

import java.util.List;
import java.util.Map;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.shape.Line;

import org.tjumyk.metaview.model.Segment;
import org.tjumyk.metaview.viewmodel.PlayerModel;

public class NodeStyleUtil {
	public static void bindStyle(PlayerModel model, Map<Object, Node> nodeMap,
			Map<Object, List<Line>> lineMap) {
		model.getActiveNode().addListener((b, o, n) -> {
			if (o != null)
				changeStyleClass(nodeMap, lineMap, o, "active", false);
			if (n != null)
				changeStyleClass(nodeMap, lineMap, n, "active", true);
		});
		model.getHoverNode().addListener((b, o, n) -> {
			if (o != null)
				changeStyleClass(nodeMap, lineMap, o, "hover", false);
			if (n != null)
				changeStyleClass(nodeMap, lineMap, n, "hover", true);
		});
		model.getPlayingSegment().addListener((b, o, n) -> {
			if (o != null)
				nodeMap.get(o).getStyleClass().remove("playing");
			if (n != null)
				nodeMap.get(n).getStyleClass().add("playing");
		});
		model.getSegmentPlayList().addListener(
				new ListChangeListener<Segment>() {
					@Override
					public void onChanged(
							javafx.collections.ListChangeListener.Change<? extends Segment> c) {
						while (c.next()) {
							for (Segment seg : c.getAddedSubList()) {
								nodeMap.get(seg).getStyleClass()
										.add("in-playlist");
							}
							for (Segment seg : c.getRemoved()) {
								nodeMap.get(seg).getStyleClass()
										.remove("in-playlist");
							}
						}
					}
				});
	}

	private static void changeStyleClass(Map<Object, Node> nodeMap,
			Map<Object, List<Line>> lineMap, Object o, String className,
			boolean add) {
		if (add) {
			nodeMap.get(o).getStyleClass().add(className);
			if (lineMap != null)
				for (Line line : lineMap.get(o))
					line.getStyleClass().add(className);
		} else {
			nodeMap.get(o).getStyleClass().remove(className);
			if (lineMap != null)
				for (Line line : lineMap.get(o))
					line.getStyleClass().remove(className);
		}
//		if (o instanceof Group) {
//			Group group = (Group) o;
//			for (Segment seg : group.getSegments()) {
//				if (add)
//					nodeMap.get(seg).getStyleClass().add(className);
//				else
//					nodeMap.get(seg).getStyleClass().remove(className);
//			}
//		}
	}

	public static void bindBlockListStyle(PlayerModel model,
			Map<Object, List<Node>> nodeListMap) {
		model.getActiveNode().addListener((b, o, n) -> {
			if (o != null)
				for (Node node : nodeListMap.get(o))
					node.getStyleClass().remove("active");
			if (n != null)
				for (Node node : nodeListMap.get(n))
					node.getStyleClass().add("active");
		});
		model.getHoverNode().addListener((b, o, n) -> {
			if (o != null)
				for (Node node : nodeListMap.get(o))
					node.getStyleClass().remove("hover");
			if (n != null)
				for (Node node : nodeListMap.get(n))
					node.getStyleClass().add("hover");
		});
		model.getSegmentPlayList().addListener(
				new ListChangeListener<Segment>() {
					@Override
					public void onChanged(
							javafx.collections.ListChangeListener.Change<? extends Segment> c) {
						while (c.next()) {
							for (Segment seg : c.getAddedSubList()) {
								for (Node node : nodeListMap.get(seg))
									node.getStyleClass().add("in-playlist");
							}
							for (Segment seg : c.getRemoved()) {
								for (Node node : nodeListMap.get(seg))
									node.getStyleClass().remove("in-playlist");
							}
						}
					}
				});
	}

	public static void bindStyle(PlayerModel model, Map<Object, Node> nodeMap) {
		bindStyle(model, nodeMap, null);
	}
}
