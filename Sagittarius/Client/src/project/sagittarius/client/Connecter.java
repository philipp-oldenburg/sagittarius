package project.sagittarius.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

@SuppressWarnings("deprecation")
class Connecter extends AsyncTask<String, String, String> {

	private Socket client;
	private DataOutputStream out;
	private DataInputStream in;
	private Camera camera;
	private Activity activity;

	public class Protocol {
		public static final int FOCUSED = 0;
		public static final int SHOT = 1;
		public static final int FOCUS = 2;
		public static final int DATA = 3;
	}

	public Connecter(Camera camera, Context context) {
		this.camera = camera;
		this.activity = (Activity) context;
	}

	@Override
	protected String doInBackground(String... params) {
		Log.d("debug", "Trying to connect to:" + params[0]);
		try {
			client = new Socket(params[0], 4321);
			out = new DataOutputStream(client.getOutputStream());
			in = new DataInputStream(client.getInputStream());
		} catch (UnknownHostException e) {
			Log.d("debug", "Unknown host: " + params[0]);

		} catch (IOException e) {
			Log.d("debug", e.getLocalizedMessage());
		}
		while (true) {
			int protocol = 100;
			try {
				protocol = in.readInt();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (protocol == Protocol.FOCUS) {
				Log.d("debug", "focus received");
				camera.autoFocus(new AutoFocusCallback() {

					@Override
					public void onAutoFocus(boolean success, Camera camera) {
						Log.d("debug","focus finished, result:" + success); 
						if (success) {
							try {
								out.writeInt(Protocol.FOCUSED);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				});
			}
			else if (protocol == Protocol.SHOT) {
				makePicture();
			}
		}
	}
	
	private void makePicture() {
		activity.runOnUiThread(new Runnable() {
			  public void run() {
			    Toast.makeText(activity, "" + System.currentTimeMillis(), Toast.LENGTH_SHORT).show();
			  }
			});
		try {
			camera.takePicture(null, null, new PictureCallback() {
				@Override
				public void onPictureTaken(byte[] data, Camera camera) {
					camera.startPreview();
					sendData(data);
				}
			});
		} catch (Exception e) {
			Log.d("debug", "test");
		}
        
        Log.d("debug", "ran through stuff");
	}

	public void sendData(byte[] data) {
		try {
			Log.d("debug", "length:" + data.length);
			out.writeInt(Protocol.DATA);
			out.writeInt(data.length);
			out.write(data);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			client.close();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
