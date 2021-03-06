package hcay.pui.com.recognizer;

/**
 * Point class containing x, y coordinates, along with an optional strokeID to be used for the recognizer.
 *
 * @author Hyun Seo Chung
 * @author Andy Ybarra
 */
public class Point {

    public double x, y;
    public int strokeID;

    public Point(double x, double y, int strokeID) {
        this.x = x;
        this.y = y;
        this.strokeID = strokeID;
    }

    public Point(double x, double y) {
        this(x, y, -1);
    }

    @Override
    public String toString() {
    	return String.format("{x=%.3f,y=%.3f,id=%d}", x, y, strokeID);
    }

    @Override
    public boolean equals(Object b){
        return this.x == ((Point)b).x && this.y == ((Point)b).y;
    }
}
