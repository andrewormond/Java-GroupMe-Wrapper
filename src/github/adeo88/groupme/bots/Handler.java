package github.adeo88.groupme.bots;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import github.adeo88.groupme.api.GroupMeAPI;
import github.adeo88.groupme.api.Message;
import github.adeo88.groupme.api.Utils;

/**
 * Internal Client Handler
 *
 * @author Andrew Ormond
 * @version 0.1
 * @since 12-26-2017
 */
public class Handler implements Runnable {

	private static int ID_CNT = 1;
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	public final int ID;
	private BotListener listener;
	
	public Handler(Socket socket, PrintWriter out, BufferedReader in, BotListener listener) {
		this.socket = socket;
		this.out = out;
		this.in = in;
		this.listener = listener;
		ID = ID_CNT++;
	}

	public void interpret(String info) {
		Matcher mtch = Utils.JSONPattern.matcher(info);
		if (mtch.find()) {
			JSONObject jobj = new JSONObject(mtch.group(0));
			Message m = new Message(jobj);
			listener.onMessage(ID, m);
		} else {
			System.err.println("Could find no JSON data in packet");
		}
	}

	public void close() {
		out.flush();
		out.close();
		try {
			socket.close();
		} catch (IOException e) {
			System.err.println("Failed to close socket of client #" + this.ID);
		}
	}

	@Override
	public void run() {
		System.out.println("Started client #" + ID);
		try {
			// read request
			String line;
			line = in.readLine();
			if(line == null) {
				return;
			}
			StringBuilder raw = new StringBuilder();
			raw.append("" + line);
			boolean isPost = line.startsWith("POST");
			int contentLength = 0;
			while (!(line = in.readLine()).equals("")) {
				raw.append('\n' + line);
				if (isPost) {
					final String contentHeader = "Content-Length: ";
					if (line.startsWith(contentHeader)) {
						contentLength = Integer.parseInt(line.substring(contentHeader.length()));
					}
				}
			}
			StringBuilder body = new StringBuilder();
			if (isPost) {
				int c = 0;
				for (int i = 0; i < contentLength; i++) {
					c = in.read();
					body.append((char) c);
				}
			}
			raw.append(body.toString());
			interpret(raw.toString());

			//
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			interpret('\n' + sw.toString());
		}
		close();

	}

}
