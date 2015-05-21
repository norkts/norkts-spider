package norkts.spider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import norkts.common.CacheUtil;
import norkts.common.CommonUtil;

public class ProcessThread extends Thread{
	
	//public static CacheClient cache = new CacheClient();
	public static Map<String, Object> cache = null;
	public static int activeThreadCount = 0;
	
	public static HttpRequest client = null;
	public static List<Processor> processors = new ArrayList<Processor>();
	public static UrlFilter filter = null;
	
	String url = "";
	String md5 = "";
	
	public boolean isProcessed = false;
	
	@SuppressWarnings("unchecked")
	public ProcessThread(String url){
		if(cache == null){
			
			Object memcach = CacheUtil.getInstance().get("url.cache");
			if(memcach == null){
				cache = new HashMap<String, Object>();
			}else{
				System.out.println("url.cache: get data from memcached");
				cache = (Map<String, Object>) memcach;
			}
			
		}
		
		this.url = url;
		this.md5 = CommonUtil.md5(this.url);
		
		if(client == null){
			setHttpClient(new HttpRequest());
		}
		
		activeThreadCount++;
		putUrl();
	}
	
	public static void setHttpClient(HttpRequest httpclient){
		client = httpclient;
	}
	
	public static void addProcessors(Processor p){
		processors.add(p);
	}
	
	public static void setUrlFilter(UrlFilter f){
		filter = f;
	}
	
	public void putUrl(){
		
		synchronized(cache){
			if(cache.containsKey(md5)){
				isProcessed = true;
			}else{
				
				Map<String,String> hashMap = new HashMap<String, String>();
				
				hashMap.put("url", url);
				hashMap.put("status", "0");
				hashMap.put("content", "0");
				
				cache.put(md5, hashMap);
			}		
		}
	}
		
	public void run(){
		
		if(!isProcessed && url.length() > 0){
			String html = "";
			
			@SuppressWarnings("unchecked")
			HashMap<String,Object> urlObject = (HashMap<String, Object>) cache.get(md5);
			
			HttpMethod method = client.get(url);
			
			try {
				html = method.getResponseBodyAsString();
			} catch (IOException e) {
				urlObject.put("status", "-1");
				e.printStackTrace();
			}

			Document doc = Jsoup.parse(html);
						
			//爬取URL

			if(filter == null){
				Elements elms = doc.getElementsByTag("a");
				for(Element elm : elms){
					String url = elm.attr("abs:href");					
					UrlList.addUrl(url);
				}
			}else{
				List<String> urls = filter.getUrls(doc);
				for(String url : urls){
					UrlList.addUrl(url);
				}
			}		

			urlObject.put("status", "1");
			try {
				urlObject.put("content", method.getResponseBody());
			} catch (IOException e) {
				urlObject.put("content", "".getBytes());
				e.printStackTrace();
			}
			cache.put(md5, urlObject);
			
			//调用处理程序
			for(Processor processor : processors){
				processor.process(url, doc);
			}
		}
				
		activeThreadCount--;
	}
}
