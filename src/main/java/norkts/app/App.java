package norkts.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import norkts.common.CommonUtil;
import norkts.spider.HttpRequest;
import norkts.spider.ProcessThread;
import norkts.spider.Processor;
import norkts.spider.TaskThread;
import norkts.spider.UrlFilter;

/**
 * Hello world!
 *
 */
public class App 
{
	public static void main(String[] args) {
		TaskThread task = new TaskThread("https://www.baidu.com/");
		
		HttpRequest.setProxy("127.0.0.1", 8888);
		
		HttpRequest httpclient = new HttpRequest();
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "http://tieba.baidu.com/p/2568448107");
		headers.put("Connection", "keep-alive");
		headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36");
		headers.put("Accept-Encoding", "gzip, deflate, sdch");
		headers.put("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
		
		httpclient.setHeader(headers);
		UrlFilter filter = new UrlFilter(){

			public List<String> getUrls(Document doc) {
				List<String> urls = new ArrayList<String>();
				
				Elements elms = doc.getElementsByTag("a");
				for(Element elm : elms){
					String url = elm.attr("abs:href");					
					if(isMatch(url)){
						urls.add(url);
					}
				}
				
				return urls;
			}

			public boolean isMatch(String url) {
				
				if(url == null || url.trim().length() == 0){
					return false;
				}
				
				try {
					
					URI uri = new URI(url);
					if(uri.getHost().equals("news.baidu.com")){
						return true;
					}
					
				} catch (URISyntaxException e) {
					
					//e.printStackTrace();
				}
				return false;
			}
			
		};
		
		Processor processor = new Processor(){

			public void process(String url, Document doc) {
				byte[] bytes = getContent(url);
				String key = CommonUtil.md5(url);
							
				File file = new File(key + ".html");
				FileOutputStream fos = null;
				
				try {
					fos = new FileOutputStream(file);
					fos.write(bytes);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					if(fos != null){
						try {
							fos.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}

			public byte[] getContent(String url) {
				String key = CommonUtil.md5(url);
				if(ProcessThread.cache.containsKey(key)){
					Map<String, Object> data = (Map<String, Object>)ProcessThread.cache.get(key);
					return (byte[])data.get("content");
				}
				
				return null;
			}
			
		};
		
		ProcessThread.setUrlFilter(filter);
		ProcessThread.addProcessors(processor);
		ProcessThread.setHttpClient(httpclient);
		
		task.start();
		
		while(true){
			System.out.println(ProcessThread.activeThreadCount);
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(ProcessThread.activeThreadCount == 0){
				break;
			}
		}
		
		task.executor.shutdown();
		System.out.println("finished");		
	}
}
