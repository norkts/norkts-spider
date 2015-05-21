package norkts.spider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import norkts.common.CacheUtil;

public class TaskThread extends Thread{
	public static ExecutorService executor = Executors.newFixedThreadPool(50);
	
	public TaskThread(String url){
		executor.execute(new ProcessThread(url));
	}
	
	protected  void finalize(){
		System.out.println("url.cache: save to memcached!");
		CacheUtil.getInstance().set("url.cahche", 24*60*60, ProcessThread.cache);
	}
	
	public void run(){

		while(true){
			String url = null; 
			while((url = UrlList.getUrl()) != null){
				executor.execute(new ProcessThread(url));
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
