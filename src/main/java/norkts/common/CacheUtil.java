package norkts.common;

import java.io.IOException;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

public class CacheUtil {
	private static MemcachedClient client;

	public static synchronized MemcachedClient getInstance() {
		if (client == null) {
			try {
				client = new MemcachedClient(
						AddrUtil.getAddresses("127.0.0.1:11211"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return client;
	}
	
	/**
	 * 
	 * @author Administrator
	 *
	 */
	public static class CacheClient implements CacheClientInterface{
		
		private static MemcachedClient client = CacheUtil.getInstance();
		public static int expire = 30 * 24 * 60 * 60 * 1000;
		
		public Object get(String key) {
			return client.get(key);
		}

		public Object set(String key, Object obj) {
			return client.set(key, expire, obj);
		}
		
	}
	
	/**
	 * 
	 * @author Administrator
	 *
	 */
	public static interface CacheClientInterface{
		public Object get(String key);
		public Object set(String key, Object obj);
	}
}
