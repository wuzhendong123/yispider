package org.yi.spider.threadpool;

public class MyThread extends Thread {
	
	public void run() {

		System.out.println(Thread.currentThread().getName() + "正在执行。。。");

	}
}
