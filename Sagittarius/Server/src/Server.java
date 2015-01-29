import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;


public class Server {
	
	public static void main(String[] args) {
		try {
			new Server().run();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private void run() throws UnknownHostException {
		ServerSocket server = null;
		try{
		   server = new ServerSocket(4321); 
		} catch (IOException e) {
		   System.out.println("Could not listen on port 4321");
		}
		System.out.println("Listening on port 4321 IP:" + InetAddress.getLocalHost());
		
		ClientWaiter clientWaiter = new ClientWaiter(server);
		clientWaiter.start();
		
		BTRover btRover = new BTRover();
		btRover.createFrame(clientWaiter);
		
		BTTower btTower = new BTTower();
		btTower.createFrame(clientWaiter);
	}
	
	

}
