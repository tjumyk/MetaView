package org.tjumyk.metaview.cellfactory;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.util.Callback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.tjumyk.metaview.controller.BrowserController;
import org.tjumyk.metaview.model.Group;

/**
 * List cell factory for ListView of "ZoomIn View"
 * 
 * @author 宇锴
 */
public class ZoomInListFactory implements
		Callback<ListView<Group>, ListCell<Group>> {

	/**
	 * List cell for items in "ZoomIn View"
	 * 
	 * @author 宇锴
	 */
	public class ZoomInListCell extends ListCell<Group> {

		@Override
		protected void updateItem(Group item, boolean empty) {
			super.updateItem(item, empty);
			if (item == null || empty)
				setGraphic(null);
			else {
				GridPane grid = new GridPane();
				grid.getStyleClass().add("zoom-in");
				grid.setHgap(5);
				grid.setVgap(5);
				grid.setPadding(new Insets(5));
				grid.setMaxHeight(100);

				RowConstraints rc1 = new RowConstraints();
				rc1.setVgrow(Priority.NEVER);
				RowConstraints rc2 = new RowConstraints();
				rc2.setValignment(VPos.TOP);
				rc2.setVgrow(Priority.ALWAYS);
				grid.getRowConstraints().addAll(rc1, rc2);

				ColumnConstraints cc1 = new ColumnConstraints();
				cc1.setMaxWidth(80);
				cc1.setHalignment(HPos.CENTER);
				ColumnConstraints cc2 = new ColumnConstraints();
				cc2.setMaxWidth(220);
				grid.getColumnConstraints().addAll(cc1, cc2);

				ImageView img = new ImageView(BrowserController.getInstance()
						.getFrameImageMap().get(item.getKey()));
				img.setFitWidth(80);
				img.setPreserveRatio(true);
				Label label_name = new Label("[" + item.getCategory().getName()
						+ "] " + item.getName());
				label_name.getStyleClass().add("name");
				Tooltip.install(label_name, new Tooltip(item.getName()));
				String info = item.getInfo();
				if (info.startsWith("http://") || info.startsWith("https://")) {
					info = "";
				}
				Document doc = Jsoup.parse(info);
				String text = doc.text();
				Label label_desc = new Label(text);
				label_desc.setWrapText(true);
				label_desc.getStyleClass().add("desc");
				grid.add(img, 0, 0, 1, 2);
				grid.add(label_name, 1, 0);
				grid.add(label_desc, 1, 1);
				setGraphic(grid);
			}
		}
	}

	@Override
	public ListCell<Group> call(ListView<Group> param) {
		return new ZoomInListCell();
	}
}
