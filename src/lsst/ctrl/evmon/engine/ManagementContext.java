package lsst.ctrl.evmon.engine;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class ManagementContext {

	public static void addMBean(Object obj, String id) {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		try {
			ObjectName name = new ObjectName(id);
			mbs.registerMBean(obj, name);
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
