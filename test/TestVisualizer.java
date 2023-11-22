// ======================================================
// Matthew Michaud (matthew.michaud@alumni.ucalgary.ca)
// TestInspector.java
// ======================================================

import org.junit.Test;
import static org.junit.Assert.*;


public class TestVisualizer extends Visualizer {
	@Test
	public void testTestPrimitiveBoolean() {
		Boolean b = true;
		assertTrue(testPrimitive(b.getClass()));
	}

	@Test
	public void testTestPrimitiveDouble() {
		Integer i = 15;
		assertTrue(testPrimitive(i.getClass()));
	}

	@Test
	public void testTestPrimitiveNegative() {
		assertFalse(testPrimitive((new String()).getClass()));
	}


	@Test
	public void testInspectValueNull() {
		assertEquals("(Ref): null", inspectValue(null));
	}

	@Test
	public void testInspectValueObject() {
		Object o = new Object();
		String truth = "(Ref): " + o.hashCode();
		assertEquals(truth, inspectValue(o));
	}

	@Test
	public void testInspectValueInt() {
		assertEquals(": 15", inspectValue(15));
	}

	@Test
	public void testInspectValueBool() {
		assertEquals(": true", inspectValue(true));
	}

	@Test
	public void testInspectValueChar() {
		assertEquals(": y", inspectValue('y'));
	}
}
