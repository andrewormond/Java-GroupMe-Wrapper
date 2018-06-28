package github.adeo88.groupme.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
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

	public void printSep(String title, PrintStream ps) {
		if (this.debugEnabled) {
			int n = 300 - title.length();
			for (int i = 0; i < n / 2; i++) {
				ps.print('-');
			}

			ps.print(title);

			for (int i = 0; i < n / 2; i++) {
				ps.print('-');
			}
			ps.println();
		}
	}

	public void printSep(PrintStream ps) {
		printSep("", ps);
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
		return sendGetRequest(url, null, authenticate);
	}

	public JSONObject sendGetRequest(String url, String body, boolean authenticate) throws GroupMeException {
		url = "https://api.groupme.com/v3" + url;
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

			if (body != null) {
				// Send Get request w/ Body
				con.setRequestProperty("Content-Type", "application/json");
				con.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
				wr.writeBytes(body);
				wr.flush();
				wr.close();
			}

			responseCode = con.getResponseCode();
			this.printSep("Sent 'GET' request to URL : " + url + " with response code: " + responseCode, System.out);
			if (body != null) {
				this.println("Body: " + body);
			}

			if (responseCode < 200 || responseCode >= 300) {
				throw new GroupMeException("Get Request Error: Code " + responseCode, responseCode);
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			Matcher mtch = JSONPattern.matcher(response);
			if (!mtch.find()) {
				throw new GroupMeException("Could not find json", 404);
			}
			JSONObject jobj = new JSONObject(mtch.group(0));
			println(jobj);
			this.printSep(System.out);
			return jobj;

		} catch (MalformedURLException e) {
			printEx(e);
		} catch (IOException e) {
			printEx(e);
		}

		throw new GroupMeException("Get Request Error: Code " + responseCode, responseCode);
	}

	public JSONObject sendPostRequest(String url, String body, boolean authenticate) throws GroupMeException {
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

			if (body != null) {
				// Send post request
				con.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
				wr.writeBytes(body);
				wr.flush();
				wr.close();
				this.println("Body: " + body);
			}

			responseCode = con.getResponseCode();

			if (responseCode < 200 || responseCode >= 300) {
				throw new GroupMeException("Post Request Error: Code " + responseCode, responseCode);
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			Matcher mtch = JSONPattern.matcher(response);
			if (!mtch.find()) {
				if (responseCode >= 200 && responseCode < 300) {
					JSONObject out = new JSONObject();
					JSONObject meta = new JSONObject();
					meta.put("code", responseCode);
					out.put("meta", meta);
					meta.put("response", JSONObject.NULL);
					return out;
				}
				throw new GroupMeException("[" + responseCode + "] Could not find json in:" + response, responseCode);
			}
			JSONObject jobj = new JSONObject(mtch.group(0));

			return jobj;
		} catch (IOException e) {
			printEx(e);
		}

		throw new GroupMeException("Post Request Error: Code " + responseCode, responseCode);

	}

}
