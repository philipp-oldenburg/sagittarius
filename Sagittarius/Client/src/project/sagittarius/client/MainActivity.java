package project.sagittarius.client;
import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


@SuppressWarnings("deprecation")
public class MainActivity extends Activity {
	
    private EditText tf_connect;
    private Camera camera;
	private Connecter connecter;
	private boolean connected;
	private SurfaceView mview;
	private boolean resumeHasRun;
	
	@Override
	protected void onResume() {
		super.onResume();
		if (resumeHasRun) {
			int camId = findFrontFacingCamera();
	        camera = Camera.open(camId);
	        Log.d("debug", "camera opened");
	        Parameters parameters = camera.getParameters();
	        parameters.setPictureSize(2048, 1232);
	        
	        camera.setParameters(parameters);
	        
			mview = new SurfaceView(getApplicationContext());
			try {
				mview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
				camera.setPreviewDisplay(mview.getHolder());
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			camera.startPreview();
		} else resumeHasRun = true;
		
	}
	@Override
	protected void onPause() {
		if (camera != null) {
            // Call stopPreview() to stop updating the preview surface.
        	camera.stopPreview();
        	camera.setPreviewCallback(null);
        
            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            camera.release();
            Log.d("debug", "Camera released");
            camera = null;
        }
		super.onPause();
	}
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        int camId = findFrontFacingCamera();
        camera = Camera.open(camId);
    
        Parameters parameters = camera.getParameters();
        parameters.setPictureSize(2048, 1232);
        
        camera.setParameters(parameters);
        
		mview = new SurfaceView(getApplicationContext());
		try {
			mview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			camera.setPreviewDisplay(mview.getHolder());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		camera.startPreview();
		
		
        tf_connect = (EditText)findViewById(R.id.tf_ip);
        
        final Button buttonConnect = (Button) findViewById(R.id.bt_connect);
    	buttonConnect.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (connected) {
					connecter.disconnect();
					buttonConnect.setText("Connect");
					connected = false;
				} else {
					connected = true;
					connecter = new Connecter(camera, MainActivity.this);
					connecter.execute(tf_connect.getText().toString());
					buttonConnect.setText("Disconnect");
				}
			}
    	});
    }
	
	
	private int findFrontFacingCamera() {
		int cameraId = -1;
		// Search for the front facing camera
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
				Log.d("debug", "Camera found");
				cameraId = i;
				break;
			}
		}
		return cameraId;
	}
    
    


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
}
