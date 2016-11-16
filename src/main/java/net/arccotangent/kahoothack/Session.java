package net.arccotangent.kahoothack;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONObject;

import java.io.IOException;

public class Session {
	
	private static boolean wasLastGameTeam = false;
	
	private static int challengeSolution = 0; //last challenge solution
	
	static boolean getLastGameTeam() {
		return wasLastGameTeam;
	}
	
	private static int solveChallenge(String challenge) {
		challenge = challenge.replace("  ", " ").replace(")", "").replace("(", "");
		String[] challengeArray = challenge.split(" ");
		
		int num1 = Integer.parseInt(challengeArray[0]);
		int num2 = Integer.parseInt(challengeArray[2]);
		int num3 = Integer.parseInt(challengeArray[4]);
		
		int solution;
		
		if (Kahoot.isDebug()) {
			for (int i = 0; i < challengeArray.length; i++) {
				System.out.println("challengeArray[" + i + "] = '" + challengeArray[i] + "'");
			}
		}
		
		if (challengeArray[1].equals("*"))
			solution = num1 * (num2 + num3);
		else
			solution = (num1 + num2) * num3;
		
		if (Kahoot.isDebug())
			System.out.println("CHALLENGE SOLUTION = " + solution);
		
		return solution;
	}
	
	/**
	 * Check if a game PIN is valid.
	 * @param gamepin The game PIN to check
	 * @return true if game PIN is valid, false if game PIN is invalid or an exception was thrown.
	 */
	static boolean checkPINValidity(int gamepin) {
		CloseableHttpClient cli = HTTP.getClient();
		HttpGet req = HTTP.GET("https://kahoot.it/reserve/session/" + gamepin + "/?" + System.currentTimeMillis());
		try {
			CloseableHttpResponse res = cli.execute(req);
			
			BasicResponseHandler handler = new BasicResponseHandler();
			int status = res.getStatusLine().getStatusCode();
			
			return (status == 200); //200 = OK, if game pin is invalid, a 404 Not Found will be returned
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Uses the last challenge solution to decode the session token.
	 * @param encoded The encoded session token
	 * @return The decoded, usable session token
	 */
	static String decodeSessionToken(String encoded) {
		byte[] rawToken = Base64.decodeBase64(encoded);
		byte[] challengeBytes = Integer.toString(challengeSolution).getBytes();
		
		for (int i = 0; i < rawToken.length; i++) {
			rawToken[i] ^= challengeBytes[i % challengeBytes.length];
		}
		
		return new String(rawToken);
	}
	
	/**
	 * Retrieve a session token.<br>
	 * Note that this function doesn't return the session token in a usable state.<br>
	 * The session token must be decoded using decodeSessionToken() before it can be used.
	 * @param gamepin The game PIN to retrieve a session token for
	 * @return The encoded session token
	 */
	static String getSessionToken(int gamepin) {
		CloseableHttpClient cli = HTTP.getClient();
		HttpGet req = HTTP.GET("https://kahoot.it/reserve/session/" + gamepin + "/?" + System.currentTimeMillis());
		try {
			CloseableHttpResponse res = cli.execute(req);
			Header[] h = res.getAllHeaders();
			for (int i = 0; i < h.length; i++) {
				if (h[i].getName().equalsIgnoreCase("x-kahoot-session-token")) {
					if (Kahoot.isDebug())
						System.out.println("SESSION = " + h[i].getValue());
					
					BasicResponseHandler handler = new BasicResponseHandler();
					String response = handler.handleResponse(res);
					
					if (Kahoot.isDebug())
						System.out.println("SESSION REQUEST RESPONSE BODY = " + response);
					wasLastGameTeam = response.toLowerCase().contains("team");
					
					if (response.toLowerCase().contains("challenge")) {
						JSONObject j = new JSONObject(response);
						String challenge = j.getString("challenge");
						challengeSolution = solveChallenge(challenge);
					}
					return h[i].getValue();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
