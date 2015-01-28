import java.io.IOException;
import java.net.ServerSocket;


public class Server {
	
	public static void main(String[] args) {
		new Server().run();
	}

	private void run() {
		ServerSocket server = null;
		try{
		   server = new ServerSocket(4321); 
		} catch (IOException e) {
		   System.out.println("Could not listen on port 4321");
		}
		System.out.println("Listening on port 4321");
		
		ClientWaiter clientWaiter = new ClientWaiter(server);
		clientWaiter.start();
		
		BTSend btSend = new BTSend();
		btSend.startConnection();
	}
	
	

}
