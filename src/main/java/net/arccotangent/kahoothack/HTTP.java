package net.arccotangent.kahoothack;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

class HTTP {

	static String uagent = "Mozilla/5.0 (X11; Linux x86_64; rv:49.0) Gecko/20100101 Firefox/49.0";
	private static String ctype = "application/json;charset=UTF-8";

	static CloseableHttpClient getClient() {
		return HttpClients.createDefault();
	}

	static HttpGet GET(String url) {
		HttpGet req = new HttpGet(url);
		req.setHeader("User-Agent", uagent);
		//req.setHeader("Connection", conn);
		return req;
	}

	static HttpPost POST(String url, String rawdata) {
		HttpPost req = new HttpPost(url);
		HttpEntity e = new ByteArrayEntity(rawdata.getBytes());
		req.setHeader("User-Agent", uagent);
		//req.setHeader("Connection", conn);
		req.setHeader("Content-Type", ctype);
		req.setHeader("Origin", "https://kahoot.it");
		req.setHeader("Accept", "application/json, text/plain, */*");
		req.setHeader("Cookie", "eyJkZXZpY2VJZCI6ImI4ZmQzNGM4LWYwMGItNGM3MS1hZTZiLTMxOWUwNTQxMzhhYSIsInVzZXJJZCI6IjIyNGNhMWFmLTk5MjQtNDBkZS05NzRlLTdmMjJmOTExYTg0NSIsIm9wdE91dCI6ZmFsc2UsInNlc3Npb25JZCI6MTQ2MTU4ODExMzQ5NiwibGFzdEV2ZW50VGltZSI6MTQ2MTU4ODExMzQ5NiwiZXZlbnRJZCI6MTE2LCJpZGVudGlmeUlkIjoxMTIsInNlcXVlbmNlTnVtYmVyIjoyMjh9");
		//req.setHeader("accept-encoding", "identity");
		req.setEntity(e);
		return req;
	}

}