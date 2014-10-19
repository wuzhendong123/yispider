package org.yi.spider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yi.spider.constants.GlobalConfig;

public class ThreadObserver extends Thread {
	
	private static final Logger logger = LoggerFactory.getLogger(ThreadObserver.class);
	
	private Thread thread;
	
	public ThreadObserver(Thread thread) {
		logger.debug("开始监控线程：" + thread.getName());
		this.thread = thread;
	}

	public void run() {
		while(true) {
			if(GlobalConfig.SHUTDOWN) {
				if(thread != null) {
					if(!thread.isInterrupted()) {
						logger.debug("stop! thread group:{}, thread name:{} ", thread.getThreadGroup().getName(), thread.getName());
						thread.interrupt();
					}
					break;
				}
			}
		}
	}
	
}
