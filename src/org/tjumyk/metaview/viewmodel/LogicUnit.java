package org.tjumyk.metaview.viewmodel;

/**
 * A logical unit containing the logic action type and the action object.
 * 
 * @author 宇锴
 *
 */
public class LogicUnit {
	private Object object;
	private LogicAction action;

	public LogicUnit(Object object, LogicAction action) {
		this.object = object;
		this.action = action;
	}

	public Object getObject() {
		return object;
	}

	public LogicAction getAction() {
		return action;
	}
}
