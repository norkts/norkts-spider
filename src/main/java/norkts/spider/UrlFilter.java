package norkts.spider;

import java.util.List;

import org.jsoup.nodes.Document;

public interface UrlFilter {
	public List<String> getUrls(Document doc);
	public boolean isMatch(String url);
}
