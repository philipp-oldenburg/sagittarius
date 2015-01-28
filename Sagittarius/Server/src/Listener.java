import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Listener extends Thread {

	private Socket client;
	private boolean isRightEye;

	public Listener(Socket client) {
		this.client = client;
	}

	public void run() {
		DataInputStream in = null;
		try {
			in = new DataInputStream(client.getInputStream());
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
				int length = in.readInt();
				System.out.println("empfange picture");
				if (length > 0) {
					byte[] message = new byte[length];
					in.readFully(message, 0, message.length);
					System.out.println(message.length);
					
					ImageIcon icon = new ImageIcon(message);
					Image img = icon.getImage();
					if (isRightEye) {
						img = rotate(icon.getImage(), 180);
					}
					//Image rotatedImage = rotate(icon.getImage(), 90);
					BufferedImage image = toBufferedImage(img);
//					Detector.DetectedObject[] objects = Detector.detectBalloon(image);
//					Graphics2D g2 = image.createGraphics();
//					for (Detector.DetectedObject detectedObject : objects) {
//						g2.drawOval(detectedObject.x - (detectedObject.width/2), detectedObject.y - (detectedObject.height/2), detectedObject.width, detectedObject.height);
//					}
					image = resize(image, 768, 462);
					myPanel.setImage(image);
					myPanel.repaint();
					frame.pack();
				}
			} catch (IOException e) {
				if (e instanceof EOFException) {
					System.out.println("cancel connection to:"+client.getLocalAddress());
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

}
