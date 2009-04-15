package lsst.ctrl.evmon;

import java.util.Iterator;
import java.util.Set;

import lsst.ctrl.evmon.engine.EventStore;
import lsst.ctrl.evmon.engine.MonitorMessage;

public class ExclusionFilterTask implements Task {
	static String DEFAULT_SEP = ";";
	String sep = null;
	String result;
	AttributeSet attributeSet;

	public ExclusionFilterTask(String result, AttributeSet set) {
		this(result, set, DEFAULT_SEP);
	}

	public ExclusionFilterTask(String result, AttributeSet set, String sep) {
		this.result = result;
		this.attributeSet = set;
		this.sep = sep;
	}

	public void execute(EventStore es, MonitorMessage event) {
		Set<String> keys = event.keySet();

		String val = "";
		boolean flag = false;
		for (Iterator<String> keyIter = keys.iterator(); keyIter.hasNext();) {
			String key = keyIter.next();
			if (attributeSet.contains(key) == false) {
				if (flag == false) {
					
					Object obj = event.get(key);
					if (obj != null) {
						val = key+"="+obj.toString();
						flag = true;
					}
				} else {
					Object obj = event.get(key);
					if (obj != null)
						val = val + "; " + key+"="+obj.toString();
				}
			}
		}
		es.put(result, val);
	}
}