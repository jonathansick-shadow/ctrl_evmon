package lsst.ctrl.evmon;

import lsst.ctrl.evmon.engine.EventStore;
import lsst.ctrl.evmon.engine.MonitorMessage;

public class NormalizeMessageFilter {
	String key = null;
	String delimitor = null;
	String eos = null;

	public NormalizeMessageFilter(String key, String delimitor, String eos) {
		this.key = key;
		this.delimitor = delimitor;
		this.eos = eos;
	}
	
	public String getKey() {
		return key;
	}

	public void normalize(MonitorMessage msg, String s) {
		String str = s;
		int len = str.length();
		while (str != null) {
			//System.out.println("s = " + s);

			int firstIndex = s.indexOf(delimitor);
			String key = s.substring(0, firstIndex);

			int lastIndex = s.lastIndexOf(eos);
			String value = s.substring(firstIndex + 2, lastIndex);

			// System.out.println("key = " + key + ", value = " + value);
			msg.put(key, value);
			str = s.substring(lastIndex+2);
			if (str.equals(""))
				str = null;
		}
	}

	public String toString() {
		return key;
	}
}