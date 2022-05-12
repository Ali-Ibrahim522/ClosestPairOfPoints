import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.Collections.sort;

public class ClosestPairOfPoints {
    private final PointXComparator sortX;
    private final PointYComparator sortY;
    private final String fileName;
    private Point[] points;

    public ClosestPairOfPoints(String fileName) {
        sortX = new PointXComparator();
        sortY = new PointYComparator();
        build10k();
        this.fileName = fileName;
    }

    public void build10k() {
        try {
            PrintWriter writer = new PrintWriter("10kdata.txt", StandardCharsets.UTF_8);
            writer.println("10000");
            Random rand = new Random();
            for (int i = 0; i < 10000; i++) {
                writer.println(rand.nextDouble(300) + " " + rand.nextDouble(300));
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void findClosestPair() {
        initializeData();
        closestPair(0, points.length - 1);
        bruteForce();
    }

    private void bruteForce() {
        ArrayList<Double> distances = new ArrayList<>();
        for (int i = 0; i < points.length - 1; i++) {
            for (int j = i + 1; j < points.length; j++) {
                distances.add(Math.sqrt(Math.pow((points[i].getX() - points[j].getX()), 2) +
                        Math.pow((points[i].getY() - points[j].getY()), 2)));
            }
        }
        sort(distances);
        for (int i = 0; i < 2; i++) {
            System.out.printf("%.4f %n", distances.get(i));
        }
    }

    public double[] closestPair(int start, int end) {
        double[] delta = new double[2];
        delta[0] = Double.MAX_VALUE;
        delta[1] = Double.MAX_VALUE;
        double distance;
        if (end - start < 3) {
            for (int i = start; i <= end - 1; i++) {
                for (int j = i + 1; j <= end; j++) {
                    distance = Math.sqrt(Math.pow((points[i].getX() - points[j].getX()), 2) +
                            Math.pow((points[i].getY() - points[j].getY()), 2));
                    if (distance < delta[0]) {
                        double temp = delta[0];
                        delta[0] = distance;
                        delta[1] = temp;
                    } else if (distance < delta[1] && Double.compare(distance, delta[0]) != 0) {
                        delta[1] = distance;
                    }
                    Arrays.sort(delta);
                }
            }
            System.out.println("[" + delta[0] + ", " + "]");
        } else {
            int mid = (start + end) / 2;
            double[] closestLeft = closestPair(start, mid);
            double[] closestRight = closestPair(mid + 1, end);
            double l = points[mid].getX();
            for (int i = 0; i < 2; i++) {
                if (closestLeft[i] < delta[0]) {
                    double temp = delta[0];
                    delta[0] = closestLeft[i];
                    delta[1] = temp;
                } else if (closestLeft[i] < delta[1] && Double.compare(closestLeft[i], delta[0]) != 0) delta[1] = closestLeft[i];
                Arrays.sort(delta);
            }
            for (int i = 0; i < 2; i++) {
                if (closestRight[i] < delta[0]) {
                    double temp = delta[0];
                    delta[0] = closestRight[i];
                    delta[1] = temp;
                } else if (closestRight[i] < delta[1] && Double.compare(closestRight[i], delta[0]) != 0) delta[1] = closestRight[i];
                Arrays.sort(delta);
            }
            ArrayList<Point> deltaRange = new ArrayList<>();
            for (int i = start; i <= end; i++) {
                if (points[i].getX() <= l + delta[0] && points[i].getX() >= l - delta[0]) {
                    deltaRange.add(points[i]);
                }
            }
            double nextDelta = delta[1];
            deltaRange.sort(sortY);
            Point from;
            Point to;
            for (int i = 0; i < deltaRange.size() - 1; i++) {
                from = deltaRange.get(i);
                for (int j = i + 1; j < Math.min(7, deltaRange.size()); j++) {
                    to = deltaRange.get(j);
                    distance = Math.sqrt(Math.pow((from.getX() - to.getX()), 2) +
                            Math.pow((from.getY() - to.getY()), 2));
                    if (distance < delta[0]) {
                        double temp = delta[0];
                        delta[0] = distance;
                        delta[1] = temp;
                    } else if (distance < delta[1] && Double.compare(distance, delta[0]) != 0) {
                        delta[1] = distance;
                    }
                    Arrays.sort(delta);
                }
            }
            deltaRange = new ArrayList<>();
            for (int i = start; i <= end; i++) {
                if (points[i].getX() <= l + nextDelta && points[i].getX() >= l - nextDelta) {
                    deltaRange.add(points[i]);
                }
            }
            deltaRange.sort(sortY);
            for (int i = 0; i < deltaRange.size() - 1; i++) {
                from = deltaRange.get(i);
                for (int j = i + 1; j < Math.min(7, deltaRange.size()); j++) {
                    to = deltaRange.get(j);
                    distance = Math.sqrt(Math.pow((from.getX() - to.getX()), 2) +
                            Math.pow((from.getY() - to.getY()), 2));
                    if (distance < delta[0]) {
                        double temp = delta[0];
                        delta[0] = distance;
                        delta[1] = temp;
                    } else if (distance < delta[1] && Double.compare(distance, delta[0]) != 0) {
                        delta[1] = distance;
                    }
                    Arrays.sort(delta);
                }
            }
        }
        System.out.printf("D[" + start + "," + end + "]: " + "%.4f", delta[0]);
        System.out.println(", " + delta[1]);
        return delta;
    }

    public void initializeData() {
        File file = new File(fileName);
        try {
            Scanner in = new Scanner(file);
            int n = in.nextInt();
            points = new Point[n];
            for (int i = 0; i < n; i++) {
                points[i] = new Point(in.nextDouble(), in.nextDouble());
            }
            Arrays.sort(points, sortX);
            in.close();
        } catch (FileNotFoundException ex) {
            System.out.println("File not found");
        }
    }
}
