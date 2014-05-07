package org.tjumyk.metaview.controll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ScrollEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import org.tjumyk.metaview.model.Category;
import org.tjumyk.metaview.model.Group;
import org.tjumyk.metaview.model.MetaVideo;
import org.tjumyk.metaview.model.Segment;

public class MetaRelationPane extends javafx.scene.Group {
	private MetaVideo video;
	private Map<Segment, Node> segmentNodes = new HashMap<>();
	private Map<Group, Node> groupNodes = new HashMap<>();

	private static final double NODE_SIZE = 12;
	private static final double CATEGORY_NODE_GAP = 3;

	public MetaRelationPane() {
	}

	public MetaRelationPane(MetaVideo video) {
		this.video = video;
		build();
	}

	private void build() {
		List<Category> cats = new ArrayList<>();
		for (Category cat : video.getCategories()) {
			cats.add(cat);
		}
		cats.sort((a, b) -> {
			return b.getGroups().size() - a.getGroups().size();
		});

		int cats1Size = 0, cats2Size = 0;
		List<Category> cats1 = new ArrayList<>();
		List<Category> cats2 = new ArrayList<>();

		for (Category cat : cats) {
			if (cats1Size <= cats2Size) {
				cats1.add(cat);
				if (cats1Size > 0)
					cats1Size += CATEGORY_NODE_GAP;
				cats1Size += cat.getGroups().size();
			} else {
				cats2.add(cat);
				if (cats2Size > 0)
					cats2Size += CATEGORY_NODE_GAP;
				cats2Size += cat.getGroups().size();
			}
		}

		javafx.scene.Group root = new javafx.scene.Group();
		
		for (Segment seg : video.getSegments()) {
			Rectangle rect = new Rectangle(NODE_SIZE * 0.8, NODE_SIZE * 0.8);
			segmentNodes.put(seg, rect);
			Tooltip tip = new Tooltip("Key:" + seg.getKey() + " From:"
					+ seg.getFrom() + " To:" + seg.getTo());
			Tooltip.install(rect, tip);
		}

		for (Category cat : video.getCategories()) {
			for (Group group : cat.getGroups()) {
				Circle circle = new Circle(NODE_SIZE / 2.0 * 0.8);
				circle.getStyleClass().add("group");
				groupNodes.put(group, circle);
				Tooltip tip = new Tooltip(group.getName());
				Tooltip.install(circle, tip);
			}
		}

		int width = segmentNodes.size();
		if (cats1Size > width) {
			width = cats1Size;
		}
		if (cats2Size > width) {
			width = cats2Size;
		}

		setGroupNodes(cats1, root, (width - cats1Size) / 2.0, 0);
		setGroupNodes(cats2, root, (width - cats2Size) / 2.0, 6);

		double offsetX = (width - segmentNodes.size()) / 2.0;
		int index = 0;
		for (Segment seg : video.getSegments()) {
			Node segNode = segmentNodes.get(seg);
			segNode.getStyleClass().add("segment");
			segNode.setLayoutX((offsetX + index) * NODE_SIZE);
			segNode.setLayoutY(NODE_SIZE * 3);
			root.getChildren().add(segNode);
			index++;
		}

		for (Category cat : video.getCategories()) {
			for (Group group : cat.getGroups()) {
				Node groupNode = groupNodes.get(group);
				Point2D groupPos = groupNode.localToParent(0, 0);
				for (Segment seg : group.getSegments()) {
					Node segNode = segmentNodes.get(seg);
					Point2D segPos = segNode.localToScene(0, 0);
					Line line = new Line();
					line.getStyleClass().add("line");
					line.setStartX(groupPos.getX());
					line.setStartY(groupPos.getY());
					line.setEndX(segPos.getX() + NODE_SIZE / 2.0);
					line.setEndY(segPos.getY() + NODE_SIZE / 2.0);
					root.getChildren().add(0,line);
				}
			}
		}

		getChildren().add(root);

		parentProperty().addListener(new ChangeListener<Parent>() {
			public void changed(
					javafx.beans.value.ObservableValue<? extends Parent> observable,
					Parent oldValue, Parent newValue) {
				if (newValue != null) {
					newValue.addEventHandler(ScrollEvent.SCROLL, e -> {
						double scale = root.getScaleX();
						if (e.getDeltaY() > 0) {
							scale += 0.1;
						} else {
							scale -= 0.1;
							if (scale < 0.1)
								scale = 0.1;
						}
						root.setScaleX(scale);
						root.setScaleY(scale);
						e.consume();
					});
				}
			};
		});
	}

	private void setGroupNodes(List<Category> cats, javafx.scene.Group root,
			double offsetX, double offsetY) {
		int index = 0;
		for (Category cat : cats) {
			for (Group group : cat.getGroups()) {
				Node groupNode = groupNodes.get(group);
				groupNode.setLayoutX((offsetX + index + 0.5) * NODE_SIZE);
				groupNode.setLayoutY((offsetY + 0.5) * NODE_SIZE);
				root.getChildren().add(groupNode);
				index++;
			}
			index += CATEGORY_NODE_GAP;
		}
	}

}
