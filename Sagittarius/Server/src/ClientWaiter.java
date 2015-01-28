import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class ClientWaiter extends Thread {

	private ServerSocket server;
	private ArrayList listeners;

	public ClientWaiter(ServerSocket server) {
		this.server = server;
		this.listeners = new ArrayList();
	}
	
	public void run() {
		while (true) {
			Socket client = null;
			try {
				client = server.accept();
			} catch (IOException e) {
				System.out.println("Accept failed: 4321");
			}
			System.out.println("Connected to:" + client.getLocalAddress());
			
			Listener listener = new Listener(client);
			JFrame frame = new JFrame();
			int n = JOptionPane.showConfirmDialog(
				    frame,
				    "Right eye?",
				    "An Inane Question",
				    JOptionPane.YES_NO_OPTION);
			if (n == 0) {
				listener.setRightEye(true);
			} else listener.setRightEye(false);
			
			listener.start();
			listeners.add(listener);
		}
	}

}
