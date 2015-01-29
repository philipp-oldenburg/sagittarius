import java.io.*;
import java.util.ArrayList;

import lejos.nxt.*;
import lejos.nxt.comm.*;

public class BTTower {

	private static BTTower btReceive;
	private Executer executer;
	private DataInputStream dis;
	private DataOutputStream dos;
	private int angle;
	ArrayList<Integer> executionList = new ArrayList<Integer>();

	public class Protocol {
		public static final int ROTATE_LEFT = 0;
		public static final int ROTATE_RIGHT = 1;
		public static final int ROTATE_STOP = 2;
		
		public static final int MOVE_UP = 3;
		public static final int MOVE_DOWN = 4;
		public static final int MOVE_STOP = 5;
		
		public static final int ANGLE_HOR = 6;
		public static final int ANGLE_VER = 7;
		
		public static final int ROTATE_ANGLE_HOR = 8;
		public static final int ROTATE_ANGLE_VER = 9;
		
		public static final int SHOOT = 10;
		
		public static final int STOP_RUNNING = 11;
	}

	public static void main(String[] args) {
		try {
			btReceive = new BTTower();
			btReceive.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void run() throws IOException {
		String connected = "Connected";
		String waiting = "Waiting...";
		String closing = "Closing...";

		while (true) {
			LCD.drawString(waiting, 0, 0);
			NXTConnection connection = Bluetooth.waitForConnection();
			LCD.clear();
			LCD.drawString(connected, 0, 0);

			dis = connection.openDataInputStream();
			dos = connection.openDataOutputStream();

			executer = new Executer();
			executer.start();
			
			Motor.A.setSpeed(75);
			Motor.B.setSpeed(20);
			Motor.C.setSpeed(35);

			loop: while (true) {
				int n = dis.readInt();
				if (dis.available() != 0) {
					angle = dis.readInt();
				}
				System.out.println(n);
//				LCD.drawInt(n, 0, 0, 1);
				switch (n) {
				case Protocol.STOP_RUNNING:
					break loop;
				case Protocol.ANGLE_HOR:
					dos.writeInt(Motor.A.getPosition());
					dos.flush();
					break;
				case Protocol.ANGLE_VER:
					dos.writeInt(Motor.B.getPosition());
					dos.flush();
					break;

				default:
					executionList.add(n);
					break;
				}
			}
			
			executer.stopRunning();
			
			dis.close();
			dos.close();

			LCD.clear();
			LCD.drawString(closing, 0, 0);

			connection.close();
			LCD.clear();
		}
	}

	class Executer extends Thread {
		private boolean running = true;

		@Override
		public void run() {
			while (running) {
				if (!executionList.isEmpty()) {
					int executionCode = executionList.remove(0);
					
					switch (executionCode) {
					case Protocol.MOVE_UP:
						Motor.B.backward();
						System.out.println("UP");
						break;
					case Protocol.MOVE_DOWN:
						Motor.B.forward();
						System.out.println("DOWN");
						break;
					case Protocol.MOVE_STOP:
						Motor.B.suspendRegulation();
						System.out.println("STOP_MOVE");
						break;
					case Protocol.ROTATE_LEFT:
						Motor.A.backward();
						System.out.println("LEFT");
						break;
					case Protocol.ROTATE_RIGHT:
						Motor.A.forward();
						System.out.println("RIGHT");
						break;
					case Protocol.ROTATE_STOP:
						Motor.A.suspendRegulation();
						System.out.println("STOP_ROTATING");
						break;
					case Protocol.SHOOT:
						Motor.C.rotate(3);
						Motor.C.flt();
						System.out.println("Shoot");
						break;
					case Protocol.ROTATE_ANGLE_HOR:
						System.out.println("rotate hor:" + angle);
						Motor.A.rotate(angle, false);
						Motor.A.suspendRegulation();
						break;
					case Protocol.ROTATE_ANGLE_VER:
						System.out.println("rotate ver:" + angle);
						Motor.B.rotate(angle, false);
						Motor.B.suspendRegulation();
						break;
					default:
						break;
					}
				}
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public void stopRunning() {
			running = false;
		}
	}
}