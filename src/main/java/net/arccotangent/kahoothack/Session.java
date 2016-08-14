package net.arccotangent.kahoothack;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;

public class Session {

	public static String getSessionToken(int gamepin) {
		CloseableHttpClient cli = HTTP.getClient();
		HttpGet req = HTTP.GET("https://kahoot.it/reserve/session/" + gamepin);
		try {
			CloseableHttpResponse res = cli.execute(req);
			Header[] h = res.getAllHeaders();
			for (int i = 0; i < h.length; i++) {
				if (h[i].getName().equalsIgnoreCase("x-kahoot-session-token")) {
					if (Kahoot.isDebug())
						System.out.println("SESSION = " + h[i].getValue());
					return h[i].getValue();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}