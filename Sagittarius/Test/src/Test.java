import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;

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
		Highgui.imwrite("pics/out.jpeg", red_image);
		
		//detectEllipse(red_image);
	}
	
	
	
	
	

//	private static void detectEllipse(Mat red_image) {
//		Mat dst  = CxCore.cvCreateImage( getSize("test"), 8, 3 );
//        CvMemStorage* storage = cvCreateMemStorage(0);
//        CvSeq* contour = 0;    
//        cvThreshold( src, src, 1, 255, CV_THRESH_BINARY );
//        //
//        // Invert the image such that white is foreground, black is background.
//        // Dilate to get rid of noise.
//        //
//        cvXorS(src, cvScalar(255, 0, 0, 0), src, NULL);
//        cvDilate(src, src, NULL, 2);    
//        cvFindContours( src, storage, &contour, sizeof(CvContour), CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));
//        cvZero( dst );
//
//        for( ; contour != 0; contour = contour->h_next )
//        {
//            double actual_area = fabs(cvContourArea(contour, CV_WHOLE_SEQ, 0));
//            if (actual_area < MIN_AREA)
//                continue;
//
//            //
//            // FIXME:
//            // Assuming the axes of the ellipse are vertical/perpendicular.
//            //
//            CvRect rect = ((CvContour *)contour)->rect;
//            int A = rect.width / 2; 
//            int B = rect.height / 2;
//            double estimated_area = M_PI * A * B;
//            double error = fabs(actual_area - estimated_area);    
//            if (error > MAX_TOL)
//                continue;    
//            printf
//            (
//                 "center x: %d y: %d A: %d B: %d\n",
//                 rect.x + A,
//                 rect.y + B,
//                 A,
//                 B
//            );
//
//            CvScalar color = CV_RGB( rand() % 255, rand() % 255, rand() % 255 );
//            cvDrawContours( dst, contour, color, color, -1, CV_FILLED, 8, cvPoint(0,0));
//        }
//
//        cvSaveImage("coins.png", dst, 0);
//	}

}
