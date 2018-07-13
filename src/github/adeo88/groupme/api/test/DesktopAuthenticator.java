package github.adeo88.groupme.api.test;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;

import github.adeo88.groupme.api.Auth;
import github.adeo88.groupme.api.Auth.AuthInitializer;

public class DesktopAuthenticator implements AuthInitializer {

	public int port = 34343;

	public DesktopAuthenticator() {

	}

	@Override
	public void openBrowser(String url) {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(new URI(url));
			} catch (IOException | URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.err.println("Desktop is not supported");
		}
	}

	@Override
	public Runnable findToken(Auth auth) {
		// TODO Auto-generated method stub
		return new Runnable() {
			HttpServer server;
			
			class RootHandler implements HttpHandler {
				public Map<String, String> queryToMap(String query) {
					Map<String, String> result = new HashMap<>();
					for (String param : query.split("&")) {
						String[] entry = param.split("=");
						if (entry.length > 1) {
							result.put(entry[0], entry[1]);
						} else {
							result.put(entry[0], "");
						}
					}
					return result;
				}

				@Override
				public void handle(HttpExchange he) throws IOException {
					Map<String, String> parameters =  queryToMap(he.getRequestURI().getQuery());
					String response = "<html>\n" + 
							"<head>\n" + 
							"<script>\n" + 
							"function loaded()\n" + 
							"{\n" + 
							"    window.setTimeout(CloseMe, 500);\n" + 
							"}\n" + 
							"\n" + 
							"function CloseMe() \n" + 
							"{\n" + 
							"    window.close();\n" + 
							"}\n" + 
							"</script>\n" + 
							"</head>\n" + 
							"<body onLoad=\"loaded()\">\n" + 
							"Window should close shortly. If not, please close manually.\n" + 
							"</body>";
					he.sendResponseHeaders(200, response.length());
					OutputStream os = he.getResponseBody();
					os.write(response.getBytes());
					os.close();
					
					if(parameters.containsKey("access_token")) {
						auth.setToken(parameters.get("access_token"));
						server.stop(0);
					}
				}

			}
			
			public boolean hostAvailabilityCheck() { 
			    try (ServerSocket s = new ServerSocket(port)) {
			    		s.close();
			        return true;
			    } catch (IOException ex) {
			        /* ignore */
			    }
			    return false;
			}

			@Override
			public void run() {
				try {
					server = HttpServer.create(new InetSocketAddress(port), 0);
					System.out.println("server started at " + port);
					server.createContext("/", new RootHandler());
					server.setExecutor(null);
					server.start();
					while(!hostAvailabilityCheck()) {
						Thread.sleep(100);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};
	}

}
