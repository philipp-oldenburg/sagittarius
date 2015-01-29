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

public class BTTower {
	
	private static DataOutputStream dos;
	private static DataInputStream dis;
	private static NXTConnector conn;
	
	public void createFrame(ClientWaiter clientWaiter) {
		JFrame frame = new JFrame("Tower Control");
		
		GridLayout gridLayout = new GridLayout(2,3, 10, 10);
		
		JPanel panel = new JPanel(gridLayout);
		
		// Create a BindingFactory with the default margin and control spacing:
		int i = 5;
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
		JButton resetVertical = new JButton("Reset Vert");
		JButton resetHorizontal = new JButton("Reset Hori");
		JButton shoot = new JButton("Shoot");
		JButton disconnect = new JButton("DISC");
		JButton btConnect = new JButton("BTConnect");
		JButton btRotateVer = new JButton("Rotate90Ver");
		JButton btRotateHor = new JButton("Rotate90Hor");
		JButton btGetVer = new JButton("getVer");
		JButton btBackToStart = new JButton("BackToStart");
		JButton btGetHor = new JButton("getHori");
		
		assignMouseListener(up, down, left, right, disconnect, btConnect, clientWaiter, resetHorizontal, resetVertical, shoot, btRotateHor, btRotateVer, btGetVer, btGetHor, btBackToStart);

		panelHolder[0][0].add(btConnect);
		panelHolder[0][1].add(up);
		panelHolder[0][2].add(disconnect);
		panelHolder[1][0].add(left);
		panelHolder[1][1].add(down);
		panelHolder[1][2].add(right);
		panelHolder[2][0].add(resetHorizontal);
		panelHolder[2][1].add(resetVertical);
		panelHolder[2][2].add(shoot);
		panelHolder[3][0].add(btRotateVer);
		panelHolder[3][1].add(btRotateHor);
		panelHolder[3][2].add(btGetVer);
		panelHolder[4][0].add(btBackToStart);
		panelHolder[4][1].add(btGetHor);
		
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}

	private static void assignMouseListener(JButton up, JButton down, JButton left, JButton right, JButton disconnect, JButton btConnect, final ClientWaiter clientWaiter, JButton resetHorizontal, JButton resetVertical, JButton shoot, JButton btRotateHor, JButton btRotateVer, JButton btGetVer, JButton btGetHor, JButton btBackToStart) {
		up.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				try {
					dos.writeInt(TowerProtocol.MOVE_STOP);
					dos.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					dos.writeInt(TowerProtocol.MOVE_UP);
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
					dos.writeInt(TowerProtocol.MOVE_STOP);
					dos.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					dos.writeInt(TowerProtocol.MOVE_DOWN);
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
					dos.writeInt(TowerProtocol.ROTATE_STOP);
					dos.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					dos.writeInt(TowerProtocol.ROTATE_LEFT);
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
					dos.writeInt(TowerProtocol.ROTATE_STOP);
					dos.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					dos.writeInt(TowerProtocol.ROTATE_RIGHT);
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
					dos.writeInt(TowerProtocol.STOP_RUNNING);
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
				boolean connected = conn.connectTo("btspp://TOWER");
//				boolean connected = conn.connectTo("NXT", "0016531787F2",  NXTCommFactory.BLUETOOTH);
			
				
				if (!connected) {
					System.err.println("Failed to connect to any NXT");
				}
				
				dos = new DataOutputStream(conn.getOutputStream());
				dis = new DataInputStream(conn.getInputStream());
				Tower.init(dos, dis);
			}
		});
		resetHorizontal.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					dos.writeInt(TowerProtocol.ANGLE_HOR);
					dos.flush();
					Tower.setResetAngleHorizontal(dis.readInt());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		resetVertical.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					dos.writeInt(TowerProtocol.ANGLE_VER);
					dos.flush();
					Tower.setResetAngleVertical(dis.readInt());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		shoot.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					dos.writeInt(TowerProtocol.SHOOT);
					dos.flush();
					System.out.println("Shoot");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		btRotateHor.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Tower.rotateAngleHorizontal(90, false);
			}
		});
		btGetVer.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(Tower.getAngleVertical());
			}
		});
		btRotateVer.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Tower.rotateAngleVertical(-5, false);
			}
		});
		btBackToStart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Tower.moveBackToStart();
			}
		});
		btGetHor.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(Tower.getAngleHorizontal());
			}
		});
	}
}