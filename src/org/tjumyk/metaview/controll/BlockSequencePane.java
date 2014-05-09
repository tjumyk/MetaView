package org.tjumyk.metaview.controll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import org.tjumyk.metaview.model.Category;
import org.tjumyk.metaview.model.Group;
import org.tjumyk.metaview.model.MetaVideo;
import org.tjumyk.metaview.model.Segment;
import org.tjumyk.metaview.util.NodeStyleUtil;
import org.tjumyk.metaview.viewmodel.PlayerModel;

public class BlockSequencePane extends GridPane {
	private static final int ROW_BLOCKS = 12;
	private static final int COLUMN_BLOCKS = 25;
	private static final int TOTAL_BLOCKS = ROW_BLOCKS * COLUMN_BLOCKS;
	private Map<Object, List<Node>> nodeListMap = new HashMap<>();

	private PlayerModel model;
	private double framePerBlock;

	public BlockSequencePane() {
	}

	public BlockSequencePane(PlayerModel model) {
		build(model);
	}

	private void build(PlayerModel model) {
		this.model = model;
		MetaVideo video = this.model.getVideo();
		framePerBlock = video.getTotalFrames() / TOTAL_BLOCKS;

		for (int i = 0; i < ROW_BLOCKS; i++) {
			RowConstraints rc = new RowConstraints();
			rc.setPercentHeight(100.0 / ROW_BLOCKS);
			getRowConstraints().add(rc);
		}
		for (int i = 0; i < COLUMN_BLOCKS; i++) {
			ColumnConstraints cc = new ColumnConstraints();
			cc.setPercentWidth(100.0 / COLUMN_BLOCKS);
			getColumnConstraints().add(cc);
		}

		List<Node> blocks = new ArrayList<>();
		for (int i = 0, blockCount = 0; i < ROW_BLOCKS; i++) {
			for (int j = 0; j < COLUMN_BLOCKS; j++) {
				StackPane rect = new StackPane();
				rect.getStyleClass().add("block");
				StackPane rectWrapper = new StackPane(rect);
				rectWrapper.setPadding(new Insets(1.5));
				add(rectWrapper, j, i);
				blocks.add(rect);
				int frameStart = (int) (blockCount * framePerBlock + 1);
				Tooltip tip = new Tooltip("Frame:" + frameStart);
				Tooltip.install(rect, tip);
				blockCount++;
			}
		}

		for (Category cat : video.getCategories()) {
			for (Group group : cat.getGroups()) {
				List<Node> groupList = new ArrayList<>();
				for (Segment seg : group.getSegments()) {
					List<Node> list = new ArrayList<>();
					int from = (int) (seg.getFrom() / framePerBlock);
					int to = (int) (seg.getTo() / framePerBlock);
					if (to >= blocks.size())
						to = blocks.size() - 1;
					for (int i = from; i <= to; i++)
						list.add(blocks.get(i));
					nodeListMap.put(seg, list);
					groupList.addAll(list);
				}
				nodeListMap.put(group, groupList);
			}
		}

		NodeStyleUtil.bindBlockListStyle(model, nodeListMap);
		model.getCurrentTime().addListener(new ChangeListener<Duration>() {
			public void changed(ObservableValue<? extends Duration> observable,
					Duration oldValue, Duration newValue) {
				if (oldValue != null) {
					int blockIndex = (int) (oldValue.toSeconds()
							* model.getVideo().getFps() / framePerBlock);
					if (blockIndex >= blocks.size())
						blockIndex = blocks.size() - 1;
					blocks.get(blockIndex).getStyleClass().remove("playing");
				}
				if (newValue != null) {
					int blockIndex = (int) (newValue.toSeconds()
							* model.getVideo().getFps() / framePerBlock);
					if (blockIndex >= blocks.size())
						blockIndex = blocks.size() - 1;
					blocks.get(blockIndex).getStyleClass().add("playing");
				}
			};
		});
		model.getPlayingSegment().addListener((b, o, n) -> {
			if (n == null && o != null) {
				List<Node> list = nodeListMap.get(o);
				Node node = list.get(list.size() - 1);
				node.getStyleClass().remove("playing");
			}
		});
	}
}
