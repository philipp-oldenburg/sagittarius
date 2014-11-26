package project.sagittarius.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

class Connecter extends AsyncTask<String, String, String> {
	
	private Socket client;
	private DataOutputStream out;
	private BufferedReader in;
	
	private Activity activity;

	public Connecter(Activity activity) {
		this.activity = activity;
	}
	
	@Override
	protected String doInBackground(String... params) {
		Log.d("debug", "Trying to connect to:" + params[0]);
		try{
		   client = new Socket(params[0], 4321);
		   out = new DataOutputStream(client.getOutputStream());
		   in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		} catch(UnknownHostException e) {
		   Log.d("debug", "Unknown host: " + params[0]);

		} catch(IOException e) {
			Log.d("debug", e.getLocalizedMessage());
		}
		return null;
	}
	
	public void sendData(byte[] data) {
		try {
			Log.d("debug", "length:" + data.length);
			out.writeInt(data.length);
			out.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
   

}
