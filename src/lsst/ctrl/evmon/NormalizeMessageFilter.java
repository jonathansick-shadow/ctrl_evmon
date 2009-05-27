package lsst.ctrl.evmon;

import lsst.ctrl.evmon.engine.EventStore;
import lsst.ctrl.evmon.engine.MonitorMessage;

public class NormalizeMessageFilter {
	String key = null;
	String delimitor = null;
	String eos = null;

    /**
     * Class constructor NormalizeMessageFilter is used by a MysqlReader to
     * split table entries into subcomponents.
     * Some of the messages sorted in the logging database contain "extra", or
     *  non-standard keywords which don't have entries in the database table.  
     * Rather than discard this information, it is stored grouping all those 
     * keywords into one entry.  There may be more than one keyword in the entry,
     * there may be no keywords. It depends on the message.
     *
     * This class takes entries with this information, and splits them back
     * into individual keywords as they were originally constructed, so they
     * can be used by the rest of the EventMonitor objects, and values can
     * be looked up and used.
     *
     * @param key the "special" database table entry name  to lookup and change back into component keywords.
     * @param delimitor the delimiter between the keys and values.
     * @param eos the "end of string" character sequence.
     */
	public NormalizeMessageFilter(String key, String delimitor, String eos) {
		this.key = key;
		this.delimitor = delimitor;
		this.eos = eos;
	}
	
    /**
     * Accessor method to retrieve this objects "key" string
     * @return the key String specified by this object
     */
	public String getKey() {
		return key;
	}

    /**
     * This method splits a string into key/value pairs and stores them into
     * a MonitorMessage.
     *
     * @param s the string to split into key/value pairs
     * @param msg the message which will hold the extracted key/values pairs
     */
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
