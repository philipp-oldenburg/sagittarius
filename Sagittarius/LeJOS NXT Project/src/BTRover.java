import java.io.*;

import lejos.nxt.*;
import lejos.nxt.comm.*;

public class BTRover {

	private static BTRover btReceive;
	private Executer executer;

	public class Protocol {
		public static final int LEFT = 0;
		public static final int RIGHT = 1;
		public static final int FORWARD = 2;
		public static final int BACKWARDS = 3;
		public static final int STOP_MOVING = 4;
		public static final int STOP_RUNNING = 5;
	}

	public static void main(String[] args) {
		try {
			btReceive = new BTRover();
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

			DataInputStream dis = connection.openDataInputStream();
			DataOutputStream dos = connection.openDataOutputStream();

			executer = new Executer();
			executer.start();
			
			Motor.A.setSpeed(100);
			Motor.B.setSpeed(100);

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
					case Protocol.FORWARD:
						Motor.A.forward();
						Motor.B.forward();
						System.out.println("FORWARD");
						break;
					case Protocol.BACKWARDS:
						Motor.A.backward();
						Motor.B.backward();
						System.out.println("BACKWARDS");
						break;
					case Protocol.LEFT:
						Motor.A.forward();
						Motor.B.backward();
						System.out.println("LEFT");
						break;
					case Protocol.RIGHT:
						Motor.A.backward();
						Motor.B.forward();
						System.out.println("RIGHT");
						break;
					case Protocol.STOP_MOVING:
						Motor.A.stop();
						Motor.B.stop();
						System.out.println("STOP_MOVING");
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