package hcay.pui.com.recognizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Recognizer {

    /** Number of points to use for the re-sampled path. */
    private static final int N = 32;
    private static ArrayList<Template> templates = new ArrayList<>();

    public Recognizer() {
        initializeTemplates();
    }

    private void initializeTemplates() {
        Gesture t = Gesture.T;
        templates.add(new Template(t, new ArrayList<>(Arrays.asList(
                new Point(30,7,1),
                new Point(103,7,1),
                new Point(66,7,2),
                new Point(66,87,2)
        ))));

        Gesture n = Gesture.N;
        templates.add(new Template(n, new ArrayList<>(Arrays.asList(
                new Point(177,92,1),new Point(177,2,1),
                new Point(182,1,2), new Point(246,95,2),
                new Point(247,87,3),new Point(247,1,3)
        ))));

        Gesture d = Gesture.D;
        templates.add(new Template(d, new ArrayList<>(Arrays.asList(
                new Point(345,9,1),new Point(345,87,1),
                new Point(351,8,2),new Point(363,8,2),
                new Point(372,9,2),new Point(380,11,2),
                new Point(386,14,2),new Point(391,17,2),
                new Point(394,22,2),new Point(397,28,2),
                new Point(399,34,2),new Point(400,42,2),
                new Point(400,50,2),new Point(400,56,2),
                new Point(399,61,2),new Point(397,66,2),
                new Point(394,70,2),new Point(391,74,2),
                new Point(386,78,2),new Point(382,81,2),
                new Point(377,83,2),new Point(372,85,2),
                new Point(367,87,2),new Point(360,87,2),
                new Point(355,88,2),new Point(349,87,2)
        ))));
    }

    public ArrayList<RecognizerResult> recognize(ArrayList<Point> points) {
        if (points.isEmpty()) {
            return new ArrayList<>();
        }

        points = normalize(points);
        double score = Double.POSITIVE_INFINITY;
        HashMap<Gesture, Double> result = new HashMap<>();

        for (Template template : templates) {
            double d = greedyCloudMatch(points, template.points);
            result.put(template.gesture, d);
            if (score > d) score = d;
        }

        if (score == Double.POSITIVE_INFINITY) {
            return new ArrayList<>();
        }

        ArrayList<RecognizerResult> convertedResult = new ArrayList<>(result.size());
        for (Map.Entry<Gesture, Double> r : result.entrySet()) {
            convertedResult.add(new RecognizerResult(r.getKey(), r.getValue()));
        }
        Collections.sort(convertedResult);

        return convertedResult;
    }

    private double greedyCloudMatch(ArrayList<Point> points, ArrayList<Point> templatePoints) {
        double e = 0.50;
        double step = Math.floor(Math.pow(N, 1 - e));
        double min = Double.POSITIVE_INFINITY;

        for (int i = 0; i < N; i += step) {
            double d1 = cloudDistance(points, templatePoints, i);
            double d2 = cloudDistance(templatePoints, points, i);
            min = Math.min(min, Math.min(d1, d2));
        }

        return min;
    }

    private double cloudDistance(ArrayList<Point> points, ArrayList<Point> templatePoints, int start) {
        if (points.size() != templatePoints.size()) {
            return Double.POSITIVE_INFINITY;
        }

        boolean[] matched = new boolean[N];
        double sum = 0;
        int i = start;
        do {
            int index = -1;
            double min = Double.POSITIVE_INFINITY;
            for (int j = 0; j < matched.length; j++) {
                if (!matched[j]) {
                    double d = getPointDistance(points.get(i), templatePoints.get(j));
                    if (d < min) {
                        min = d;
                        index = j;
                    }
                }
            }
            matched[index] = true;
            double weight = 1 - ((i - start + N) % N) / (double) N;
            sum += weight * min;
            i = (i + 1) % N;
        } while (i != start);

        return sum;
    }

    private ArrayList<Point> resample(ArrayList<Point> points) {
        double incrementLength = getTotalPathDistance(points) / (N - 1);

        ArrayList<Point> newPoints = new ArrayList<>(N);
        newPoints.add(points.get(0));

        double currentDistance = 0;

        for (int i = 1; i < points.size(); i++) {
            Point prev = points.get(i - 1);
            Point curr = points.get(i);
            if (prev.strokeID == curr.strokeID) {
                double d = getPointDistance(prev, curr);
                if ((currentDistance + d) >= incrementLength) {
                    double qx = prev.x + ((incrementLength - currentDistance) / d) * (curr.x - prev.x);
                    double qy = prev.y + ((incrementLength - currentDistance) / d) * (curr.y - prev.y);
                    Point q = new Point(qx, qy, curr.strokeID);
                    newPoints.add(q);
                    points.add(i, q);
                    currentDistance = 0;
                } else {
                    currentDistance += d;
                }
            }
        }

        if (newPoints.size() < N) {
            newPoints.add(points.get(points.size() - 1));
        }

        return newPoints;
    }

    private ArrayList<Point> scale(ArrayList<Point> points) {
        double minX, minY, maxX, maxY;
        minX = minY = Double.POSITIVE_INFINITY;
        maxX = maxY = 0;

        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            minX = Math.min(minX, point.x);
            minY = Math.min(minY, point.y);
            maxX = Math.max(maxX, point.x);
            maxY = Math.max(maxY, point.y);
        }

        double scale = Math.max(maxX - minX, maxY - minY);

        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            point.x = (point.x - minX) / scale;
            point.y = (point.y - minY) / scale;
        }

        return points;
    }

    private ArrayList<Point> translate(ArrayList<Point> points) {
        Point c = getCentroid(points);

        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            p.x -= c.x;
            p.y -= c.y;
        }

        return points;
    }

    private Point getCentroid(ArrayList<Point> points) {
        Point c = new Point(0, 0, -1);

        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            c.x += p.x;
            c.y += p.y;
        }

        c.x /= N;
        c.y /= N;

        return c;
    }

    private ArrayList<Point> normalize(ArrayList<Point> points) {
        points = resample(points);
        return translate(scale(points));
    }

    private double getPointDistance(Point p1, Point p2) {
        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }

    private double getTotalPathDistance(ArrayList<Point> points) {
        double distance = 0;
        for (int i = 1; i < points.size(); i++) {
            Point prev = points.get(i - 1);
            Point curr = points.get(i);
            if (prev.strokeID == curr.strokeID) {
                // add the distances between points for the same stroke
                distance += getPointDistance(prev, curr);
            }
        }
        return distance;
    }

    private class Template {

        public Gesture gesture;
        public ArrayList<Point> points;

        public Template(Gesture gesture, ArrayList<Point> points) {
            this.gesture = gesture;
            this.points = normalize(points);
        }

    }

}
