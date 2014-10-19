package org.yi.spider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;

import org.apache.commons.configuration.ConfigurationException;
import org.yi.spider.constants.GlobalConfig;
import org.yi.spider.loader.InitCfgLoader;

public class StopApp {

	public static void main(String[] args) throws ConfigurationException {
		InitCfgLoader.loadCollectConfig();
		new StopApp().shutdown();;
	}
	
	public void shutdown() {
		try {
			int port = GlobalConfig.collect.getInt("stop_port", 10987);
			Socket socket = new Socket("127.0.0.1", port);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader line = new BufferedReader(new StringReader("shutdown"));
			out.println(line.readLine());
			out.flush();
			
	      	line.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
