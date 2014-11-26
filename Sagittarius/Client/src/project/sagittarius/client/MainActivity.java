package project.sagittarius.client;
import java.io.IOException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;


@SuppressWarnings("deprecation")
public class MainActivity extends Activity {
	
    private EditText tf_connect;
    private Activity activity;
	private Camera camera;
	private Connecter connecter;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        int camId = findFrontFacingCamera();
        camera = Camera.open(camId);
        
		SurfaceView mview = new SurfaceView(getApplicationContext());
		try {
			camera.setPreviewDisplay(mview.getHolder());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		camera.startPreview();
		
        tf_connect = (EditText)findViewById(R.id.tf_ip);
        
    	findViewById(R.id.bt_connect).setOnClickListener(new OnClickListener() {
    		
			public void onClick(View v) {
				connecter = new Connecter(activity);
				connecter.execute(tf_connect.getText().toString());
			}
    	});
    	
    	findViewById(R.id.bt_sendPicture).setOnClickListener(new OnClickListener() {
    		
			public void onClick(View v) {
				makePicture();
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
	
	private void makePicture() {
		camera.startPreview();
		try {
			camera.takePicture(null, null, new PictureCallback() {
				
				@Override
				public void onPictureTaken(byte[] data, Camera camera) {
					connecter.sendData(data);
				}
			});
		} catch (Exception e) {
			Log.d("debug", "test");
		}
        
        Log.d("debug", "ran through stuff");
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
    
    public static void setCameraDisplayOrientation(Activity activity,
            int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
}
