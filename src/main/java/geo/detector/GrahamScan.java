package geo.detector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class GrahamScan {
    private Stack<Point> hull = new Stack<Point>();

    public GrahamScan(Point[] pts) {
        int n = pts.length; //копия числа вершин
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            points[i] = pts[i];
        }
        Arrays.sort(points);
        //сортируем по полярному углу
        Arrays.sort(points, 1, n, points[0].polarOrder());

        hull.push(points[0]);       // p[0] стартовая точка

        // найти индекс k1 первой точки, не равной points[0]
        int k1;
        for (k1 = 1; k1 < n; k1++)
            if (!points[0].equals(points[k1])) break;
        if (k1 == n) return;

        // найти индекс k2 первой точки, не коллинеарной с points[0] и points[k1]
        int k2;
        for (k2 = k1 + 1; k2 < n; k2++)
            if (Point.ccw(points[0], points[k1], points[k2]) != 0) break;
        hull.push(points[k2 - 1]);    // points[k2-1] вторая точка

        // алгоритм Грэхема
        for (int i = k2; i < n; i++) {
            Point top = hull.pop();
            while (Point.ccw(hull.peek(), top, points[i]) <= 0) {
                top = hull.pop();
            }
            hull.push(top);
            hull.push(points[i]);
        }

        assert isConvex();
    }

    public Iterable<Point> hull() {
        Stack<Point> s = new Stack<Point>();
        for (Point p : hull) s.push(p);
        return s;
    }

    public ArrayList<Point> hullModified() {
        ArrayList<Point> s = new ArrayList<>();
        for (Point p : hull) s.add(p);
        return s;
    }

    public int number(){ //количество точек в готовом многоугольнике
        int  n2 = 0;
        for (Point p : hull) ++n2;
        return n2;
    }
    //проверить, является ли граница многоугольника строго выпуклой
    private boolean isConvex() {
        int n = hull.size();
        if (n <= 2) return true;

        Point[] points = new Point[n];
        int k = 0;
        for (Point p : hull()) {
            points[k++] = p;
        }

        for (int i = 0; i < n; i++) {
            if (Point.ccw(points[i], points[(i + 1) % n], points[(i + 2) % n]) <= 0) {
                return false;
            }
        }
        return true;
    }
}