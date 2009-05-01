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
//		System.out.println("Normalizing s = "+s);
		String str = s;
		int len = str.length();
		while (str != null) {
            if (str.equals(""))
                break;
//			System.out.println("str = " + str);

			int firstIndex = str.indexOf(delimitor);
			String key = str.substring(0, firstIndex);

			int lastIndex = str.lastIndexOf(eos);
			String value = null;
//			System.out.println("lastIndex = "+lastIndex);
			if (lastIndex == -1) {
				lastIndex = str.length()-1;
				value = str.substring(firstIndex + 1);
			} else {
				value = str.substring(firstIndex + 1, lastIndex);
			}
			// System.out.println("key = " + key + ", value = " + value);
			key = key.trim();
			value = value.trim();
			msg.put(key, value);
			str = str.substring(lastIndex+1);
			if (str.equals(""))
				str = null;

		}
	}

	public String toString() {
		return key;
	}
}
