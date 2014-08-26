package org.yi.spider.reflect;

import java.lang.reflect.Field;

import org.junit.Test;

public class ReflectATest {

	@Test
	public void testReflect() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		ReflectA aObj = new ReflectA();
		Field aField = aObj.getClass().getDeclaredField("a");
		aField.setAccessible(true);
		System.out.println(aField.get(aObj)+","+aObj.getA());
		
		aField.set(aObj, 2);
		System.out.println(aField.get(aObj)+","+aObj.getA());
		
		Field bField = aObj.getClass().getDeclaredField("b");
		bField.setAccessible(true);
		System.out.println(bField.get(aObj)+","+aObj.getB());
		
		bField.set(aObj, 22);
		System.out.println(bField.get(aObj)+","+aObj.getB());
		
	}
	
}
