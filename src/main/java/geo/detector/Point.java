package geo.detector;

import java.util.Comparator;

//описание точки
public class Point implements Comparable<Point>{

    private final float x;    // x координата
    private final float y;    // y координата

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public float x() {
        return x;
    }

    public float y() {
        return y;
    }
    public int compareTo(Point that) {
        if (this.y < that.y) return -1;
        if (this.y > that.y) return +1;
        if (this.x < that.x) return -1;
        if (this.x > that.x) return +1;
        return 0;
    }
    /**
     * Возвращает 1, если a→b→c - поворот против часовой стрелки
     */
    public static int ccw(Point a, Point b, Point c) {
        float area2 = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
        if (area2 < 0) return -1;
        else if (area2 > 0) return +1;
        else return 0;
    }

    public Comparator<Point> polarOrder() {
        return new PolarOrder();
    }

    private class PolarOrder implements Comparator<Point> {
        public int compare(Point q1, Point q2) {
            float dx1 = q1.x - x;
            float dy1 = q1.y - y;
            float dx2 = q2.x - x;
            float dy2 = q2.y - y;
            if      (dy1 >= 0 && dy2 < 0) return -1;    // q1 сверху; q2 снизу
            else if (dy2 >= 0 && dy1 < 0) return +1;    // q1 снизу; q2 сверху
            else if (dy1 == 0 && dy2 == 0) {            // коллинеарные
                if      (dx1 >= 0 && dx2 < 0) return -1;
                else if (dx2 >= 0 && dx1 < 0) return +1;
                else                          return  0;
            }
            else return -ccw(Point.this, q1, q2);     // обе сверху или снизу
        }
    }
}