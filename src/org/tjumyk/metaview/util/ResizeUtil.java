package org.tjumyk.metaview.util;

import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;

/**
 * Utility class for resizing windows.
 * 
 * @author 宇锴
 */
public class ResizeUtil {
	private static double drag_x, drag_y, init_x, init_y;

	public static void setResizeHandler(Window win, Node resizeHandler) {
		resizeHandler.setOnMousePressed(e -> {
			drag_x = e.getScreenX();
			drag_y = e.getScreenY();
			init_x = win.getWidth();
			init_y = win.getHeight();
		});
		resizeHandler.setOnMouseDragged(e -> {
			double dx = e.getScreenX() - drag_x;
			double dy = e.getScreenY() - drag_y;
			win.setWidth(init_x + dx);
			win.setHeight(init_y + dy);
			e.consume();
		});
	}

	public static void setResizeBorders(Window win, List<Node> list) {
		EventHandler<? super MouseEvent> initHandler = e -> {
			drag_x = e.getScreenX();
			drag_y = e.getScreenY();
			init_x = win.getWidth();
			init_y = win.getHeight();
		};
		for (Node node : list) {
			node.setOnMousePressed(initHandler);
		}
		list.get(0).setOnMouseDragged(e -> {
			double dx = e.getScreenX() - drag_x;
			double dy = e.getScreenY() - drag_y;
			win.setWidth(init_x - dx);
			win.setHeight(init_y - dy);
			e.consume();
		});
		list.get(1).setOnMouseDragged(e -> {
			double dy = e.getScreenY() - drag_y;
			win.setHeight(init_y - dy);
			e.consume();
		});
		list.get(2).setOnMouseDragged(e -> {
			double dx = e.getScreenX() - drag_x;
			double dy = e.getScreenY() - drag_y;
			win.setWidth(init_x + dx);
			win.setHeight(init_y - dy);
			e.consume();
		});
		list.get(3).setOnMouseDragged(e -> {
			double dx = e.getScreenX() - drag_x;
			win.setWidth(init_x + dx);
			e.consume();
		});
		list.get(4).setOnMouseDragged(e -> {
			double dx = e.getScreenX() - drag_x;
			double dy = e.getScreenY() - drag_y;
			win.setWidth(init_x + dx);
			win.setHeight(init_y + dy);
			e.consume();
		});
		list.get(5).setOnMouseDragged(e -> {
			double dy = e.getScreenY() - drag_y;
			win.setHeight(init_y + dy);
			e.consume();
		});
		list.get(6).setOnMouseDragged(e -> {
			double dx = e.getScreenX() - drag_x;
			double dy = e.getScreenY() - drag_y;
			win.setWidth(init_x - dx);
			win.setHeight(init_y + dy);
			e.consume();
		});
		list.get(7).setOnMouseDragged(e -> {
			double dx = e.getScreenX() - drag_x;
			win.setWidth(init_x - dx);
			e.consume();
		});
	}
}
