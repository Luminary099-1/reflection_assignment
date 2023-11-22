// ======================================================
// Matthew Michaud (matthew.michaud@alumni.ucalgary.ca)
// JSONSerialize.java
// ======================================================

import org.json.*;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * Class for Java object serialization and deserialization to and from the JSON
 * format.
 */
class JSONSerialize extends Serialize {
	/**
	 * Serializes the passed object and returns it as a JSON encoded
	 * {@code string}.
	 * 
	 * @param source The object to be serialized.
	 * @return The JSON encoded serialization of the passed object.
	 * @throws Exception If the object cannot be serialized.
	 */
	public String serializeObject(Object source) throws Exception {
		// Stores the hash code values for objects arelady serialized.
		TreeSet<Integer> seen = new TreeSet<Integer>();
		// Stores objects encountered and yet to be serialized.
		LinkedList<Object> todo = new LinkedList<Object>();
		// A JSON array to store all serialized objects and its root object.
		JSONArray objArray = new JSONArray();
		JSONObject json = new JSONObject();
		json.put("objects", objArray);
		// Initialize the stack with the root object.
		todo.push(source);

		// Serialize all objects 'recursively".
		while (todo.size() > 0) {
			Object todoObj = todo.pop(); // Grab the next object to do.
			int objHash = todoObj.hashCode(); // Grab the object's ID.
			if (seen.contains(objHash)) // If already seen encoded, skip it.
				continue;
			// Otherwise, encode the object and add it to the XML structure.
			seen.add(objHash);
			objArray.put(encodeObject(todoObj, todo));
		}
		
		// Output the JSON string.
		return json.toString(4);
	}


	/**
	 * Serializes objects as JSON objects.
	 * 
	 * @param source The object to be serialized.
	 * @param todo A stack containing references to objects that need to be
	 * serialized.
	 * @return The JSON object containing the serialization of {@code source}.
	 * @throws Exception If there is an issue accessing the values of the object
	 * being serialized.
	 */
	protected static JSONObject encodeObject(
	Object source, LinkedList<Object> todo)	throws Exception {
		// The class of the object being serialized.
		Class<?> c = source.getClass();
		// The JSON object to represent the object being serialized.
		JSONObject jsonObj = new JSONObject();
		// A JSON array to store field or array values.
		JSONArray jsonEntries = new JSONArray();

		// Store the basic parameters of the object.
		jsonObj
			.put("class", c.getName())
			.put("id", source.hashCode())
			.put("type", c.isArray() ? "array" : "object")
			.put(c.isArray() ? "entries" : "fields", jsonEntries);

		if (c.isArray()) {
			// Grab and store the length of the array.
			int arrayLen = Array.getLength(source);
			jsonObj.put("length", arrayLen);
			// Grab the array's component type.
			Class<?> componentType = source.getClass().getComponentType();
			// Store the value of each array element:
			for (int i = 0; i < arrayLen; i ++) {
				JSONObject valObj = new JSONObject();
				Object value = Array.get(source, i);
				encodeValue(value, valObj, componentType.isPrimitive());
				jsonEntries.put(valObj);
				// "Recurse" over non-primitive array entries.
				if (!componentType.isPrimitive() && value != null)
					todo.push(value);
			}

		} else { // Encode fields:
			// Obtain a set of all fields (declared and inherited).
			TreeMap<String, Field> fields = ReflectionHelper.getAllFields(c);
			for (Field f : fields.values()) {
				// Ignore static fields. 	
				if (Modifier.isStatic(f.getModifiers())) continue;
				// Create the JSON object for the field. Store its parameters.
				JSONObject fObj = new JSONObject();
				fObj.put("name", f.getName())
					.put("declaring_class", f.getDeclaringClass().getName());
				f.setAccessible(true);
				Object value = f.get(source);
				encodeValue(value, fObj, f.getType().isPrimitive());
				jsonEntries.put(fObj);
				// "Recurse" over non-primitive array entries.
				if (!f.getType().isPrimitive() && value != null)
					todo.push(value);
			}
		}

		return jsonObj;
	}


	/**
	 * Encodes a passed object as a value for JSON serialization.
	 * 
	 * @param value The object to be encoded.
	 * @param target The JSON object in which to store the object.
	 * @param isPrimitive Indicates if the object to encode is of a primitive
	 * type.
	 */
	protected static void encodeValue(
	Object value, JSONObject target, boolean isPrimitive) {
		if (isPrimitive)
			target.put("value", value.toString());
		else if (value == null)
			target.put("reference", "null");
		else
			target.put("reference", Integer.toString(value.hashCode()));
	}


	/**
	 * Deserializes the object stored in the passed JSON encoded {@code String}.
	 * 
	 * @param source A JSON encoded string to be deserialized.
	 * @return The object expressed by the passed JSON encoded string.
	 * @throws Exception If the string cannot be deserialized.
	 */
	public Object deserializeObject(String source) throws Exception {
		// Maps the hash codes of objects to their new instances.
		TreeMap<Integer, Object> objects = new TreeMap<Integer, Object>();
		// Create a JSON object from the passed JSON encoded string.
		JSONObject json = new JSONObject(source);
		// Grab a list of all objects to be decoded.
		JSONArray objArray = json.getJSONArray("objects");
		// Grab the hash code of the base object (as it is decoded first).
		int baseId = objArray.getJSONObject(0).getInt("id");

		// Create an instance for each object to be deserialized.
		for (int i = 0; i < objArray.length(); i ++) {
			JSONObject jsonObj = objArray.getJSONObject(i);
			int id = jsonObj.getInt("id");
			Class<?> c = Class.forName(jsonObj.getString("class"));
			Object o = null;
			if (c.isArray())
				o = Array.newInstance(c.getComponentType(), jsonObj.getInt("length"));
			else
				o = instantiateObject(c);
			
			objects.put(id, o);
		}
		
		//Decode the objects in reverse order (to build up dependency trees).
		for (int i = objArray.length() - 1; i >= 0; i --)
			decodeObject(objArray.getJSONObject(i), objects);
		
		return objects.get(baseId);
	}


	/**
	 * Deserializes JSON objects and returns the encoded objects within.
	 * 
	 * @param jsonObj The JSON object encoding the serialized object.
	 * @param objects A map of hash codes to the instances that will store the
	 * deserialized objects.
	 * @throws Exception If there is an issue instantiating the object
	 * reflectively.
	 */
	protected static void decodeObject(
	JSONObject jsonObj, TreeMap<Integer, Object> objects) throws Exception {
		// Grab the Class for the object to be decoded.
		Class<?> c = Class.forName(jsonObj.getString("class"));
		// Destination for the object and a JSON array for fields/array values.
		Object out = objects.get(jsonObj.getInt("id"));
		JSONArray entries = null;

		// Deserialize object:
		if (jsonObj.getString("type").compareTo("object") == 0) {
			// Grab the of Fields and the JSON array of fields.
			TreeMap<String, Field> fields = ReflectionHelper.getAllFields(c);
			entries = jsonObj.getJSONArray("fields");

			// Grab and set values for each field:
			for (int i = 0; i < entries.length(); i ++) {
				// Grab the JSON object for the field and grab parameters.
				JSONObject fieldObj = entries.getJSONObject(i);
				String fieldName = fieldObj.getString("name");
				String declaringCLas = fieldObj.getString("declaring_class");

				// Set the value of the field:
				Field f = fields.get(fieldName + declaringCLas);
				f.setAccessible(true);
				if (fieldObj.has("value"))
					f.set(out, extractPrimitive(
						f.getType(), fieldObj.getString("value")));

				else {
					String ref = fieldObj.getString("reference");
					if (ref.compareTo("null") == 0)
						f.set(out, null);
					else
						f.set(out, objects.get(Integer.valueOf(ref)));
				}
			}

		// Deserialize arrays:
		} else {
			// Grab the array entries.
			entries = jsonObj.getJSONArray("entries");

			// Set the value of each element of the array.
			for (int i = 0; i < jsonObj.getInt("length"); i ++) {
				JSONObject entryObj = entries.getJSONObject(i);
				if (c.getComponentType().isPrimitive())
					Array.set(out, i, extractPrimitive(
						c.getComponentType(), entryObj.getString("value")));

				else {
					String ref = entryObj.getString("reference");
					if (ref.compareTo("null") == 0)
						Array.set(out, i, null);
					else
						Array.set(out, i, objects.get(Integer.valueOf(ref)));
				}
			}
		}
	}
}