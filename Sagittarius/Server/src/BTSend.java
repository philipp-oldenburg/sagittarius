import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;

public class BTSend {
	
	private static DataOutputStream dos;
	private static DataInputStream dis;
	private static NXTConnector conn;

	public class Protocol {
		public static final int LEFT = 0;
		public static final int RIGHT = 1;
		public static final int FORWARD = 2;
		public static final int BACKWARDS = 3;
		public static final int STOP_MOVING = 4;
		public static final int STOP_RUNNING = 5;
	}
	
	public void startConnection() {
		createFrame();
	}

	private static void createFrame() {
		JFrame frame = new JFrame("Control");
		
		GridLayout gridLayout = new GridLayout(2,3, 10, 10);
		
		JPanel panel = new JPanel(gridLayout);

		// Create a BindingFactory with the default margin and control spacing:
		int i = 2;
		int j = 3;
		JPanel[][] panelHolder = new JPanel[i][j];    
		panel.setLayout(new GridLayout(i,j, 10, 10));

		for(int m = 0; m < i; m++) {
		   for(int n = 0; n < j; n++) {
		      panelHolder[m][n] = new JPanel();
		      panel.add(panelHolder[m][n]);
		   }
		}
		
		JButton up = new JButton("UP");
		JButton down = new JButton("DOWN");
		JButton left = new JButton("LEFT");
		JButton right = new JButton("RIGHT");
		JButton disconnect = new JButton("DISC");
		JButton btConnect = new JButton("BTConnect");
		assignMouseListener(up, down, left, right, disconnect, btConnect);
		
		panelHolder[0][0].add(btConnect);
		panelHolder[0][1].add(up);
		panelHolder[0][2].add(disconnect);
		panelHolder[1][0].add(left);
		panelHolder[1][1].add(down);
		panelHolder[1][2].add(right);
		
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}

	private static void assignMouseListener(JButton up, JButton down, JButton left, JButton right, JButton disconnect, JButton btConnect) {
		up.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				try {
					dos.writeInt(Protocol.STOP_MOVING);
					dos.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					dos.writeInt(Protocol.FORWARD);
					dos.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		
		down.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				try {
					dos.writeInt(Protocol.STOP_MOVING);
					dos.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					dos.writeInt(Protocol.BACKWARDS);
					dos.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		left.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				try {
					dos.writeInt(Protocol.STOP_MOVING);
					dos.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					dos.writeInt(Protocol.LEFT);
					dos.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		right.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				try {
					dos.writeInt(Protocol.STOP_MOVING);
					dos.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					dos.writeInt(Protocol.RIGHT);
					dos.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		disconnect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					dos.writeInt(Protocol.STOP_RUNNING);
					dos.flush();
					dis.close();
					dos.close();
					conn.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btConnect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				conn = new NXTConnector();
				
				conn.addLogListener(new NXTCommLogListener(){

					public void logEvent(String message) {
						System.out.println("BTSend Log.listener: "+message);
						
					}

					public void logEvent(Throwable throwable) {
						System.out.println("BTSend Log.listener - stack trace: ");
						 throwable.printStackTrace();
						
					}
					
				} 
				);
				// Connect to any NXT over Bluetooth
				boolean connected = conn.connectTo("btspp://NXT");
			
				
				if (!connected) {
					System.err.println("Failed to connect to any NXT");
				}
				
				dos = new DataOutputStream(conn.getOutputStream());
				dis = new DataInputStream(conn.getInputStream());
			}
		});
	}
}