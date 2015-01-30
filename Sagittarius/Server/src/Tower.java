import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class Tower {
	
	private static int resetAngleHorizontal;
	private static int resetAngleVertical;
	private static DataOutputStream dos;
	private static DataInputStream dis;
	private static boolean isInitialized;
	
	public static void init(DataOutputStream dos, DataInputStream dis) {
		Tower.dos = dos;
		Tower.dis = dis;
		Tower.isInitialized = true;
	}
	
	public static void shoot() {
		if (isInitialized) {
			try {
				dos.writeInt(TowerProtocol.SHOOT);
				dos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void rotateToAngleVertical(int param) {
		if (isInitialized) {
			try {			
				dos.writeInt(TowerProtocol.ANGLE_VER);
				dos.flush();
				int angle = dis.readInt() - resetAngleVertical;
				rotateAngleVertical(angle + param, true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void rotateToAngleHorizontal(int param) {
		if (isInitialized) {
			try {			
				dos.writeInt(TowerProtocol.ANGLE_HOR);
				dos.flush();
				int angle = dis.readInt() - resetAngleHorizontal;
				rotateAngleHorizontal(-angle + param*7, true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
		
	
	public static void rotateAngleVertical(int angle, boolean motor) {
		if (isInitialized) {
			try {
				dos.writeInt(TowerProtocol.ROTATE_ANGLE_VER);
				dos.writeInt(motor ? -angle : -angle);
				dos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void moveBackToStart() {
		if (isInitialized) {
			try {
				dos.writeInt(TowerProtocol.ANGLE_HOR);
				dos.flush();
				int angle = dis.readInt() - resetAngleHorizontal;
				rotateAngleHorizontal(-angle, true);			
				dos.writeInt(TowerProtocol.ANGLE_VER);
				dos.flush();
				angle = dis.readInt() - resetAngleVertical;
				rotateAngleVertical(angle, true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public static void rotateAngleHorizontal(int angle, boolean motor) {
		if (isInitialized) {
			try {
				dos.writeInt(TowerProtocol.ROTATE_ANGLE_HOR);
				dos.writeInt(motor ? angle : angle*7);
				dos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static int getAngleVertical() {
		if (isInitialized) {
			try {
				dos.writeInt(TowerProtocol.ANGLE_VER);
				dos.flush();
				return dis.readInt() - resetAngleVertical;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return -1;
			}
		} else return -1;
	}
	
	public static int getAngleHorizontal() {
		if (isInitialized) {
			try {
				dos.writeInt(TowerProtocol.ANGLE_HOR);
				dos.flush();
				return dis.readInt() - resetAngleHorizontal;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return -1;
			}
		} else return -1;
	}

	public static void setResetAngleHorizontal(int resetAngleHorizontal) {
		Tower.resetAngleHorizontal = resetAngleHorizontal;
	}

	public static void setResetAngleVertical(int resetAngleVertical) {
		Tower.resetAngleVertical = resetAngleVertical;
	}
}
