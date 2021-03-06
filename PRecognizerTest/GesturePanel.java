import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class GesturePanel extends JPanel {

	private ArrayDeque<Stroke> strokes = new ArrayDeque<>();

	public GesturePanel() {
		setBackground(Color.LIGHT_GRAY);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
	    		strokes.add(new Stroke(new Point(e.getPoint().x, e.getPoint().y, strokes.size() + 1)));
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
            	Stroke currentStroke = strokes.getLast();
            	currentStroke.addPoint(new Point(e.getPoint().x, e.getPoint().y, strokes.size()));
                repaint();
            }
        });
	}

	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Stroke stroke : strokes) {
        	stroke.draw(g2d);
        }
    }

    public ArrayList<Point> getPoints() {
    	ArrayList<Point> points = new ArrayList<>();
    	for (Stroke stroke : strokes) {
    		points.addAll(stroke.points);
    	}
    	return points;
    }

    public void clear() {
    	strokes.clear();
    	repaint();
    }

	private class Stroke {

		private ArrayDeque<Point> points;

		public Stroke(ArrayDeque<Point> points) {
			this.points = points;
		}

		public Stroke(Point point) {
			this(new ArrayDeque<>());
			points.add(point);
		}

		public void addPoint(Point point) {
			points.add(point);
		}

		public void draw(Graphics2D g2d) {
			g2d.setColor(Color.BLACK);
			g2d.setStroke(new BasicStroke(8));

			Point previousPoint = null;

			for (Point point : points) {
				if (previousPoint != null) {
					g2d.draw(new Line2D.Double(previousPoint.x, previousPoint.y - 4, point.x, point.y - 4));
				}
	            previousPoint = point;
			}

			if (points.size() == 1) {
	            g2d.fill(new Ellipse2D.Double(previousPoint.x - 5, previousPoint.y - 8, 8, 8));
			}
		}
	}
}
