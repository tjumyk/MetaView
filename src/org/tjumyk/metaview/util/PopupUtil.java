package org.tjumyk.metaview.util;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.stage.Window;

public class PopupUtil {
	public static void showPopupBelowNode(final Node node, final Popup popup) {
		final Window window = node.getScene().getWindow(); // 获得当前的stage
		double x = window.getX() + node.localToScene(0, 0).getX()
				+ node.getScene().getX();
		double y = window.getY() + node.localToScene(0, 0).getY()
				+ node.getScene().getY() + node.getBoundsInParent().getHeight();
		popup.show(window, x, y);
		if (!popup.getContent().isEmpty()) {
			final Node content = popup.getContent().get(0);
			x -= content.localToScene(0, 0).getX();
			y -= content.localToScene(0, 0).getY();
		}
		popup.show(window, x, y);
	}

	public static void showPopupWithinBounds(final Pane root, final Node node,
			final Popup popup) {
		final Window window = node.getScene().getWindow();
		double x = window.getX() + node.localToScene(0, 0).getX()
				+ node.getScene().getX();
		double y = window.getY() + node.localToScene(0, 0).getY()
				+ node.getScene().getY() + node.getBoundsInParent().getHeight();
		popup.show(window, x, y);
		if (!popup.getContent().isEmpty()) {
			final Node content = popup.getContent().get(0);
			x -= content.localToScene(0, 0).getX();
			y -= content.localToScene(0, 0).getY();
		}

		double Z = window.getX();
		double gX = root.getLayoutX();
		double c = root.getWidth() - popup.getWidth();

		popup.show(window, (Z + gX + c + 8), y);
	}
}
