// ======================================================
// Matthew Michaud (matthew.michaud@alumni.ucalgary.ca)
// XMLSerialize.java
// ======================================================

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;


/**
 * Class for Java object serialization and deserialization to and from the XML
 * format.
 */
class XMLSerialize extends Serialize {
	/**
	 * Serializes the passed object and returns it as an XML encoded
	 * {@code string}.
	 * 
	 * @param source The object to be serialized.
	 * @return The XML encoded serialization of the passed object.
	 * @throws Exception If the object cannot be serialized.
	 */
	public String serializeObject(Object source) throws Exception {
		// Stores the hash code values for objects arelady serialized.
		TreeSet<Integer> seen = new TreeSet<Integer>();
		// Stores objects encountered and yet to be serialized.
		LinkedList<Object> todo = new LinkedList<Object>();
		// An XML document to store all serialized objects and its root element.
		Document document = new Document();
		Element rootElem = new Element("objects");
		document.setRootElement(rootElem);
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
			rootElem.addContent(encodeObject(todoObj, todo));
		}
		
		// Output the XML string.
		XMLOutputter outer = new XMLOutputter();
		Format fmt = outer.getFormat();
		fmt.setTextMode(Format.TextMode.TRIM_FULL_WHITE);
		fmt.setIndent("\t");
		fmt.setLineSeparator("\n");
		return outer.outputString(document);
	}


	/**
	 * Serializes objects as XML elements.
	 * 
	 * @param source The object to be serialized.
	 * @param todo A stack containing references to objects that need to be
	 * serialized.
	 * @return The XML element containing the serialization of {@code source}.
	 * @throws Exception If there is an issue accessing the values of the object
	 * being serialized.
	 */
	protected static Element encodeObject(
	Object source, LinkedList<Object> todo)	throws Exception {
		// The class of the object being serialized.
		Class<?> c = source.getClass();
		// The XML element to represent the object being serialized.
		Element xmlObj = new Element(c.isArray() ? "array" : "object");

		// Store the basic parameters of the object.
		xmlObj.setAttribute("class", c.getName());
		xmlObj.setAttribute("id", Integer.toString(source.hashCode()));

		if (c.isArray()) {
			// Grab and store the length of the array.
			int arrayLen = Array.getLength(source);
			xmlObj.setAttribute("length", Integer.toString(arrayLen));
			// Grab the array's component type.
			Class<?> componentType = source.getClass().getComponentType();
			// Store the value of each array element:
			for (int i = 0; i < arrayLen; i ++) {
				Element xmlVal = new Element("element");
				Object value = Array.get(source, i);
				encodeValue(value, xmlVal, componentType.isPrimitive());
				xmlObj.addContent(xmlVal);
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
				Element fieldXML = new Element("field");
				fieldXML.setAttribute("name", f.getName());
				fieldXML.setAttribute(
					"declaring_class", f.getDeclaringClass().getName());
				f.setAccessible(true);
				Object value = f.get(source);
				encodeValue(value, fieldXML, f.getType().isPrimitive());
				xmlObj.addContent(fieldXML);
				// "Recurse" over non-primitive array entries.
				if (!f.getType().isPrimitive() && value != null)
					todo.push(value);
			}
		}

		return xmlObj;
	}


	/**
	 * Encodes a passed object as a value for XML serialization.
	 * 
	 * @param value The object to be encoded.
	 * @param target The XML element in which to store the object.
	 * @param isPrimitive Indicates if the object to encode is of a primitive
	 * type.
	 */
	protected static void encodeValue(
	Object value, Element target, boolean isPrimitive) {
		if (isPrimitive)
			target.setText(value.toString());
		else if (value == null)
			target.setText("null");
		else
			target.setText(Integer.toString(value.hashCode()));
	}


	/**
	 * Deserializes the object stored in the passed XML encoded {@code String}.
	 * 
	 * @param source An XML encoded string to be deserialized.
	 * @return The object expressed by the passed XML encoded string.
	 * @throws Exception If the string cannot be deserialized.
	 */
	public Object deserializeObject(String source) throws Exception {
		// Maps the hash codes of objects to their new instances.
		TreeMap<Integer, Object> objects = new TreeMap<Integer, Object>();
		// Create a XML document from the passed XML encoded string.
		InputStream stream = new ByteArrayInputStream(source.getBytes("UTF-8"));
		Document document = new SAXBuilder().build(stream);
		// Grab a list of all objects to be decoded.
		List<Element> objList = document.getRootElement().getChildren();
		// Grab the hash code of the base object (as it is decoded first).
		int baseId = objList.get(0).getAttribute("id").getIntValue();
		
		// Create an instance for each object to be deserialized.
		for (int i = 0; i < objList.size(); i ++) {
			Element xmlObj = objList.get(i);
			int id = xmlObj.getAttribute("id").getIntValue();
			Class<?> c = Class.forName(xmlObj.getAttributeValue("class"));
			Object o = null;
			if (c.isArray())
				o = Array.newInstance(c.getComponentType(),
					xmlObj.getAttribute("length").getIntValue());
			else
				o = instantiateObject(c);

			objects.put(id, o);
		}

		//Decode the objects in reverse order (to build up dependency trees).
		for (int i = objList.size() - 1; i >= 0; i --)
			decodeObject(objList.get(i), objects);
		
		return objects.get(baseId);
	}


	/**
	 * Deserializes XML elements and returns the encoded objects within.
	 * 
	 * @param xmlObj The XML element encoding the serialized object.
	 * @param objects A map of hash codes to the instances that will store the
	 * deserialized objects.
	 * @throws Exception If there is an issue instantiating the object
	 * reflectively.
	 */
	protected static void decodeObject(
	Element xmlObj, TreeMap<Integer, Object> objects) throws Exception {
		// Grab the Class for the object to be decoded.
		Class<?> c = Class.forName(xmlObj.getAttributeValue("class"));
		// Destination for the object and XML elements for fields/array values.
		Object out = objects.get(xmlObj.getAttribute("id").getIntValue());
		List<Element> entries = xmlObj.getChildren();

		// Deserialize object:
		if (xmlObj.getName().compareTo("object") == 0) {
			// Grab the of Fields from the class.
			TreeMap<String, Field> fields = ReflectionHelper.getAllFields(c);

			// Grab and set values for each field:
			for (int i = 0; i < entries.size(); i ++) {
				// Grab the XML element for the field and grab parameters.
				Element fieldObj = entries.get(i);
				String fieldName = fieldObj.getAttributeValue("name");
				String declaringCLas
					= fieldObj.getAttributeValue("declaring_class");

				// Set the value of the field:
				Field f = fields.get(fieldName + declaringCLas);
				f.setAccessible(true);
				String value = fieldObj.getText();
				if (f.getType().isPrimitive())
					f.set(out, extractPrimitive(f.getType(), value));
				else if (value.compareTo("null") == 0)
					f.set(out, null);
				else
					f.set(out, objects.get(Integer.valueOf(value)));
			}

		// Deserialize arrays:
		} else {
			// Grab the length of the array.
			int arrayLen = xmlObj.getAttribute("length").getIntValue();

			// Set the value of each element of the array.
			for (int i = 0; i < arrayLen; i ++) {
				Element entryObj = entries.get(i);
				String value = entryObj.getText();
				if (c.getComponentType().isPrimitive())
					Array.set(
						out, i, extractPrimitive(c.getComponentType(), value));
				else if (value.compareTo("null") == 0)
					Array.set(out, i, null);
				else
					Array.set(out, i, objects.get(Integer.valueOf(value)));
			}
		}
	}
}