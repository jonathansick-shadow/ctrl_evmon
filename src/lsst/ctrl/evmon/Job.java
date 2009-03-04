package lsst.ctrl.evmon;

import lsst.ctrl.evmon.input.MessageReader;



public class Job {
	MessageReader input = null;

	Chain chain = null;
	
	public Job(MessageReader input, Chain chain) {
		this.input = input;
		this.chain = chain;
	}
	
	public MessageReader getReader() {
		return input;
	}

	public Chain getChain() {
		return chain;
	}
	
	
	public String toString() {
		return "job input = "+input.toString();
	}
}