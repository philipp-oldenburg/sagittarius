import org.bytedeco.javacpp.opencv_core.IplImage;

public class Vision {
	public IplImage left;
	public IplImage right;
	
	public Vision(IplImage left, IplImage right) {
		this.left = left;
		this.right = right;
	}
}