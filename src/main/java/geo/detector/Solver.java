package geo.detector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class Solver {
    private HashMap<String, List<Point>> points = new HashMap<>();
    private HashMap<Integer, Point> requests = new HashMap<>();
    private HashMap<String, List<Point>> hulls = new HashMap<>();

    private static boolean pointInPolygon(float x, float y, List<Point> points) {
        Point p0 = points.get(points.size() - 1);
        Point p1 = points.get(0);
        boolean below0 = (y <= p0.y());
        boolean hit = false;
        for (int i = 1; i < points.size(); i++) {
            boolean below1 = (y <= p1.y());

            if (below0 != below1) {
                if (((p1.y() - y) * (p0.x() - p1.x()) >= (p1.x() - x) * (p0.y() - p1.y())) == below1) {
                    hit = !hit;
                }
            }
            p0 = p1;
            p1 = points.get(i);
            below0 = below1;
        }
        return hit;
    }

    public void addPoint(String geoTag, float latitude, float longitude) {
        if (points.containsKey(geoTag)) {
            List<Point> newList = points.get(geoTag);
            newList.add(new Point(latitude, longitude));
            points.replace(geoTag, points.get(geoTag), newList);
        } else {
            ArrayList<Point> addList = new ArrayList<>();
            addList.add(new Point(latitude, longitude));
            points.put(geoTag, addList);
        }
    }

    public void addRequest(int id, float latitude, float longitude) {
        Point newPoint = new Point(latitude, longitude);
        requests.put(id, newPoint);
    }

    public Solver readFromFile(String name) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(name))) {
            String line = reader.readLine();
            while (line != null) {
                String[] data = line.split("\\s+");
                //String[] data = line.split(" ");
                if (data.length == 4) {
                    float latt = Float.parseFloat(data[1]);
                    float longt = Float.parseFloat(data[2]);
                    String index = data[3];
                    addPoint(index, latt, longt);
                } else {
                    int id = Integer.parseInt(data[0]);
                    float latt = Float.parseFloat(data[1]);
                    float longt = Float.parseFloat(data[2]);
                    addRequest(id, latt, longt);
                }
                line = reader.readLine();
            }
        }
        return this;
    }

    private void buildHulls() {
        for (String key : points.keySet()) {
            Point[] pts = new Point[points.get(key).size()];
            for (int i = 0; i < points.get(key).size(); i++) {
                pts[i] = points.get(key).get(i);
            }
            GrahamScan graham = new GrahamScan(pts);
            hulls.put(key, Collections.unmodifiableList(graham.hull()));
        }
    }

    public Map<Integer, String> buildAnswer() {
        buildHulls();
        Map<Integer, String> result = new HashMap<>();
        for (Integer key : requests.keySet()) {
            Point pt = requests.get(key);
            for (String key1 : hulls.keySet()) {
                if (pointInPolygon(pt.x(), pt.y(), hulls.get(key1))) {
                    result.put(key, key1);
                    break;
                }
            }
        }
        return result;
    }
}
