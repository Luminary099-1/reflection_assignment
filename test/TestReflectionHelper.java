// ======================================================
// Matthew Michaud (matthew.michaud@alumni.ucalgary.ca)
// TestReflectionHelper.java
// ======================================================

import java.lang.reflect.Field;
import java.util.TreeMap;

import javax.crypto.Cipher;

import org.junit.Test;
import static org.junit.Assert.*;
import samples.*;


public class TestReflectionHelper {
	@Test
	public void testGetFieldsString() {
		doTestComplete(String.class);
	}

	@Test
	public void testGetFieldsSimple() {
		doTestComplete(Simple.class);
	}

	@Test
	public void testGetFieldsCyclic() {
		doTestComplete(Cyclic.class);
	}

	@Test
	public void testGetFieldsNode() {
		doTestComplete(Node.class);
	}

	@Test
	public void testGetFieldsPrimitiveArray() {
		doTestComplete(PrimitiveArray.class);
	}

	@Test
	public void testGetFieldsObjectArray() {
		doTestComplete(ObjectArray.class);
	}

	@Test
	public void testGetFieldsArrayListContainer() {
		doTestComplete(ArrayListContainer.class);
	}

	@Test
	public void testGetFieldsCipher() {
		doTestComplete(Cipher.class);
	}


	private void doTestComplete(Class<?> c) {
		Field[] declared = c.getDeclaredFields();
		Field[] visible = c.getFields();
		TreeMap<String, Field> map
			= ReflectionHelper.getAllFields(c);
		
		doTestArray(declared, map);
		doTestArray(visible, map);
	}

	private void doTestArray(Field[] array, TreeMap<String, Field> map) {
		for (Field f : array) {
			String key = f.getName() + f.getDeclaringClass().getName();
			assertTrue(map.containsKey(key));
		}
	}
}
