// ======================================================
// Matthew Michaud (matthew.michaud@alumni.ucalgary.ca)
// ReflectionHelper.java
// ======================================================

import java.lang.reflect.Field;
import java.util.TreeMap;

/**
 * Methods to simplify reflection.
 */
public class ReflectionHelper {
	/**
	 * Returns all the fields of a Class.
	 * 
	 * @param c The class to retrieve fields from.
	 * @return A mapping of the fields of class c such that the keys are
	 * determined by {@code f.getName() + f.getDeclaringClass().getName()} for
	 * the field {@code f}.
	 */
	public static TreeMap<String, Field> getAllFields(Class<?> c) {
		TreeMap<String, Field> set = new TreeMap<String, Field>();
		for (Field f : c.getFields())
			set.put(f.getName() + f.getDeclaringClass().getName(), f);
		for (Field f : c.getDeclaredFields())
			set.put(f.getName() + f.getDeclaringClass().getName(), f);
		return set;
	}
}
