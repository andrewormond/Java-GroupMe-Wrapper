package github.adeo88.groupme.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

public class GroupMeAPI {

	private String token;
	private final String USER_AGENT = "Mozilla/5.0";
	private final Pattern JSONPattern = Pattern.compile("(\\{.*\\})");
	public boolean debugEnabled = false;

	public void println(Object obj) {
		if (this.debugEnabled) {
			System.out.println(obj);
		}
	}

	public void printEx(Exception e) {
		if (this.debugEnabled) {
			e.printStackTrace();
		}
	}

	public void setToken(String token) {
		this.token = token;
		println("Set token to: " + token);
	}

	public GroupMeAPI(String token) {
		setToken(token);
	}

	@Override
	public String toString() {
		return "GroupMeAPI [token=" + token + "]";
	}

	public JSONObject sendGetRequest(String url, boolean authenticate) throws GroupMeException {
		url = "https://api.groupme.com/v3"+url;
		int responseCode = -1;
		if (authenticate) {
			url += "?token=" + token;
		}
		try {
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			// add request header
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);

			responseCode = con.getResponseCode();
			println("Sent 'GET' request to URL : " + url + " with response code: " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			Matcher mtch = JSONPattern.matcher(response);
			if (!mtch.find()) {
				throw new GroupMeException("Could not find json");
			}
			JSONObject jobj = new JSONObject(mtch.group(0));
			return jobj;

		} catch (MalformedURLException e) {
			printEx(e);
		} catch (IOException e) {
			printEx(e);
		}

		throw new GroupMeException("Get Request Error: Code " + responseCode);
	}

	public int sendPostRequest(String url, String body, boolean authenticate) throws GroupMeException {
		int responseCode = -1;
		url = "https://api.groupme.com/v3" + url;
		if (authenticate) {
			url += "?token=" + token;
		}
		System.out.println("\nSending 'POST' request to URL : " + url);
		URL obj;
		try {
			obj = new URL(url);

			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			// add request header
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(body);
			System.out.println("Body: " + body);
			wr.flush();
			wr.close();

			responseCode = con.getResponseCode();
			return responseCode;
		} catch (IOException e) {
			printEx(e);
		}

		throw new GroupMeException("Post Request Error: Code " + responseCode);

	}
	
	static String ReadJSONStringWithNull(JSONObject json, String key) {
		if (json.has(key) && json.get(key) != org.json.JSONObject.NULL) {
			return json.getString(key);
		}
		return null;
	}
	

}
