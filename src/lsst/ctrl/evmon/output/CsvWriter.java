package lsst.ctrl.evmon.output;

import java.net.URI;

import lsst.ctrl.evmon.Template;
import lsst.ctrl.evmon.TemplateEntry;
import lsst.ctrl.evmon.engine.EventStore;


/**
 * Class CsvWriter write templates to standard output in CSV format, with tabs
 * as the delimiter.
 *
 */
public class CsvWriter implements MessageWriter, StringWriter {

    /**
     * Class constructor
     */
	public CsvWriter() {
	}
	
    /**
     * @deprecated
     */
    // TODO: re-work the API, since this class doesn't use it.  There's a
    // better way to specify the class hierarchy to take this into account.
	public void openConnection(URI uri) {
	}
	
    /**
     * Write a Template to standard output
     * @param es the EventStore to use to look up values 
     * @param template the Template to resolve and output
     */
	public void send(EventStore es, Template template) {

		String retVal = "";
		for (int i = 0; i < template.size(); i++) {

			TemplateEntry entry = (TemplateEntry) template.get(i);
			
			if (i != 0)
				retVal += "\t";

			// retVal += entry.getTypeString() + " " + entry.getKey() + " = ";
			String value = (String) entry.getValue();
			if (value.charAt(0) == '$') {
				String val = null;

				val = (String)es.lookup(value);
				retVal += val;


			} else {
				retVal += value;
			}
		}
		System.out.println(retVal);
	}

    /**
     * Write a string to standard output.   Currently, "es" is completely
     * ignored.
     * @param es the EventStore to use to look up values 
     * @param str the string to resolve and output
     */
    // TODO: resolve any $vars in the string before writing
	public void send(EventStore es, String str) {
		System.out.println(str);
	}
	
}
