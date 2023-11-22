// ======================================================
// Matthew Michaud (matthew.michaud@alumni.ucalgary.ca)
// TestSerialize.java
// ======================================================

import org.junit.Test;
import static org.junit.Assert.*;


public class TestSerialize extends Serialize {
	// Stub methood to permit the extension of the class under test.
	@Override
	public String serializeObject(Object source) throws Exception {
		return null;
	}

	// Stub methood to permit the extension of the class under test.
	@Override
	public Object deserializeObject(String source) throws Exception {
		return null;
	}
	

	@Test
	public void testExtractPrimitiveBoolean() throws Exception {
		assertEquals(false, extractPrimitive(boolean.class, "false"));
	}

	@Test
	public void testExtractPrimitiveShort() throws Exception {
		assertEquals((short) 55, extractPrimitive(short.class, "55"));
	}

	@Test
	public void testExtractPrimitiveInteger() throws Exception {
		assertEquals((int) 1234, extractPrimitive(int.class, "1234"));
	}

	@Test
	public void testExtractPrimitiveLong() throws Exception {
		assertEquals((long) 767, extractPrimitive(long.class, "767"));
	}

	@Test
	public void testExtractPrimitiveFloat() throws Exception {
		assertEquals((float) 1.424, extractPrimitive(float.class, "1.424"));
	}

	@Test
	public void testExtractPrimitiveDouble() throws Exception {
		assertEquals((double) 565.879,
			extractPrimitive(double.class, "565.879"));
	}

	@Test
	public void testExtractPrimitiveByte() throws Exception {
		assertEquals((byte) 76, extractPrimitive(byte.class, "76"));
	}

	@Test
	public void testExtractPrimitiveChar() throws Exception {
		assertEquals('y', extractPrimitive(char.class, "y"));
	}

	@Test(expected = Exception.class)
	public void testExtractPrimitiveNonPrimitive() throws Exception {
		extractPrimitive(Object.class, "apple");
	}

	@Test(expected = NumberFormatException.class)
	public void testExtractPrimitiveNumberFormat() throws Exception {
		extractPrimitive(int.class, "apple");
	}
	
	@Test(expected = NumberFormatException.class)
	public void testExtractPrimitiveBoundsFault() throws Exception {
		extractPrimitive(byte.class, "256");
	}


	@Test
	public void testInstantiateObjectBoolean() throws Exception {
		assertEquals(false, instantiateObject(Boolean.class));
	}

	@Test
	public void testInstantiateObjectShort() throws Exception {
		assertEquals((short) 0, instantiateObject(Short.class));
	}
	
	@Test
	public void testInstantiateObjectInteger() throws Exception {
		assertEquals((int) 0, instantiateObject(Integer.class));
	}

	@Test
	public void testinstantiateObjectLong() throws Exception {
		assertEquals((long) 0, instantiateObject(Long.class));
	}

	@Test
	public void testInstantiateObjectFloat() throws Exception {
		assertEquals((float) 0.0, instantiateObject(Float.class));
	}

	@Test
	public void testInstantiateObjectDouble() throws Exception {
		assertEquals((double) 0.0, instantiateObject(Double.class));
	}

	@Test
	public void testInstantiateObjectByte() throws Exception {
		assertEquals((byte) 0, instantiateObject(Byte.class));
	}

	@Test
	public void testInstantiateObjectChar() throws Exception {
		assertEquals((char) 0, instantiateObject(Character.class));
	}

	@Test
	public void testInstantiateObjectNonPrimitive() throws Exception {
		assertNotNull(instantiateObject(Object.class));
	}
}
