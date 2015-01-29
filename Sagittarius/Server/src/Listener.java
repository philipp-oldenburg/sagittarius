import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Listener extends Thread {

	private Socket client;
	private boolean isRightEye;
	private DataOutputStream out;
	private ClientWaiter clientWaiter;
	private boolean focused;
	
	public class Protocol {
		public static final int FOCUSED = 0;
		public static final int SHOT = 1;
		public static final int FOCUS = 2;
		public static final int DATA = 3;
	}

	public Listener(Socket client, ServerSocket server, ClientWaiter clientWaiter) {
		this.client = client;
		this.clientWaiter = clientWaiter;
	}

	public void run() {
		DataInputStream in = null;
		try {
			in = new DataInputStream(client.getInputStream());
			out = new DataOutputStream(client.getOutputStream());
		} catch (IOException e) {
			System.out.println("Read failed");
		}
		
		JFrame frame = new JFrame();
		MyJPanel myPanel = new MyJPanel();
		frame.add(myPanel);
		frame.pack();
		frame.setVisible(true);
		frame.toFront();
		
		while (true) {
			try {
				int protocol = in.readInt();
				if (protocol == Protocol.FOCUSED) {
					focused = true;
					clientWaiter.cameraFocused();
				}
				else if (protocol == Protocol.DATA) {
					int length = in.readInt();
					byte[] message = new byte[length];
					in.readFully(message, 0, message.length);
					System.out.println(message.length);
					
					ImageIcon icon = new ImageIcon(message);
					Image img = icon.getImage();

					BufferedImage bi = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_BGR);

					Graphics2D g2 = bi.createGraphics();
					g2.drawImage(img, 0, 0, null);
					g2.dispose();
					ImageIO.write(bi, "jpg", new File(isRightEye ? "pics/1.jpg" : "pics/2.jpg"));
					
					if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
						if (isRightEye) {
							img = rotate(icon.getImage(), 180);
						}
						BufferedImage image = toBufferedImage(img);
//						Detector.DetectedObject[] objects = Detector.detectBalloon(image);
//						Graphics2D g2 = image.createGraphics();
//						for (Detector.DetectedObject detectedObject : objects) {
//							g2.drawOval(detectedObject.x - (detectedObject.width/2), detectedObject.y - (detectedObject.height/2), detectedObject.width, detectedObject.height);
//						}
						image = resize(image, 768, 462);
						myPanel.setImage(image);
						myPanel.repaint();
						frame.pack();
					}
				}
			} catch (IOException e) {
				if (e instanceof EOFException) {
					System.out.println("cancel connection to:"+client.getLocalAddress());
					clientWaiter.imOff(this);
					frame.setVisible(false);
					break;
				}
				e.printStackTrace();
			}
		}
	}

	public static BufferedImage resize(BufferedImage image, int width,
			int height) {
		BufferedImage bi = new BufferedImage(width, height,
				BufferedImage.TRANSLUCENT);
		Graphics2D g2d = (Graphics2D) bi.createGraphics();
		g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY));
		g2d.drawImage(image, 0, 0, width, height, null);
		g2d.dispose();
		return bi;
	}

	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null),
				img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}
	
	public static Image rotate(Image img, double angle)
	{
	    double sin = Math.abs(Math.sin(Math.toRadians(angle))),
	           cos = Math.abs(Math.cos(Math.toRadians(angle)));

	    int w = img.getWidth(null), h = img.getHeight(null);

	    int neww = (int) Math.floor(w*cos + h*sin),
	        newh = (int) Math.floor(h*cos + w*sin);

	    BufferedImage bimg = toBufferedImage(getEmptyImage(neww, newh));
	    Graphics2D g = bimg.createGraphics();

	    g.translate((neww-w)/2, (newh-h)/2);
	    g.rotate(Math.toRadians(angle), w/2, h/2);
	    g.drawRenderedImage(toBufferedImage(img), null);
	    g.dispose();

	    return toImage(bimg);
	}
	
	public static Image getEmptyImage(int width, int height){
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        return toImage(img);
    }
	
	public static Image toImage(BufferedImage bimage){
        // Casting is enough to convert from BufferedImage to Image
        Image img = (Image) bimage;
        return img;
    }

	public void setRightEye(boolean b) {
		this.isRightEye = b;
	}

	public boolean isFocused() {
		return focused;
	}

	public void sendPictureCommand() {
		try {
			out.writeInt(Protocol.SHOT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendFocusCommand() {
		try {
			out.writeInt(Protocol.FOCUS);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
