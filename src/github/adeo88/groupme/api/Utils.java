package github.adeo88.groupme.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

public class Utils {

	public static final Pattern JSONPattern = Pattern.compile("(\\{.*\\})");

	private Utils() {
	}

	static String jsonReadString(JSONObject json, String key) {
		if (json.has(key) && json.get(key) != org.json.JSONObject.NULL) {
			return json.getString(key);
		}
		return null;
	}

	static JSONObject jsonReadJSOBObject(JSONObject json, String key) {
		if (json.has(key) && json.get(key) != org.json.JSONObject.NULL) {
			return json.getJSONObject(key);
		}
		return null;
	}

	static int responseToCode(JSONObject response) {
		JSONObject meta = Utils.jsonReadJSOBObject(response, "meta");
		if (meta == null) {
			return -1;
		} else {
			return meta.getInt("code");
		}
	}

	static String[] interpretStrings(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new String[0];
		}
		String[] result = new String[jsonArray.length()];
		for (int i = 0; i < jsonArray.length(); i++) {
			result[i] = jsonArray.getString(i);
		}
		return result;
	}

	public static void downloadImage(String image_url, String filename) throws IOException {
		URL url = new URL(image_url);
		Matcher m = Pattern.compile(".+\\.(\\w+)\\b").matcher(image_url);
		if (!m.matches()) {
			throw new IOException("Image extension not found");
		}
		String extension = m.group(1);
		InputStream is = url.openStream();
		OutputStream os = new FileOutputStream(filename + "." + extension);

		byte[] b = new byte[2048];
		int length;

		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}

		is.close();
		os.close();
	}

	public static String uploadImage(String filename) throws GroupMeException {
		int responseCode = -1;
		String url = "https://image.groupme.com/pictures";

		System.out.println("\nSending 'POST' request to URL : " + url);
		URL obj;
		try {
			obj = new URL(url);

			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			// add request header
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "image/jpeg");

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			FileInputStream fin = new FileInputStream(new File(filename));
			int b;
			int i = 0;
			while ((b = fin.read()) != -1) {
				wr.writeByte(b);
				i++;
			}
			fin.close();
			System.out.println("wrote " + i + " bytes");

			wr.flush();
			wr.close();

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

			JSONObject jobj = new JSONObject(response);
			System.out.println("response");
			return jobj.getJSONObject("payload").getString("url");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static final String USER_AGENT = "Mozilla/5.0";

	public static JSONObject sendGetRequest(String url, String body, HashMap<String, String> parameters)
			throws GroupMeException {
		try {
			int responseCode = -1;
			int i = 0;
			if (parameters != null) {
				for (String key : parameters.keySet()) {
					if (i++ == 0) {
						url += "?";
					} else {
						url += "&";
					}
					url += key + "=" + parameters.get(key);
				}
			}

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
			if (responseCode < 200 || responseCode >= 300) {
				throw new GroupMeException(
						"Get Request Error to " + url + "\n" + con.getResponseMessage() + ": Code " + responseCode,
						responseCode);
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			Matcher mtch = Utils.JSONPattern.matcher(response);
			if (!mtch.find()) {
				throw new GroupMeException("Could not find json", 404);
			}
			JSONObject jobj = new JSONObject(mtch.group(0));
			return jobj;
		} catch (IOException e) {
			throw new GroupMeException(e.getMessage(), 500);
		}

	}

	public static JSONArray sendPostRequestToArray(String url, HashMap<String, String> parameters, String body)
			throws GroupMeException {
		try {
			int responseCode = -1;
			int i = 0;
			if (parameters != null) {
				for (String key : parameters.keySet()) {
					if (i++ == 0) {
						url += "?";
					} else {
						url += "&";
					}
					url += key + "=" + parameters.get(key);
				}
			}

			URL obj = new URL(url);

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
			return new JSONArray(response.toString());
		} catch (IOException e) {
			e.printStackTrace();
			throw new GroupMeException(e.getMessage(), 500);
		}
	}

	public static JSONObject sendPostRequest(String url, HashMap<String, String> parameters, String body)
			throws GroupMeException {

		try {
			int responseCode = -1;
			int i = 0;
			if (parameters != null) {
				for (String key : parameters.keySet()) {
					if (i++ == 0) {
						url += "?";
					} else {
						url += "&";
					}
					url += key + "=" + parameters.get(key);
				}
			}

			URL obj = new URL(url);

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

			Matcher mtch = Utils.JSONPattern.matcher(response);
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
			e.printStackTrace();
			throw new GroupMeException(e.getMessage(), 500);
		}
	}
}
