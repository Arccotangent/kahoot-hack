/*
kahoot-hack - Reverse engineering kahoot.it
Copyright (C) 2016-2019 Arccotangent

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

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
		return req;
	}

	static HttpPost POST(String url, String rawdata) {
		HttpPost req = new HttpPost(url);
		HttpEntity e = new ByteArrayEntity(rawdata.getBytes());
		req.setHeader("User-Agent", uagent);
		req.setHeader("Content-Type", ctype);
		req.setHeader("Origin", "https://kahoot.it");
		req.setHeader("Accept", "application/json, text/plain, */*");
		//req.setHeader("accept-encoding", "identity");
		req.setEntity(e);
		return req;
	}

}