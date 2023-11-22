// ======================================================
// Matthew Michaud (matthew.michaud@alumni.ucalgary.ca)
// Serialize.java
// ======================================================


/**
 * Class for Java object serialization and deserialization to and
 * {@code String}.
 */
public abstract class Serialize {
	/**
	 * Serializes the passed object and returns it as an encoded {@code String}.
	 * 
	 * @param source The object to be serialized.
	 * @return The serialization of the passed object.
	 * @throws Exception If the object cannot be serialized.
	 */
	public abstract String serializeObject(Object source) throws Exception;


	/**
	 * Deserializes the object stored in the passed {@code String}.
	 * 
	 * @param source An string to be deserialized.
	 * @return The object expressed by the passed string.
	 * @throws Exception If the string cannot be deserialized.
	 */
	public abstract Object deserializeObject(String source) throws Exception;


	/**
	 * Parses the passed string value as the specified primitive type.
	 * 
	 * @param type The primitive type to be used to interpret the value.
	 * @param value The value to be parsed.
	 * @return An object of the type specified and of valued passed.
	 * @throws Exception If the passed type is not a primitive.
	 * @throws NumberFormatException If the string cannot be interpreted as the
	 * expected numeric type.
	 */
	protected static Object extractPrimitive(Class<?> type, String value)
	throws Exception {
		Object newValue = null;
		if (type == boolean.class)		newValue = Boolean.valueOf(value);
		else if (type == short.class)	newValue = Short.valueOf(value);
		else if (type == int.class)		newValue = Integer.valueOf(value);
		else if (type == long.class)	newValue = Long.valueOf(value);
		else if (type == float.class)	newValue = Float.valueOf(value);
		else if (type == double.class)	newValue = Double.valueOf(value);
		else if (type == byte.class)	newValue = Byte.valueOf(value);
		else if (type == char.class)	newValue = value.charAt(0);
		else throw new Exception("Invalid primitive type: " + type.getName());
		return newValue;
	}


	/**
	 * Returns an empty instance of the passed {@code Class}. The class must
	 * define a null (no-argument) constructor.
	 * 
	 * @param c A {@code Class} to be instantiated.
	 * @return An empty instance of the specified {@code Class}.
	 * @throws Exception If the class cannot be instantiated reflectively.
	 */
	protected static Object instantiateObject(Class<?> c)
	throws Exception {
		// Handle primitive wrappers as they don't have null constructors.
		if (c == Boolean.class)			return Boolean.valueOf(false);
		else if (c == Short.class)		return Short.valueOf((short) 0);
		else if (c == Integer.class)	return Integer.valueOf(0);
		else if (c == Long.class)		return Long.valueOf(0);
		else if (c == Float.class)		return Float.valueOf((float) 0.0);
		else if (c == Double.class)		return Double.valueOf(0.0);
		else if (c == Byte.class)		return Byte.valueOf((byte) 0);
		else if (c == Character.class)	return Character.valueOf((char) 0);
		// Otherwise, rely on the null constructors being present in the class.
		else
			return c.getDeclaredConstructor().newInstance();
	}
}
