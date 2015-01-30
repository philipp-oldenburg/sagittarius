import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class ClientWaiter extends Thread {

	private ArrayList<Listener> listeners;
	public ArrayList<Listener> getListeners() {
		return listeners;
	}

	private ServerSocket serverSocket;
	private Server server;

	public ClientWaiter(ServerSocket serverSocket, Server server) {
		this.serverSocket = serverSocket;
		this.listeners = new ArrayList<Listener>();
		this.server = server;
	}
	
	public void run() {
		while (true) {
			Socket client = null;
			try {
				client = serverSocket.accept();
				System.out.println("accepted");
			} catch (IOException e) {
				System.out.println("Accept failed: 4321");
			}
			System.out.println("Connected to:" + client.getLocalAddress());
			
			Listener listener = new Listener(client, serverSocket, this, server);
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

	public void cameraFocused() {
		boolean allFocused = true;
		for (Listener listener : listeners) {
			if (!listener.isFocused()) {
				allFocused = false;
			}
		}
		if (allFocused) {
			for (Listener listener : listeners) {
				listener.sendPictureCommand();
			}
		}
	}

	public void startSynchronizedPicture() {
		for (Listener listener : listeners) {
			listener.sendFocusCommand();
		}
	}

	public void imOff(Listener listener) {
		listeners.remove(listener);
	}

}
