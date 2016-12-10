package net.arccotangent.kahoothack;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONObject;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;

public class Session {
	
	private static boolean wasLastGameTeam = false;
	
	private static String challengeSolution = ""; //last challenge solution
	
	static boolean getLastGameTeam() {
		return wasLastGameTeam;
	}
	
	/**
	 * Solve the new JS based challenges
	 * @param rawChallenge The raw challenge string returned from the GET request.
	 * @return The solved session token
	 */
	private static String solveJSChallenge(String rawChallenge) {
		JSONObject jsonChallenge = new JSONObject(rawChallenge);
		String challenge = jsonChallenge.getString("challenge");
		String[] challengeParts = challenge.split(";");
		
		if (Kahoot.isDebug()) {
			for (int i = 0; i < challengeParts.length; i++) {
				System.out.println("challengeParts[" + i + "] = " + challengeParts[i]);
			}
		}
		
		for (int i = 0; i < challengeParts.length; i++) {
			challengeParts[i] = challengeParts[i] + ";";
		}
		
		challengeParts[2] = "";
		challengeParts[3] = "return message.replace(/./g, function(char, position) {return String.fromCharCode((((char.charCodeAt(0) * position) + offset) % 77) + 48)";
		
		StringBuilder challengeBuilder = new StringBuilder();
		for (String challengePart : challengeParts) {
			challengeBuilder.append(challengePart);
			challengeBuilder.append("\n");
		}
		
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine jsEngine = manager.getEngineByName("JavaScript");
		
		try {
			return (String)jsEngine.eval(challengeBuilder.toString());
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	@Deprecated
	private static long solveRegexChallenge(String challenge) {
		challenge = challenge.replace("  ", " ");
		String[] challengeArray;
		
		long solution;
		
		//Numbers occur on each even index of the array such as 0, 2, 4, and so on
		//Operators occur on each odd index of the array such as 1, 3, 5, and so on
		
		if (Pattern.matches("^([0-9]*) \\* \\(([0-9]*) \\+ ([0-9]*)\\)$", challenge)) {
			challenge = challenge.replace("(", "").replace(")", "");
			challengeArray = challenge.split(" ");
			long num1 = Integer.parseInt(challengeArray[0]);
			long num2 = Integer.parseInt(challengeArray[2]);
			long num3 = Integer.parseInt(challengeArray[4]);
			
			solution = num1 * (num2 + num3);
		} else if (Pattern.matches("^\\(([0-9]*) \\+ ([0-9]*)\\) \\* ([0-9]*)$", challenge)) {
			challenge = challenge.replace("(", "").replace(")", "");
			challengeArray = challenge.split(" ");
			long num1 = Integer.parseInt(challengeArray[0]);
			long num2 = Integer.parseInt(challengeArray[2]);
			long num3 = Integer.parseInt(challengeArray[4]);
			
			solution = (num1 + num2) * num3;
		} else if (Pattern.matches("^([0-9]*) - \\(([0-9]*) \\* ([0-9]*)\\)$", challenge)) {
			challenge = challenge.replace("(", "").replace(")", "");
			challengeArray = challenge.split(" ");
			long num1 = Integer.parseInt(challengeArray[0]);
			long num2 = Integer.parseInt(challengeArray[2]);
			long num3 = Integer.parseInt(challengeArray[4]);
			
			solution = num1 - (num2 * num3);
		} else if (Pattern.matches("^\\(([0-9]*) \\+ ([0-9]*)\\) \\* \\(([0-9]*) \\* ([0-9]*)\\)$", challenge)) {
			challenge = challenge.replace("(", "").replace(")", "");
			challengeArray = challenge.split(" ");
			long num1 = Integer.parseInt(challengeArray[0]);
			long num2 = Integer.parseInt(challengeArray[2]);
			long num3 = Integer.parseInt(challengeArray[4]);
			long num4 = Integer.parseInt(challengeArray[6]);
			
			solution = (num1 + num2) * (num3 * num4);
		} else {
			challenge = challenge.replace("(", "").replace(")", "");
			challengeArray = challenge.split(" ");
			solution = -1;
			System.out.println("An unknown challenge was returned. Please report this to the developers.");
			for (int i = 0; i < challengeArray.length; i++) {
				System.out.println("challengeArray[" + i + "] = '" + challengeArray[i] + "'");
			}
		}
		
		if (Kahoot.isDebug()) {
			for (int i = 0; i < challengeArray.length; i++) {
				System.out.println("challengeArray[" + i + "] = '" + challengeArray[i] + "'");
			}
			System.out.println("CHALLENGE SOLUTION = " + solution);
		}
		
		return solution;
	}
	*/
	
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
			
			int status = res.getStatusLine().getStatusCode();
			
			return (status == 200); //200 = OK, 404 = Not found
			
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
		byte[] challengeBytes = challengeSolution.getBytes();
		
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
					
					challengeSolution = solveJSChallenge(response);
					if (Kahoot.isDebug()) {
						System.out.println("challengeSolution = " + challengeSolution);
					}
					/*
					if (response.toLowerCase().contains("challenge")) {
						JSONObject j = new JSONObject(response);
						String challenge = j.getString("challenge");
						challengeSolution = solveRegexChallenge(challenge);
					}
					*/
					return h[i].getValue();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
