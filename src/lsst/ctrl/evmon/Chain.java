package lsst.ctrl.evmon;

import java.util.Vector;

/**
 * The <code>Chain</code> class represents a list of Link objects that describe a
 * sequence of events to watch, and tasks to execute.
 * 
 * @author srp
 * 
 */
public class Chain {
	Vector<Link> links = new Vector<Link>();

	public Chain() {

	}

	public void addLink(Link link) {
		links.add(link);
	}

	public Link getLink(int index) {
		if ((links.size() - 1) >= index)
			return links.get(index);
		return null;
	}

	public int size() {
		return links.size();
	}

}