package norkts.spider;

import org.jsoup.nodes.Document;

public interface Processor {
	public void process(String url, Document doc);
	public byte[] getContent(String url);
}
