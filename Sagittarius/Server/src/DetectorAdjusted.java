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
import static org.bytedeco.javacpp.opencv_imgproc.*;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.CvContour;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.helper.opencv_core.AbstractCvMemStorage;

public class DetectorAdjusted {

	public static void main(String[] argv) {
		
		Vision vision = colorDetect(null, null);
		//IplImage img = colorDetect(null, null);
		
		//img = fillOutConturesAndDeleteSmallOnes(img);
		vision = fillOutConturesAndDeleteSmallOnes(vision);
		
		ResCoord coord = detectEllipse(vision);
		System.out.println("Left:");
		System.out.println("x=  " + coord.xLeft);
		System.out.println("y=  " + coord.yLeft);
		System.out.println("Right:");
		System.out.println("x=  " + coord.xRight);
		System.out.println("y=  " + coord.yRight);
		System.out.println();
		calculateFromResCoord(coord);
		
//		DetectedObject[] objects = detectEllipse(vision);
//		for (DetectedObject object : objects) {
//			System.out.println(object.x + " " + object.y + " " + object.width + " " + object.height);
//		}
		
	}
	
	public class DetectedObject {
		
		public int x, y, width, height;
		
		public DetectedObject(int x, int y, int width, int height) {
			this.x = x; this.y = y; this.width = width; this.height = height;
		}
		
	}
	
//	public static DetectedObject[] detectBalloon(BufferedImage image) {
//		IplImage iplImage = IplImage.createFrom(image);
//
//		iplImage = colorDetect(iplImage);
//		
//		iplImage = fillOutConturesAndDeleteSmallOnes(iplImage);
//		return detectEllipse(iplImage);
//	}

	private static Vision colorDetect(IplImage imgLeft, IplImage imgRight) {
		//read image
		long timestamp = System.currentTimeMillis();
		
		if (imgLeft == null) {
			imgLeft = cvLoadImage("pics/Doppelphoto3_linkes_Auge.jpg");
		}
		if (imgRight == null) {
			imgRight = cvLoadImage("pics/Doppelphoto3_rechtes_Auge.jpg");
		}
        System.out.println("took: " + (System.currentTimeMillis()-timestamp) + "ms");
        //create binary image of original size
        
        IplImage imgHSVLeft = cvCreateImage(cvGetSize(imgLeft), 8, 3);
        IplImage imgHSVRight = cvCreateImage(cvGetSize(imgRight), 8, 3);
        cvCvtColor(imgLeft, imgHSVLeft, CV_BGR2HSV);
        cvCvtColor(imgRight, imgHSVRight, CV_BGR2HSV);
        
      //TODO Remove after Testing
        cvSaveImage("pics/out0Left.jpg", imgLeft);
        cvSaveImage("pics/out0Right.jpg", imgRight);
        
        
        IplImage imgLeft1 = cvCreateImage(cvGetSize(imgHSVLeft), 8, 1);
        IplImage imgLeft2 = cvCreateImage(cvGetSize(imgHSVLeft), 8, 1);
        IplImage imgRight1 = cvCreateImage(cvGetSize(imgHSVRight), 8, 1);
        IplImage imgRight2 = cvCreateImage(cvGetSize(imgHSVRight), 8, 1);
        //apply thresholding
        cvInRangeS(imgHSVLeft, cvScalar(60, 64, 204, 0), cvScalar(90, 255, 255, 0), imgLeft1);
        cvInRangeS(imgHSVLeft, cvScalar(60, 128, 128, 0), cvScalar(90, 255, 255, 0), imgLeft2);
        cvInRangeS(imgHSVRight, cvScalar(60, 64, 204, 0), cvScalar(90, 255, 255, 0), imgRight1);
        cvInRangeS(imgHSVRight, cvScalar(60, 128, 128, 0), cvScalar(90, 255, 255, 0), imgRight2);
        
//        cvInRangeS(imgHSVLeft, cvScalar(60, 64, 204, 0), cvScalar(90, 255, 255, 0), imgLeft1);
//        cvInRangeS(imgHSVLeft, cvScalar(60, 128, 204, 0), cvScalar(90, 255, 255, 0), imgLeft2);
//        cvInRangeS(imgHSVRight, cvScalar(60, 64, 204, 0), cvScalar(90, 255, 255, 0), imgRight1);
//        cvInRangeS(imgHSVRight, cvScalar(60, 128, 204, 0), cvScalar(90, 255, 255, 0), imgRight2);
        
        
        CvMemStorage storage=AbstractCvMemStorage.create();
        CvScalar color = CV_RGB(255, 255, 255);
        System.out.println();
        
        //Left
        CvSeq contours = new CvContour();
        cvFindContours(imgLeft2, storage, contours, Loader.sizeof(CvContour.class), CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE);
        
        for( ; contours != null && !contours.isNull(); contours = contours.h_next()) {
        	cvDrawContours( imgLeft1, contours, color, color, -1, CV_FILLED, 8, cvPoint(0,0));
        }
        
        cvNot(imgLeft1, imgLeft1);
        
        
        //Right
        contours = new CvContour();
        cvFindContours(imgRight2, storage, contours, Loader.sizeof(CvContour.class), CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE);
        
        for( ; contours != null && !contours.isNull(); contours = contours.h_next()) {
        	cvDrawContours( imgRight1, contours, color, color, -1, CV_FILLED, 8, cvPoint(0,0));
        }
        
        cvNot(imgRight1, imgRight1);
        
        //smooth filter- median
        //cvSmooth(imgThreshold, imgThreshold);
        //save
        //TODO Remove after Testing
        cvSaveImage("pics/outLeft.jpg", imgLeft1);
        cvSaveImage("pics/outRight.jpg", imgRight1);
        Vision vision = new Vision (imgLeft1, imgRight1);
        return vision;
	}



	private static Vision fillOutConturesAndDeleteSmallOnes(Vision vision) {
		IplImage srcLeft = vision.left;
		IplImage srcRight = vision.right;
        CvMemStorage storage=AbstractCvMemStorage.create();
        
        IplImage gryLeft  = cvCreateImage( cvGetSize(srcLeft), 8, 1 );
        IplImage gryRight  = cvCreateImage( cvGetSize(srcRight), 8, 1 );
        cvThreshold(srcLeft, srcLeft, 1, 255, CV_THRESH_BINARY_INV);
        cvThreshold(srcRight, srcRight, 1, 255, CV_THRESH_BINARY_INV);
        
        //Left
        CvSeq contours = new CvContour();
        cvFindContours(srcLeft, storage, contours, Loader.sizeof(CvContour.class), CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE);
        for( ; contours != null && !contours.isNull(); contours = contours.h_next()) {
        	
        	double actual_area = Math.abs(cvContourArea(contours, CV_WHOLE_SEQ, 0));
        	if (actual_area < 0) {
        		CvScalar color = CV_RGB(0, 0, 0);
                cvDrawContours( gryLeft, contours, color, color, -1, CV_FILLED, 8, cvPoint(0,0));
			} else {
				CvScalar color = CV_RGB(255, 255, 255);
	            cvDrawContours( gryLeft, contours, color, color, -1, CV_FILLED, 8, cvPoint(0,0));
			}
        }
        
        cvXorS(gryLeft, cvScalar(255, 0, 0, 0), gryLeft, null);
        
        
        //Right
        contours = new CvContour();
        cvFindContours(srcRight, storage, contours, Loader.sizeof(CvContour.class), CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE);
        for( ; contours != null && !contours.isNull(); contours = contours.h_next()) {
        	
        	double actual_area = Math.abs(cvContourArea(contours, CV_WHOLE_SEQ, 0));
        	if (actual_area < 0) {
        		CvScalar color = CV_RGB(0, 0, 0);
                cvDrawContours( gryRight, contours, color, color, -1, CV_FILLED, 8, cvPoint(0,0));
			} else {
				CvScalar color = CV_RGB(255, 255, 255);
	            cvDrawContours( gryRight, contours, color, color, -1, CV_FILLED, 8, cvPoint(0,0));
			}
        }
        
        cvXorS(gryRight, cvScalar(255, 0, 0, 0), gryRight, null);
        
        
        
        cvSaveImage("pics/out2Left.png", gryLeft);
        cvSaveImage("pics/out2Right.png", gryRight);
        return new Vision(gryLeft, gryRight);
	}
	
	
	
	// One way to tell if an object is an ellipse is to look at the relationship
		// of its area to its dimensions.  If its actual occupied area can be estimated
		// using the well-known area formula Area = PI*A*B, then it has a good chance of
		// being an ellipse.
	private static ResCoord detectEllipse(Vision vision) {
		IplImage srcLeft = vision.left;
		IplImage srcRight = vision.right;
		
//		DetectedObject[] objects;
		ArrayList<DetectedObject> objectsAL = new ArrayList<DetectedObject>();
		
		double MAX_TOL = 1000.00;
		// This value is the maximum permissible error between actual and estimated area.
		double MIN_AREA = 50.00;
		// We need this to be high enough to get rid of things that are too small too
		// have a definite shape.  Otherwise, they will end up as ellipse false positives.

	    // the first command line parameter must be file name of binary (black-n-white) image
		
		ResCoord result = new ResCoord(-1, -1, -1, -1);
		
	    if(srcLeft != null && srcRight != null)
	    {
	        IplImage dstLeft  = cvCreateImage( cvGetSize(srcLeft), 8, 3 );
	        IplImage dstRight  = cvCreateImage( cvGetSize(srcRight), 8, 3 );
	        CvMemStorage storage = cvCreateMemStorage(0);
	        
	        cvThreshold( srcLeft, srcLeft, 1, 255, CV_THRESH_BINARY );
	        cvThreshold( srcRight, srcRight, 1, 255, CV_THRESH_BINARY );
	        //
	        // Invert the image such that white is foreground, black is background.
	        // Dilate to get rid of noise.
	        //
	        
	        CvSeq contour = new CvContour();
	        cvXorS(srcLeft, cvScalar(255, 0, 0, 0), srcLeft, null);
	        cvDilate(srcLeft, srcLeft, null, 2);
	        cvFindContours( srcLeft, storage, contour, Loader.sizeof(CvContour.class), CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));
	        cvZero( dstLeft );

	        CvSeq contourBiggest = null;
	        double areaBiggest = 0;
	        for( ; contour != null && !contour.isNull(); contour = contour.h_next()){
	        	double actual_area = Math.abs(cvContourArea(contour, CV_WHOLE_SEQ, 0));
	            if (actual_area < MIN_AREA)
	                continue;
	        	if (actual_area > areaBiggest) {
	        		contourBiggest = contour;
	        		areaBiggest = actual_area;
	        	}
	        	
	        }
	        
	        if (contourBiggest != null) {
	            double actual_area = Math.abs(cvContourArea(contourBiggest, CV_WHOLE_SEQ, 0));
	            System.out.println("actual area: " + actual_area);


	            //
	            // Assuming the axes of the ellipse are vertical/perpendicular.
	            //
	            CvRect rect = null;
	            if (contourBiggest instanceof CvContour) {
	            	rect = ((CvContour) contourBiggest).rect();
				}
	            else {
	            	System.out.println("cvboundingrect");
	            	rect = cvBoundingRect(contourBiggest,0);
				}
	            int A = rect.width() / 2; 
	            int B = rect.height() / 2;
	            double estimated_area = Math.PI * A * B;
	            double error = Math.abs(actual_area - estimated_area);
	            System.out.println("error: " + error);
	            if (error > MAX_TOL)
	                System.err.println("Fehlertoleranz überschritten!\n");;
	            objectsAL.add(new DetectorAdjusted().new DetectedObject(rect.x() + A, rect.y() + B, rect.width(), rect.height()));
	            System.out.println("center x: " + (rect.x() + A)  + " y: " + (rect.y() + B) + " A: " + A + " B: " + B + "\n");

	            CvScalar color = CV_RGB( Math.random() * 255, Math.random() * 255, Math.random() * 255 );
	            cvDrawContours( dstLeft, contourBiggest, color, color, -1, CV_FILLED, 8, cvPoint(0,0));
	            
	            result.xLeft = (rect.x() + A);
	            result.yLeft = (rect.y() + B);
	        } else {
	        	System.out.println("No Result (Left)\n");
	        }
	        cvSaveImage("pics/finalLeft.png", dstLeft);
	        
	        
	        
	        // RIGHT
	        contour = new CvContour();
	        cvXorS(srcRight, cvScalar(255, 0, 0, 0), srcRight, null);
	        cvDilate(srcRight, srcRight, null, 2);
	        cvFindContours( srcRight, storage, contour, Loader.sizeof(CvContour.class), CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));
	        cvZero( dstRight );
	        
	        contourBiggest = null;
	        areaBiggest = 0;
	        for( ; contour != null && !contour.isNull(); contour = contour.h_next()){
	        	double actual_area = Math.abs(cvContourArea(contour, CV_WHOLE_SEQ, 0));
	            if (actual_area < MIN_AREA)
	                continue;
	        	if (actual_area > areaBiggest) {
	        		contourBiggest = contour;
	        		areaBiggest = actual_area;
	        	}
	        	
	        }
	        
	        if (contourBiggest != null) {
	            double actual_area = Math.abs(cvContourArea(contourBiggest, CV_WHOLE_SEQ, 0));
	            System.out.println("actual area: " + actual_area);


	            //
	            // FIXME:
	            // Assuming the axes of the ellipse are vertical/perpendicular.
	            //
	            CvRect rect = null;
	            if (contourBiggest instanceof CvContour) {
	            	rect = ((CvContour) contourBiggest).rect();
				}
	            else {
	            	System.out.println("cvboundingrect");
	            	rect = cvBoundingRect(contourBiggest,0);
				}
	            int A = rect.width() / 2; 
	            int B = rect.height() / 2;
	            double estimated_area = Math.PI * A * B;
	            double error = Math.abs(actual_area - estimated_area);
	            System.out.println("error: " + error);
	            if (error > MAX_TOL)
	                System.err.println("Fehlertoleranz überschritten!\n");;
	            objectsAL.add(new DetectorAdjusted().new DetectedObject(rect.x() + A, rect.y() + B, rect.width(), rect.height()));
	            System.out.println("center x: " + (rect.x() + A)  + " y: " + (rect.y() + B) + " A: " + A + " B: " + B + "\n");

	            CvScalar color = CV_RGB( Math.random() * 255, Math.random() * 255, Math.random() * 255 );
	            cvDrawContours( dstRight, contourBiggest, color, color, -1, CV_FILLED, 8, cvPoint(0,0));
	            
	            result.xRight = 2047 - (rect.x() + A);
	            result.yRight = 1231 - (rect.y() + B);
	        } else {
	        	System.out.println("No Result (Right)\n");
	        }
	        cvSaveImage("pics/finalRight.png", dstRight);
	        
	    }
	    
	    return result;
//	    objects = new DetectedObject[objectsAL.size()];
//	    for (int i = 0; i < objects.length; i++) {
//			objects[i] = objectsAL.get(i);
//		}
//	    return objects;
	}
	
	private static void calculateFromResCoord(ResCoord coord) {
		final double CM2PX = 115.0;
		final double AC2CM = 15.9;
		
		final double lx1 = -1;
		final double ly1 = 0;
		double lx2 = (((coord.xLeft - 1024) / CM2PX) / AC2CM) - 1;
		System.out.println(lx2);
		final double ly2 = 1;
		
		final double rx1 = 1;
		final double ry1 = 0;
		double rx2 = (((coord.xRight - 1024) / CM2PX) / AC2CM) + 1;
		System.out.println(rx2);
		final double ry2 = 1;
		
		double la = ly1-ly2;
//		System.out.println(la);
		double lb = lx2-lx1;
//		System.out.println(lb);
		double lc = lx1*ly2 - lx2*ly1;
//		System.out.println(lc);
		
		double ra = ry1-ry2;
//		System.out.println(ra);
		double rb = rx2-rx1;
//		System.out.println(rb);
		double rc = rx1*ry2 - rx2*ry1;
//		System.out.println(rc);
		
		
		double y = ( ra / la * lc - rc ) / (rb - ra / la * lb );
		double x = (lb * y + lc) / (-la);
		
		System.out.println("X = " + x + ", Y = " + y);
		
		
		
		double dist = Math.sqrt((x*x)+(y*y));
		System.out.println("rawdist="+dist);
		System.out.println("Distance = approx. " + dist * AC2CM + "cm");
		double angH = ((Math.asin(x / dist)) / (Math.PI * 2)) * 360;
		System.out.println("Angle (Horizontal) = " + angH + "°");
		double angV = (Math.atan(((616 - ((coord.yLeft+coord.yRight)/2)) / CM2PX) / AC2CM) / (Math.PI * 2) * 360);
		System.out.println("Angle (Vertical) = " + angV + "°");
	}

}
