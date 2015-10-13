package lips.aj.stackheap.exmp7;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Main {

	public static void main(String[] args) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException {
		Class<?> clazz = Class.forName("exmp7.A");
		Object inst = clazz.newInstance();
		Method m = clazz.getDeclaredMethods()[0];
		m.setAccessible(true);
		m.invoke(inst, 3, 5);	
		for (Field f : clazz.getDeclaredFields()){
			f.setAccessible(true);
			System.out.println(f.get(inst));
		}
	}

}
