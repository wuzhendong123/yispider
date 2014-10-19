package org.yi.spider.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.yi.spider.constants.GlobalConfig;
import org.yi.spider.utils.StringUtils;

public class SignalListener extends Thread {
	
	private ServerSocket server;
	private Socket socket;
	private BufferedReader in;
	
	public void run() {
	
		try {
			int port = GlobalConfig.collect.getInt("stop_port", 10987);
			server = new ServerSocket(port);
			System.out.println("start listening ...");
			while (true) {
				socket = server.accept();
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String line = in.readLine();
				//客户端发过来消息不为空则将采集器结束标识置为true
				if(StringUtils.isNotBlank(line)) {
					GlobalConfig.SHUTDOWN = true;
				}
				in.close();
				socket.close();
			}
		} catch (IOException e) {
			try {
				server.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}
