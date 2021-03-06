import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


public class MyJPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private BufferedImage img;

	
	public void setImage(BufferedImage img) {
		this.img = img;
		setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(img, null, 0, 0);
	}
}
