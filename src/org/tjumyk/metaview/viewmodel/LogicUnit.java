package org.tjumyk.metaview.viewmodel;

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
