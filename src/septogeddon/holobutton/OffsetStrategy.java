package septogeddon.holobutton;

public class OffsetStrategy {
	
	public static double getNearestBoundingBox(double length) {
		if (length < 1) return 0;
		double gapLimit = 30;
		double limit = 0;
		if (gapLimit * length > 360) {
			limit = 360d;
		} else {
			while (limit / length <= gapLimit) {
				limit+=5;
			}
		}
		return limit/(length*limit + limit/length);
	}
	public static float getTargetOffset(double index, double length) {
		if (length <= 1) return 0;
		double gapLimit = 30;
		double limit = 0;
		if (gapLimit * length > 360) {
			limit = 360d;
		} else {
			while (limit / length <= gapLimit) {
				limit+=5;
			}
		}
		return (float)((limit / length) * ((length/2d)-index) - (limit/length)/2d);
	}
	
}
