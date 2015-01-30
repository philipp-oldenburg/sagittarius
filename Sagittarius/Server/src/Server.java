import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class Server {
	ArrayList<BufferedImage> receivedImages = new ArrayList<BufferedImage>();
	JFrame jframe = new JFrame("Dangerino");
	private BufferedImage imageLeft;
	private BufferedImage imageRight;
	
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
		
		ClientWaiter clientWaiter = new ClientWaiter(server, this);
		clientWaiter.start();
		
		BTRover btRover = new BTRover();
		btRover.createFrame(clientWaiter);
		
		BTTower btTower = new BTTower();
		btTower.createFrame(clientWaiter);
		
		while (true) {
			if (imageLeft != null && imageRight != null) {
				
				double[] values = DetectorAdjusted.analyze(imageLeft, imageRight);
				if (values != null) {
					System.out.println("Found Target\nDistance: " + values[0] + "\nhAngle: " + values[1] + "\nvAngle: " + values[2] + "\nCrosshair Left: (" + values[3] + "," + values[4] + ")\nCrosshair Right: (" + values[5] + "," + values[6] + ")");
					if (Math.abs(values[1]) < 35 && Math.abs(values[2]) < 30
							&& values[2] >= 0) {
						ArrayList<Listener> listeners = clientWaiter.getListeners();
						Image fadenkreuz = null;
						try {
							fadenkreuz = ImageIO.read(new File("pics/Fadenkreuz.png"));
							System.out.println("fadenwidth: " + fadenkreuz.getWidth(null));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for (Listener listener : listeners) {
							if (listener.isRightEye()) {
								listener.getMyPanel().getGraphics().drawImage(fadenkreuz, (int)(values[5]*0.375) - fadenkreuz.getWidth(null) / 2, (int)(values[6]*0.375) - fadenkreuz.getHeight(null) / 2, null);
							} else {
								listener.getMyPanel().getGraphics().drawImage(fadenkreuz, (int)(values[3]*0.375) - fadenkreuz.getWidth(null) / 2, (int)(values[4]*0.375) - fadenkreuz.getHeight(null) / 2, null);
							}
						}
						Tower.rotateToAngleHorizontal((int) values[1]);
						
						Tower.rotateToAngleVertical((int) (values[2] + values[0]/100 * 5) + 4);
						
						int n = JOptionPane.showConfirmDialog(jframe,
								"Tower bereit zum Schieﬂen: Accept?",
								"An Inane Question", JOptionPane.YES_NO_OPTION);
						if (n == 0) {
							Tower.shoot();
						}
						System.out.println("horizontal:" + Tower.getAngleHorizontal());
						System.out.println("vertical:" +  Tower.getAngleVertical());
					} else
						System.out
								.println("Berechnungen auﬂerhalb Turmreichweite");
				} else System.out.println("kein Laserpointer gefunden.");
				imageLeft = null;
				imageRight = null;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void receivedPhoto(BufferedImage bi, boolean isRightEye) {
		if (isRightEye) {
			imageRight = bi;
		} else {
			imageLeft = bi;
		}
	}
	
	
	
	

}
