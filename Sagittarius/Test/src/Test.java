import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.CvContour;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_highgui.*;

public class Test {

	public static void main(String[] argv) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//		Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
//		System.out.println("mat = " + mat.dump());
		Mat image = Highgui.imread("pics/balloon.jpeg");
		if (image.empty())
		{
		    System.out.println("Failed to read file.");
		    return;
		}

		Mat red_image = new Mat();
		//Core.inRange(image, new Scalar(40, 0, 180), new Scalar(135, 110, 255), red_image);
		//Core.inRange(image, new Scalar(50, 20, 20), new Scalar(244, 194, 194), red_image);
		Core.inRange(image, new Scalar(0, 0, 110), new Scalar(60, 60, 255), red_image);
		Highgui.imwrite("pics/out.png", red_image);
		
		detectEllipse(red_image);
	}
	
	// One way to tell if an object is an ellipse is to look at the relationship
	// of its area to its dimensions.  If its actual occupied area can be estimated
	// using the well-known area formula Area = PI*A*B, then it has a good chance of
	// being an ellipse.

	private static void detectEllipse(Mat red_image) {
		double MAX_TOL = 300.00;
		// This value is the maximum permissible error between actual and estimated area.
		double MIN_AREA = 100.00;
		// We need this to be high enough to get rid of things that are too small too
		// have a definite shape.  Otherwise, they will end up as ellipse false positives.
		IplImage src;
	    // the first command line parameter must be file name of binary (black-n-white) image
	    if((src=cvLoadImage("pics/out2.jpeg", 0))!= null)
	    {
	        IplImage dst  = cvCreateImage( cvGetSize(src), 8, 3 );
	        CvMemStorage storage = cvCreateMemStorage(0);
	        CvSeq contour = new CvContour();    
	        cvThreshold( src, src, 1, 255, CV_THRESH_BINARY );
	        //
	        // Invert the image such that white is foreground, black is background.
	        // Dilate to get rid of noise.
	        //
	        cvXorS(src, cvScalar(255, 0, 0, 0), src, null);
	        cvDilate(src, src, null, 2);
	        cvFindContours( src, storage, contour, Loader.sizeof(CvContour.class), CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));
	        cvZero( dst );

	        for( ; contour != null; contour = contour.h_next())
	        {
	            double actual_area = Math.abs(cvContourArea(contour, CV_WHOLE_SEQ, 0));
	            System.out.println(actual_area);
	            if (actual_area < MIN_AREA)
	                continue;

	            //
	            // FIXME:
	            // Assuming the axes of the ellipse are vertical/perpendicular.
	            //
	            CvRect rect = null;
	            if (contour instanceof CvContour) {
	            	rect = ((CvContour) contour).rect();
				}
	            else {
	            	rect = cvBoundingRect(contour,0);
				}
	            int A = rect.width() / 2; 
	            int B = rect.height() / 2;
	            double estimated_area = Math.PI * A * B;
	            double error = Math.abs(actual_area - estimated_area);
	            System.out.println(error);
	            if (error > MAX_TOL)
	                continue;    
	            System.out.println("center x: " + rect.x() + A + " y: " + rect.y() + B + " A: " + A + " B: " + B + "\n");

	            CvScalar color = CV_RGB( Math.random() * 255, Math.random() * 255, Math.random() * 255 );
	            cvDrawContours( dst, contour, color, color, -1, CV_FILLED, 8, cvPoint(0,0));
	        }
	        cvSaveImage("pics/final.png", dst);
	    }
	}

}
