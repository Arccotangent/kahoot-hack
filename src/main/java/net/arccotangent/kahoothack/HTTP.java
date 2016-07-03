package net.arccotangent.kahoothack;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

class HTTP {

	static String uagent = "Mozilla/5.0 (Linux; Android 4.4.2; en-us; SAMSUNG SCH-I545 Build/KOT49H) AppleWebKit/537.36 (KHTML, like Gecko) Version/1.5 Chrome/28.0.1500.94 Mobile Safari/537.36";
	static String conn = "Keep-Alive";
	static String ctype = "application/json;charset=UTF-8";

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
		//req.setHeader("accept-encoding", "identity");
		req.setEntity(e);
		return req;
	}

}