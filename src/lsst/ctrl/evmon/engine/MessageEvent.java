package lsst.ctrl.evmon.engine;

import java.util.EventObject;


public class MessageEvent extends EventObject {
	MonitorMessage _message = null;
	
	public MessageEvent(Object source, MonitorMessage message) {
		super(source);
		_message = message;
	}
	
	public MonitorMessage getMessage() {
		return _message;
	}
	
	public String get(String name) {
		String value = null;

		
		Object fieldValue = _message.get(name);
		if (fieldValue == null) { // todo: should we really be setting defaults?
			value = "0";
		} else {
			value = fieldValue.toString();
		}
		return value;		

	}
}
