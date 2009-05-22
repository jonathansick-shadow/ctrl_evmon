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

    /**
     * Class constructor
     */
	public Chain() {

	}

    /**
     * Appends a Link to this Chain
     *
     * @param link The Link which will be appended to this Chain
     */
	public void addLink(Link link) {
		links.add(link);
	}

    /**
     * Returns the Link (if any) at the given index
     *
     * @param index the index that we wish to retrieve
     * @return the link at the specified index
     */
	public Link getLink(int index) {
		if ((links.size() - 1) >= index)
			return links.get(index);
		return null;
	}

    /**
     * Returns the length of this Chain
     * @return the length of this Chain
     */
	public int size() {
		return links.size();
	}

}
