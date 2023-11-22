// ======================================================
// Matthew Michaud (matthew.michaud@alumni.ucalgary.ca)
// Visualize.java
// ======================================================

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.lang.reflect.Array;


/**
 * Recursive reflective object visualizer. The {@code visualize()} method prints
 * a textual representation of the passed object.
 */
public class Visualizer {
    // Store the objects that have been output in a path through the object
    // graph.
    static protected LinkedList<Object> callStack = new LinkedList<Object>();


    /**
     * Returns a textual representation of the passed object, displaying its
     * name, hash code, length (if an array), and the values of all fields.
     * Referenced objects are expanded recursively in a similar manner.
     * 
     * @param obj The instance to be visualized.
     * @return A string that textually represents the passed object.
     * @throws Exception If the class cannot be inspected reflectively.
     */
    public static String visualize(Object obj) throws Exception {
        Class<?> c = obj.getClass();
        return visualizeClass(c, obj, 0);
    }


    /**
     * Prints out the name, methods, supertypes, and fields of the passed class.
     * 
     * @param c The class to be inspected.
     * @param obj The instance of the class being inspected.
     * @param depth The reflection recursion depth.
     * @throws Exception If the class cannot be inspected reflectively.
     * @return A string that textually described the passed class.
     */
    protected static String visualizeClass(Class<?> c, Object obj, int depth)
    throws Exception {
        // Prevent infinite recursion along this path.
        if (callStack.contains(obj)) return "";
        callStack.push(obj);

        // Set up the string that will store the indentation for this class.
        String indent = "\t".repeat(depth);
        // String builder to accumulate the output string.
        StringBuilder out = new StringBuilder();

        // Class/Array Name:
        out.append(String.format("%1$s%2$s:\n%1$s Class Name: %3$s\n",
            indent,
            c.isArray() ? "ARRAY" : "OBJECT",
            c.getName()
        ));

        // If the class is an array, output the relevant details.
        if (c.isArray())
            return out.append(visualizeArray(obj, depth, false)).toString();

        // If the class is not an array, output a representation of its fields.
        TreeMap<String, Field> fields = ReflectionHelper.getAllFields(c);
        out.append(String.format("%1$s Fields:%2$s\n",
            indent,
            fields.size() == 0 ? " NONE" : ""
        ));

        // For each field, output its name, type, and modifiers.
        for (Entry<String, Field> e : fields.entrySet()) {
            Field f = e.getValue();
            f.setAccessible(true);
            Object value = f.get(obj);

            out.append(String.format("%1$s  FIELD:\n"
                + "%1$s   Name: %2$s\n"
                + "%1$s   Type: %3$s\n",
                indent,
                f.getName(),
                f.getType().getName()
            ));

            // Output the value of the field for the passed object.
            out.append(String.format("%1$s   Value %2$s\n",
                indent,
                inspectValue(value)
            ));

            // Recurse if necessary.
            if (value != null && !testPrimitive(value.getClass()))
                out.append(visualizeClass(f.getType(), value, depth + 1));
        }

        return out.toString();
    }


    /**
     * Returns a textual representation of the component type, length,
     * modifiers, and values of arrays.
     * 
     * @param array The array object.
     * @param depth The reflection recursion depth.
     * @param isField Indicates whether the array is being printed in the
     * context of a field.
     * @throws Exception If the class cannot be inspected reflectively.
     * @return A string that textually described the passed array.
     */
    protected static String visualizeArray(
    Object array, int depth, boolean isField) throws Exception {
        // Grab the array class, indent string, and array length.
        Class<?> c = array.getClass();
        String indent = "\t".repeat(depth);
        int cLen = Array.getLength(array);
        // String builder to accumulate the output string.
        StringBuilder out = new StringBuilder();

        // Output the header indiciating the component type and array length.
        out.append(String.format(
            "%1$s%6$s Component Type: %2$s\n"
            + "%1$s%6$s Length: %3$d\n"
            + "%1$s%6$s Array Contents( %4$s ):%5$s\n",
            indent,
            c.getComponentType(),
            cLen,
            c.getName(),
            cLen == 0 ? " NONE" : "",
            isField ? "  " : ""
        ));

        // Output the value for each element of the array.
        for (int i = 0; i < cLen; i ++) {
            Object elem = Array.get(array, i);
            out.append(String.format("%1$s%4$s  [%2$d]: %3$s\n",
                indent,
                i,
                inspectValue(elem),
                isField ? "  " : ""
            ));
            // Recurse if the component type is non-primitive.
            if (elem != null && !testPrimitive(elem.getClass()))
                out.append(visualizeClass(elem.getClass(), elem, depth + 1));
        }

        return out.toString();
    }


    /**
     * Returns a string that textually describes a value.
     * 
     * @param value The object representing the value.
     * @return A string containing a textual representation of the value.
     */
    protected static String inspectValue(Object value) {
        if (value == null)
            return "(Ref): null";
        else if (!testPrimitive(value.getClass()))
            return String.format("(Ref): %1$s", value.hashCode());
        else
            return ": " + value.toString();
    }


    /**
     * Returns true if the passed class metaobject is a primitive wrapper type.
     * 
     * @param c The class metaobject to test.
     * @return True if the metaobject represents a primitive wrapper class.
     */
    protected static boolean testPrimitive(Class<?> c) {
        return c == Boolean.class
            || c == Byte.class
            || c == Character.class
            || c == Float.class
            || c == Integer.class
            || c == Long.class
            || c == Short.class
            || c == Double.class;
    }
}
