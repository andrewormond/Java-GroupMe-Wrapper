package github.adeo88.groupme.bots;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import github.adeo88.groupme.api.Bot;
import github.adeo88.groupme.api.GroupMeAPI;
import github.adeo88.groupme.api.GroupMeException;

public class BotManager implements Runnable {

	private void printf(String formatString, Object... objects) {
		if (isDebug) {
			System.out.println(String.format(formatString, objects));
		}
	}

	public Bot bot;
	private int port;
	private ServerSocket serverSocket;
	private volatile boolean running;
	public BotListener listener;
	public boolean isDebug = false;

	public BotManager(Bot bot, int port, BotListener listener) {
		this.bot = bot;
		this.port = port;
		this.listener = listener;
	}

	public BotManager(String bot_id, int port, GroupMeAPI api, BotListener listener) throws GroupMeException {
		this(Bot.get(bot_id, api), port, listener);
	}

	@Override
	public void run() {
		printf("Starting BotManager on port %d for %s", port, bot);
		try {
			serverSocket = new ServerSocket(port);
			running = true;

			while (running) {
				try {
					Socket clientSocket = serverSocket.accept();
					PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
					BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					new Thread(new Handler(clientSocket, out, in, listener)).start();
				} catch (SocketException e) {
					running = false;
					if (isDebug) {
						System.err.println(e.getLocalizedMessage());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(2);
		}
	}

	public void stop() throws IOException {
		running = false;
		serverSocket.close();
	}

}
