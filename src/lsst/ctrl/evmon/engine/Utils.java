package lsst.ctrl.evmon.engine;

public class Utils {

	
	public static Object createObject(String pkg, String obj) throws Exception {
		// take the name of the field, and create an string with that name
		// capitalized in the first character, and lower case for the rest
		String fieldName = obj.toLowerCase();
		String objectName = fieldName.substring(0, 1).toUpperCase()
				+ fieldName.substring(1, fieldName.length());
		Class<?> newClass = Class.forName(pkg+ "." + objectName);
		Object newObject = newClass.newInstance();
		return newObject;
	}
	
	public static Object createObject(String obj) throws Exception {
		Class<?> newClass = Class.forName(obj);
		Object newObject = newClass.newInstance();
		return newObject;
	}
}
