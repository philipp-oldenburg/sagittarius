import static org.bytedeco.javacpp.helper.opencv_core.CV_RGB;
import static org.bytedeco.javacpp.helper.opencv_imgproc.cvFindContours;
import static org.bytedeco.javacpp.opencv_core.CV_FILLED;
import static org.bytedeco.javacpp.opencv_core.CV_WHOLE_SEQ;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvCreateMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvDrawContours;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvInRangeS;
import static org.bytedeco.javacpp.opencv_core.cvNot;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
import static org.bytedeco.javacpp.opencv_core.cvXorS;
import static org.bytedeco.javacpp.opencv_core.cvZero;
import static org.bytedeco.javacpp.opencv_highgui.cvLoadImage;
import static org.bytedeco.javacpp.opencv_highgui.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RETR_CCOMP;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RETR_EXTERNAL;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY_INV;
import static org.bytedeco.javacpp.opencv_imgproc.cvBoundingRect;
import static org.bytedeco.javacpp.opencv_imgproc.cvContourArea;
import static org.bytedeco.javacpp.opencv_imgproc.cvDilate;
import static org.bytedeco.javacpp.opencv_imgproc.cvFindContours;
import static org.bytedeco.javacpp.opencv_imgproc.cvThreshold;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.CvContour;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.helper.opencv_core.AbstractCvMemStorage;

public class Test {

	public static void main(String[] argv) {
		IplImage img = colorDetect();
		img = fillOutConturesAndDeleteSmallOnes(img);
		detectEllipse(img);
	}
	
	

	private static IplImage colorDetect() {
		//read image
        IplImage orgImg = cvLoadImage("pics/bla.jpg");
        //create binary image of original size
        IplImage img1 = cvCreateImage(cvGetSize(orgImg), 8, 1);
        IplImage img2 = cvCreateImage(cvGetSize(orgImg), 8, 1);
        //apply thresholding
        cvInRangeS(orgImg, cvScalar(0, 0, 120, 0), cvScalar(60, 60, 255, 0), img1);
        cvInRangeS(orgImg, cvScalar(60, 60, 200, 0), cvScalar(120, 120, 255, 0), img2);
        CvMemStorage storage=AbstractCvMemStorage.create();
        CvSeq contours = new CvContour();
        cvFindContours(img2, storage, contours, Loader.sizeof(CvContour.class), CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE);
        CvScalar color = CV_RGB(255, 255, 255);
        for( ; contours != null; contours = contours.h_next()) {
        	cvDrawContours( img1, contours, color, color, -1, CV_FILLED, 8, cvPoint(0,0));
        }
        cvNot(img1, img1);
        //smooth filter- median
        //cvSmooth(imgThreshold, imgThreshold);
        //save
        cvSaveImage("pics/out.jpg", img1);
        return img1;
	}



	private static IplImage fillOutConturesAndDeleteSmallOnes(IplImage src) {
        CvMemStorage storage=AbstractCvMemStorage.create();
        CvSeq contours = new CvContour();
        IplImage gry  = cvCreateImage( cvGetSize(src), 8, 1 );
        cvThreshold(src, src, 1, 255, CV_THRESH_BINARY_INV);
        cvFindContours(src, storage, contours, Loader.sizeof(CvContour.class), CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE);
        for( ; contours != null; contours = contours.h_next()) {
        	
        	double actual_area = Math.abs(cvContourArea(contours, CV_WHOLE_SEQ, 0));
        	if (actual_area < 100) {
        		CvScalar color = CV_RGB(0, 0, 0);
                cvDrawContours( gry, contours, color, color, -1, CV_FILLED, 8, cvPoint(0,0));
			} else {
				CvScalar color = CV_RGB(255, 255, 255);
	            cvDrawContours( gry, contours, color, color, -1, CV_FILLED, 8, cvPoint(0,0));
			}
        }
        cvXorS(gry, cvScalar(255, 0, 0, 0), gry, null);
        
        cvSaveImage("pics/out2.png", gry);
        return gry;
	}
	
	
	
	// One way to tell if an object is an ellipse is to look at the relationship
		// of its area to its dimensions.  If its actual occupied area can be estimated
		// using the well-known area formula Area = PI*A*B, then it has a good chance of
		// being an ellipse.
	private static void detectEllipse(IplImage src) {
		
		double MAX_TOL = 1000.00;
		// This value is the maximum permissible error between actual and estimated area.
		double MIN_AREA = 400.00;
		// We need this to be high enough to get rid of things that are too small too
		// have a definite shape.  Otherwise, they will end up as ellipse false positives.

	    // the first command line parameter must be file name of binary (black-n-white) image
	    if(src != null)
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
	            System.out.println("actual area: " + actual_area);
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
	            System.out.println("error: " + error);
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
