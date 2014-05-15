package org.tjumyk.metaview.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * 对话框控制器基类，所有对话框的控制器都必须继承此类，以便通过{@link #initData(Object)}传递数据参数
 * 
 * @author 宇锴
 */
public abstract class DialogControllerBase implements Initializable {

	/**
	 * 抽象函数，用于传入数据参数
	 * 
	 * @param data
	 *            传入的数据
	 */
	public abstract void initData(Object data);
	
	/**
	 * 显示对话框，带有动画
	 * 
	 * @param root
	 *            对话框场景中的根节点
	 */
	protected void showDialog(Node root) {
		ScaleTransition scale1 = new ScaleTransition(Duration.millis(400));
		ScaleTransition scale2 = new ScaleTransition(Duration.millis(100));
		scale1.setFromX(0.8);
		scale1.setFromY(0.8);
		scale1.setToX(1.05);
		scale1.setToY(1.05);
		scale2.setToX(1.0);
		scale2.setToY(1.0);
		SequentialTransition seq = new SequentialTransition(scale1, scale2);
		FadeTransition fade = new FadeTransition(Duration.millis(500));
		fade.setFromValue(0.0);
		fade.setToValue(1.0);
		ParallelTransition pt = new ParallelTransition(root, seq, fade);
		pt.play();
	}

	/**
	 * 关闭对话框，带有动画
	 * 
	 * @param root
	 *            对话框场景中的根节点
	 */
	protected void closeDialog(final Node root) {
		ScaleTransition scale1 = new ScaleTransition(Duration.millis(100));
		ScaleTransition scale2 = new ScaleTransition(Duration.millis(400));
		scale1.setFromX(1.0);
		scale1.setFromY(1.0);
		scale1.setToX(1.05);
		scale1.setToY(1.05);
		scale2.setToX(0.8);
		scale2.setToY(0.8);
		SequentialTransition seq = new SequentialTransition(scale1, scale2);
		FadeTransition fade = new FadeTransition(Duration.millis(500));
		fade.setFromValue(1.0);
		fade.setToValue(0.0);
		ParallelTransition pt = new ParallelTransition(root, seq, fade);
		pt.play();
		pt.setOnFinished(e -> {
			Stage stage = (Stage) root.getScene().getWindow();
			stage.close();
		});
		pt.play();
	}
}
