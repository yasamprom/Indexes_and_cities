import java.io.*;
import java.util.*;

public class Main {
    public static HashMap<String, ArrayList<Point>> Points = new HashMap<>();
    public static HashMap<Integer, Point> Requests = new HashMap<>();
    public static HashMap<String, ArrayList<Point>> Hulls = new HashMap<>();


    public static void readFile(String name){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader (name));
            String line = reader.readLine();
            while (line != null) {
                String[] data = line.split("\\s+");
                //String[] data = line.split(" ");

                if (data.length == 4){
                    float latt = Float.parseFloat(data[1]);
                    float longt = Float.parseFloat(data[2]);
                    String index = data[3];
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
                else{
                    int id = Integer.parseInt(data[0]);
                    float latt = Float.parseFloat(data[1]);
                    float longt = Float.parseFloat(data[2]);
                    Point newPoint = new Point(latt, longt);
                    Requests.put(id, newPoint);
                    System.out.println("added");
                }
                line = reader.readLine();
            }
        }
        catch (IOException e){
            System.out.println("Reading file error. Main/ReadFile.");
        }
    }


    public static void BuildHulls(){
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

    public static void Answer(){
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("Result.txt", true));
        }
        catch (Exception e){
            System.out.println("Open Result.txt file error. Main/Answer");
        }

        for (Integer key: Requests.keySet()){
            Point pt = Requests.get(key);
            for (String key1: Hulls.keySet()){
                if (PointInPolygon(pt.x(), pt.y(), Hulls.get(key1)) == true){
                    try {
                        writer.write(key + " " + key1 + "\n");
                    }
                    catch (Exception e){
                        System.out.println("Writing in file error. Main/Answer");
                    }
                    break;
                }
            }
        }
        try {
            writer.close();
        }
        catch (Exception e){}
    }



    public static void main(String[] args) {
        readFile("geo_tag2.tsv");
        //readFile("a.txt");
        BuildHulls();
        Answer();
    }
}
