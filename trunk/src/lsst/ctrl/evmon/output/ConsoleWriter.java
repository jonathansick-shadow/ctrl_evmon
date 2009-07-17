package lsst.ctrl.evmon.output;

import java.net.URI;

import lsst.ctrl.evmon.Template;
import lsst.ctrl.evmon.TemplateEntry;
import lsst.ctrl.evmon.engine.EventStore;


/**
 * Class ConsoleWriter write templates to standard output.
 *
 */
public class ConsoleWriter implements MessageWriter, StringWriter {

    /**
     * Class constructor
     */
	public ConsoleWriter() {
	}
	
    /**
     * @deprecated
     */
    // TODO: re-work the API, since this class doesn't use it.  There's a
    // better way to specify the class heirarchy to take this into account.
	public void openConnection(URI uri) {
	}
	
    /**
     * Write a Template to standard output
     * @param es the EventStore to use to look up values 
     * @param template the Template to resolve and output
     */
	public void send(EventStore es, Template template) {
		System.out.println("Message is:");
		String retVal = "";
		for (int i = 0; i < template.size(); i++) {

			TemplateEntry entry = (TemplateEntry) template.get(i);

			retVal += entry.getTypeString() + " " + entry.getKey() + " = ";
			String value = (String) entry.getValue();
			if (value.charAt(0) == '$') {
				String val = null;

				val = (String)es.lookup(value);
				retVal += val;


			} else {
				retVal += value;
			}
			retVal += "\n";
		}
		System.out.println(retVal);
		System.out.println("------");
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
