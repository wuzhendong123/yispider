package org.yi.spider.sys;

public class AAA {
	
	/**
	 * 
	 * <p>私有方法不能被继承</p>
	 */
	private void print1() {
		System.out.println("AAA----1------");
	}

	/**
	 * 
	 * <p>共有方法会被继承</p>
	 */
	public void print2() {
		System.out.println("AAA----2------");
	}
	
	public void fun() {
		System.out.println(this);
		//子类的该方法不会被继承， 打印的结果依旧是父类
		print1();
		print2();
	}
}
class BBB extends AAA {
	@SuppressWarnings("unused")
	private void print1() {
		System.out.println("BBB----1------");
	}

	public void print2() {
		System.out.println("BBB----2------");
	}
}