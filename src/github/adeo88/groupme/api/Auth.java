package github.adeo88.groupme.api;

public class Auth implements Runnable {
	public interface AuthInitializer {
		void openBrowser(String url);
		
		Runnable findToken(Auth auth);
	}

	public AuthInitializer initializer;
	public String url;
	public Auth(String url, AuthInitializer intializer) {
		this.initializer = intializer;
		this.url = url;
	}
	
	public String token;
	
	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public void run() {
		Thread t = new Thread(initializer.findToken(this));
		t.start();
		initializer.openBrowser(url);
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
