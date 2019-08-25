package geo.detector;

import java.io.*;
import java.util.*;

public class Solver {
    private HashMap<String, ArrayList<Point>> Points = new HashMap<>();
    private HashMap<Integer, Point> Requests = new HashMap<>();
    private HashMap<String, ArrayList<Point>> Hulls = new HashMap<>();

    public void addPoint(String index, float latt, float longt) {
        if (Points.containsKey(index)){
            ArrayList<Point> newList = Points.get(index);
            newList.add(new Point(latt, longt));
            Points.replace(index, Points.get(index), newList);
            //System.out.println("new point in heap");
        }
        else{
            ArrayList<Point> addList = new ArrayList<>();
            addList.add(new Point(latt, longt));
            Points.put(index, addList);
            //System.out.println("new heap");
        }
    }

    public void addRequest(int id, float latt, float longt) {
        Point newPoint = new Point(latt, longt);
        Requests.put(id, newPoint);
    }

    public Solver readFromFile(String name) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader (name))){
            String line = reader.readLine();
            while (line != null) {
                String[] data = line.split("\\s+");
                //String[] data = line.split(" ");
                if (data.length == 4){
                    float latt = Float.parseFloat(data[1]);
                    float longt = Float.parseFloat(data[2]);
                    String index = data[3];
                    addPoint(index, latt, longt);
                }
                else{
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


    public void BuildHulls(){
        for (String key: Points.keySet()){
            Point[] pts = new Point[Points.get(key).size()];
            for (int i = 0; i < Points.get(key).size(); i++){
                pts[i] = Points.get(key).get(i);                                       //???
            }
            GrahamScan graham = new GrahamScan(pts);
            System.out.println(graham.hullModified().size());
            Hulls.put(key, graham.hullModified());
        }
    }

    public static boolean PointInPolygon(float x, float y, ArrayList<Point> points) {
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

    public Map<Integer, String> Answer(){
        BuildHulls();
        Map<Integer, String> result = new HashMap<>();

        for (Integer key: Requests.keySet()){
            Point pt = Requests.get(key);
            for (String key1: Hulls.keySet()){
                if (PointInPolygon(pt.x(), pt.y(), Hulls.get(key1)) == true){
                        result.put(key, key1);
                    break;
                }
            }
        }
        return result;
    }
}
