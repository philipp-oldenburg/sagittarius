import java.io.*;

import lejos.nxt.*;
import lejos.nxt.comm.*;

public class BTTower {

	private static BTTower btReceive;
	private Executer executer;

	public class Protocol {
		public static final int ROTATE_LEFT = 0;
		public static final int ROTATE_RIGHT = 1;
		public static final int ROTATE_STOP = 2;
		public static final int MOVE_UP = 3;
		public static final int MOVE_DOWN = 4;
		public static final int MOVE_STOP = 5;
		
		public static final int STOP_RUNNING = 7;
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
		
		System.out.println(Bluetooth.getLocalAddress());
		System.out.println(Bluetooth.getFriendlyName());
		System.out.println(Bluetooth.getAddress());
		System.out.println(Bluetooth.getName());

		while (true) {
			LCD.drawString(waiting, 0, 0);
			NXTConnection connection = Bluetooth.waitForConnection();
			LCD.clear();
			LCD.drawString(connected, 0, 0);

			DataInputStream dis = connection.openDataInputStream();
			DataOutputStream dos = connection.openDataOutputStream();

			executer = new Executer();
			executer.start();
			
			Motor.A.setSpeed(50);
			Motor.B.setSpeed(10);

			loop: while (true) {
				int n = dis.readInt();
				System.out.println(n);
//				LCD.drawInt(n, 0, 0, 1);
				switch (n) {
				case Protocol.STOP_RUNNING:
					break loop;

				default:
					executer.setExecutionCode(n);
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

		private int executionCode = 100;
		private int oldExecutionCode = 100;
		private boolean running = true;

		@Override
		public void run() {
			while (running) {
				if (oldExecutionCode != executionCode) {
					oldExecutionCode = executionCode;
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
						Motor.A.stop();
						System.out.println("STOP_ROTATING");
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

		public void setExecutionCode(int code) {
			executionCode = code;
		}
	}
}