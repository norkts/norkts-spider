package norkts.spider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;


public class HttpRequest {
	static private String proxyHost = null;
	static private int proxyPort = 0;
	private Map<String, String> defaultHeader;
	
	public HttpRequest(String host, int port) {
		setHeader(null);
		setProxy(host, port);
	}
	
	public HttpRequest(){
		setHeader(null);
	}
	
	public void setHeader(Map<String, String> headers){
		if(headers == null){
			defaultHeader = new HashMap<String, String>();
			defaultHeader.put("Cache-Control", "max-age=0");
			defaultHeader.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.143 Safari/537.36");
			defaultHeader.put("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
			defaultHeader.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			defaultHeader.put("Content-Type", "application/x-www-form-urlencoded");			
		}else{
			defaultHeader = headers;
		}
			
	}
	
	public void setHeader(String key, String val){
		defaultHeader.put(key, val);
	}
	
	static public void setProxy(String host, int port){
		proxyHost = host;
		proxyPort = port;
	}	
	
	public HttpMethod get(Map<String, Object>params){
		Protocol myhttps;
		myhttps = new Protocol("https", new EasySSLProtocolSocketFactory(), 443);
		Protocol.registerProtocol("https", myhttps);
				
		HttpClient httpClient = new HttpClient();
		
		if(proxyHost != null){
			httpClient.getHostConfiguration().setProxy(proxyHost, proxyPort);
		}
		
		String url = (String)params.get("url");

		
		GetMethod getMethod = new GetMethod(url);
		getMethod.setFollowRedirects(false);
		
		Map<String, String> resHeaders = defaultHeader;
		
		if(params.containsKey("cookie")){
			resHeaders.put("Cookie", (String)params.get("cookie"));
		}
		
		HashMap<String, String> headers = null;
		
		if(!params.containsKey(("header"))){
			headers = new HashMap<String, String>();
		}else{
			headers = (HashMap<String, String>)params.get("header");
		}
		
		for(String key : headers.keySet()){
			resHeaders.put(key, headers.get(key));
		}
		
		for(String key : resHeaders.keySet()){
			getMethod.addRequestHeader(key, resHeaders.get(key));				
		}
        

		try {
			int status = httpClient.executeMethod(getMethod);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		httpClient.getState();

		return getMethod;
	}
	
	public HttpMethod post(Map<String, Object>params){
		Protocol myhttps;
		myhttps = new Protocol("https", new EasySSLProtocolSocketFactory(), 443);
		Protocol.registerProtocol("https", myhttps);
		
		HttpClient httpClient = new HttpClient();
        
		if(proxyHost != null){
			httpClient.getHostConfiguration().setProxy(proxyHost, proxyPort);
		}
		
		
		PostMethod postMethod = new PostMethod((String)params.get("url"));
		
		Map<String, String> resHeaders = defaultHeader;
		
		if(params.containsKey("cookie")){
			resHeaders.put("Cookie", (String)params.get("cookie"));
		}
		
		HashMap<String, String> headers = null;
		
		if(!params.containsKey(("header"))){
			headers = new HashMap<String, String>();
		}else{
			headers = (HashMap<String, String>)params.get("header");
		}
		
		for(String key : headers.keySet()){
			resHeaders.put(key, headers.get(key));
		}
		
		for(String key : resHeaders.keySet()){
			postMethod.addRequestHeader(key, resHeaders.get(key));				
		}
		

		postMethod.setRequestBody((String)params.get("data"));
		
		try {
			httpClient.executeMethod(postMethod);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return postMethod;		
	}
	
	private static class EasySSLSocketFactory extends SSLSocketFactory {  
        SSLContext sslContext = SSLContext.getInstance("TLS");  
  
        public EasySSLSocketFactory(KeyStore truststore)  
                throws NoSuchAlgorithmException, KeyManagementException,  
                KeyStoreException, UnrecoverableKeyException {  
            super();  
  
            TrustManager tm = new X509TrustManager() {  
                public void checkClientTrusted(X509Certificate[] chain, String authType)  
                        throws CertificateException {  
                }  
  
                public void checkServerTrusted(X509Certificate[] chain, String authType)  
                        throws CertificateException {  
                }  
  
                public X509Certificate[] getAcceptedIssuers() {  
                    return null;  
                }  
            };  
  
            sslContext.init(null, new TrustManager[] { tm }, null);  
        }  
  
        @Override  
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose)  
                throws IOException, UnknownHostException {  
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);  
        }  
  
        @Override  
        public Socket createSocket() throws IOException {  
            return sslContext.getSocketFactory().createSocket();  
        }

		@Override
		public String[] getDefaultCipherSuites() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String[] getSupportedCipherSuites() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Socket createSocket(String host, int port) throws IOException,
				UnknownHostException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Socket createSocket(InetAddress host, int port)
				throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Socket createSocket(String host, int port,
				InetAddress localHost, int localPort) throws IOException,
				UnknownHostException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Socket createSocket(InetAddress address, int port,
				InetAddress localAddress, int localPort) throws IOException {
			// TODO Auto-generated method stub
			return null;
		}


    }
	
	public HttpMethod get(String url){
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("url", url);
		return get(params);
	}
	
	public static String ugzip(HttpMethod method){
		
		if(!isGzip(method)){
			try {
				return method.getResponseBodyAsString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}
		}
		
		InputStream buff;
		try {
			buff = method.getResponseBodyAsStream();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "";
		}
		
        ByteArrayOutputStream os = new ByteArrayOutputStream(); 
        try {
            int count;    
            byte data[] = new byte[1024];
        	GZIPInputStream gis = new GZIPInputStream(buff);  
			while ((count = gis.read(data, 0, 1024)) != -1) {   
			    os.write(data, 0, count);    
			}
			gis.close();
			
			return os.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} 				
	}
	
	public static boolean isGzip(HttpMethod method){
		return "gzip".equals(method.getResponseHeader("Content-Encoding"));
	}
	
	public HttpMethod head(HashMap<String, Object> params){
		HeadMethod method = new HeadMethod((String)params.get("url"));
		
		Protocol myhttps;
		myhttps = new Protocol("https", new EasySSLProtocolSocketFactory(), 443);
		Protocol.registerProtocol("https", myhttps);
				
		HttpClient httpClient = new HttpClient();
		
		if(proxyHost != null){
			httpClient.getHostConfiguration().setProxy(proxyHost, proxyPort);
		}
		
		
		Map<String, String> resHeaders = defaultHeader;
		
		if(params.containsKey("cookie")){
			resHeaders.put("Cookie", (String)params.get("cookie"));
		}
		
		HashMap<String, String> headers = null;
		
		if(!params.containsKey(("header"))){
			headers = new HashMap<String, String>();
		}else{
			headers = (HashMap<String, String>)params.get("header");
		}
		
		for(String key : headers.keySet()){
			resHeaders.put(key, headers.get(key));
		}
		
		for(String key : resHeaders.keySet()){
			method.addRequestHeader(key, resHeaders.get(key));				
		}
		
		try {
			httpClient.executeMethod(method);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return method;
	}
	
	public HttpMethod head(String url){
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("url", url);
		
		return head(params);
	}
	
	public HttpMethod file(String url, long start, long end){
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("url", url);
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Range", "bytes=" + start + "-" + end);
		params.put("header", headers);
		
		HttpMethod method = get(params);
		
		return method;
		
	}
	
	public HttpMethod file(String url){
		return get(url);
	}
	
	public static class EasySSLProtocolSocketFactory implements ProtocolSocketFactory {  
		   
		  public Socket createSocket(String host, int port) throws IOException,   
		          UnknownHostException {   
		      return new Socket(host, port);
		  }   
		   
		   
		  public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort)   
		          throws IOException, UnknownHostException {   
		      return new Socket(host, port, clientHost, clientPort);   
		  }   
		  
		  public Socket createSocket(String host, int port, InetAddress localAddress,   
		          int localPort, HttpConnectionParams params) throws IOException,   
		          UnknownHostException, ConnectTimeoutException {   
		      if (params == null) {   
		          throw new IllegalArgumentException("Parameters may not be null");   
		      }   
		      int timeout = params.getConnectionTimeout();   

		      if (timeout == 0) {   
		          return new Socket(host, port, localAddress, localPort);   
		      } else {   
		          Socket socket = new Socket();   
		          SocketAddress localaddr = new InetSocketAddress(localAddress, localPort);   
		          SocketAddress remoteaddr = new InetSocketAddress(host, port);   
		          socket.bind(localaddr);   
		          socket.connect(remoteaddr, timeout);   
		          return socket;   
		      }   
		  }   
		   
		  //自定义私有类   
		  private static class TrustAnyTrustManager implements X509TrustManager {   
		      
		      public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {   
		      }   
		  
		      public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {   
		      }   
		  
		      public X509Certificate[] getAcceptedIssuers() {   
		          return new X509Certificate[]{};   
		      }   
		  }     
		  
		  
		}
}
