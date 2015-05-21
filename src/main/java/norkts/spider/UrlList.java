package norkts.spider;

import java.util.ArrayList;
import java.util.List;

public class UrlList {
	private static List<String> urls = new ArrayList<String>();
	private static List<String> keys = new ArrayList<String>();
	
	/**
	 * 
	 * @param url
	 */
	public static void addUrl(String url){		
		synchronized(urls){
			urls.add(url);
		}
		
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getUrl(){
		
		String url = null;
		synchronized(urls){
			
			if(urls.size() > 0 ){
				url = urls.get(0);
				urls.remove(0);				
			}
		}
		
		return url;
	}
	
	public static void setKey(String key){
		keys.add(key);
	}
}
