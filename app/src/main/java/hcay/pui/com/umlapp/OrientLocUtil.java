package hcay.pui.com.umlapp;

import android.util.Log;

import hcay.pui.com.recognizer.Point;
import hcay.pui.com.recognizer.Size;

/**
 * Created by andrasta on 11/13/15.
 */
public class OrientLocUtil {

    private static final int PADDING = 50;

    /**
     * Figures out whether the gesture was done vertically or horizontally and which axis
     * @param bounds
     * @return
     */
    public static GestureOrientation getGestureOrientation(Point[] bounds){
        // If the width is longer then we're horizontal...otherwise its vertical
        if(Math.abs(bounds[1].x-bounds[0].x) > Math.abs(bounds[2].y-bounds[3].y)) {
            // Looking a the strokeID we can determine which way the relationship is pointing
            // The left strokeid has to be less than the right strokeid
            if (bounds[0].strokeID < bounds[1].strokeID && bounds[0].strokeID <= bounds[3].strokeID && bounds[0].strokeID <= bounds[2].strokeID) {
                return GestureOrientation.LEFT_TO_RIGHT;
            } else {
                return GestureOrientation.RIGHT_TO_LEFT;
            }
        } else {
            // The top has to be less than the bottom strokeid
            if(bounds[2].strokeID < bounds[3].strokeID && bounds[2].strokeID <= bounds[1].strokeID && bounds[2].strokeID <= bounds[0].strokeID) {
                return GestureOrientation.TOP_TO_BOTTOM;
            } else {
                return GestureOrientation.BOTTOM_TO_TOP;
            }
        }
    }

    public static android.graphics.Point getPlacementLocation(GestureOrientation orientation, UMLObject src, UMLObject dst){

        android.graphics.Point location = null;
        if(orientation == GestureOrientation.LEFT_TO_RIGHT || orientation == GestureOrientation.RIGHT_TO_LEFT){
            // going down
            if(src.getLocation().y < dst.getLocation().y){
                if(orientation == orientation.LEFT_TO_RIGHT){
                    location = new android.graphics.Point(src.getLocation().x+src.getSize().getWidth(), src.getLocation().y+src.getSize().getHeight()/2);
                } else {
                    // RIGHT TO LEFT
                    location = new android.graphics.Point(dst.getLocation().x+dst.getSize().getWidth(), src.getLocation().y+src.getSize().getHeight()/2);
                }
            // going up
            } else if(src.getLocation().y > dst.getLocation().y) {
                if(orientation == orientation.LEFT_TO_RIGHT) {
                    location = new android.graphics.Point(src.getLocation().x+src.getSize().getWidth(), dst.getLocation().y+dst.getSize().getHeight()/2);
                } else {
                    // RIGHT TO LEFT
                    location = new android.graphics.Point(dst.getLocation().x+dst.getSize().getWidth(), dst.getLocation().y+dst.getSize().getHeight()/2);
                }
            //same level
            } else {
                // Default to either of the src or dst?
                if(orientation == GestureOrientation.LEFT_TO_RIGHT){
                    location = src.getLocation();
                } else {
                    location = dst.getLocation();
                }
            }
        } else if(orientation == GestureOrientation.TOP_TO_BOTTOM || orientation == GestureOrientation.BOTTOM_TO_TOP) {
            location = src.getLocation();
            location.x += src.getSize().getWidth() / 2;
            location.y += src.getSize().getHeight();
        }
//        } else if(orientation == GestureOrientation.BOTTOM_TO_TOP){
//            location = dst.getLocation();
//            location.x += dst.getSize().getWidth()/2;
//            location.y += dst.getSize().getHeight();
//        }
        return location;
    }

    public static Size getRelationshipSize(UMLObject src, UMLObject dst, GestureOrientation orientation){
        Size size = null;
        int height;
        int width;
        switch(orientation){

            case LEFT_TO_RIGHT:
                width = Math.abs(dst.getLocation().x-(src.getLocation().x+src.getSize().getWidth()));

                // Going up
                if(src.getLocation().y > dst.getLocation().y){
                    height = (src.getLocation().y+src.getSize().getHeight()/2)-(dst.getLocation().y+dst.getSize().getHeight()/2);
                // Going down
                } else if(src.getLocation().y < dst.getLocation().y) {
                    height = (dst.getLocation().y+dst.getSize().getHeight()/2)-(src.getLocation().y+src.getSize().getHeight()/2);
                } else {
                    // Default to 50?
                    height = 50;
                }
                size = new Size(width, height+PADDING);
                break;
            case RIGHT_TO_LEFT:
//                size = new Size(Math.abs(src.getLocation().x-(dst.getLocation().x+dst.getSize().getWidth())),
//                        (int)Math.abs((dst.getLocation().y+.5*dst.getSize().getHeight())-(.5*dst.getSize().getHeight())));
                width = Math.abs((src.getLocation().x-dst.getLocation().x+dst.getSize().getWidth()));

                // Going up
                if(src.getLocation().y > dst.getLocation().y){
                    height = (dst.getLocation().y+dst.getSize().getHeight()/2)-(src.getLocation().y + src.getSize().getHeight()/2);
                    // Going down
                } else if(src.getLocation().y < dst.getLocation().y) {
                    height = (src.getLocation().y+src.getSize().getHeight()/2)-(dst.getLocation().y+dst.getSize().getHeight()/2);
                } else {
                    // Default to 50?
                    height = 50;
                }

                size = new Size(width, height+PADDING);
                break;
            case TOP_TO_BOTTOM:
                break;
            case BOTTOM_TO_TOP:
                break;
        }
        return size;
    }
}
