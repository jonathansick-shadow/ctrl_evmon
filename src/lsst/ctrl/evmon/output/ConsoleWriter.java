package lsst.ctrl.evmon.output;

import java.net.URI;

import lsst.ctrl.evmon.Template;
import lsst.ctrl.evmon.TemplateEntry;
import lsst.ctrl.evmon.engine.EventStore;


public class ConsoleWriter implements MessageWriter, StringWriter {

	public ConsoleWriter() {
	}
	
	public void openConnection(URI uri) {
	}
	
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

	public void send(EventStore es, String str) {
		System.out.println(str);
	}
	
}
