package nl.carmageddon.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
public class DetermineAverageLineTest {


    int width = 300;
    int height = 150;
    //https://www.researchgate.net/publication/282921028_Steering_angle_estimation_for_autonomous_vehicle_navigation_using_hough_and_Euclidean_transform
    Random rand = new SecureRandom();

    @Test
    public void averageLine() {

        final List<Point> roadLinePair = createRoadLinePair();
        //roadLinePair.addAll(createRoadLinePair());

        List<Point> median = getMedianPoints(roadLinePair);

        System.out.println(median);

    }

    private List<Point> getMedianPoints(List<Point> roadLinePair) {
        final Double averageX = roadLinePair.stream().map(p -> p.x).collect(Collectors.averagingInt(x -> x.intValue()));

        System.out.println(averageX);

        final List<Point> rightLines = roadLinePair.stream().sorted((p1, p2) -> p2.y.compareTo(p1.y))
                .filter(p -> p.x > averageX.intValue())
                .collect(Collectors.toList());

        final List<Point> leftLines = roadLinePair.stream().sorted((p1, p2) -> p2.y.compareTo(p1.y))
                .filter(p -> p.x <= averageX.intValue())
                .collect(Collectors.toList());

        List<Point> median = new ArrayList<>();
        if (rightLines.size() < leftLines.size()) {
            for (int i = 0; i < rightLines.size(); i++) {
                final Point rP = rightLines.get(i);
                final Point lP = leftLines.get(i);
                median.add(new Point((rP.x + lP.x) / 2, (rP.y + lP.y) / 2));
            }
        } else {
            for (int i = 0; i < leftLines.size(); i++) {
                final Point rP = rightLines.get(i);
                final Point lP = leftLines.get(i);
                median.add(new Point((rP.x + lP.x) / 2, (rP.y + lP.y) / 2));
            }
        }
        return median;
    }


    public List<Point> createRoadLinePair() {
        List<Point> points = new ArrayList<>();
        {// left line
            final Point lt = leftTopCorner();
            final Point lb = leftBottomCorner(lt);
            points.add(lt);
            points.add(lb);
        }

        {// left line
            final Point rt = rightTopCorner();
            final Point rb = rightBottomCorner(rt);
            points.add(rt);
            points.add(rb);
        }

        return points;
    }

    private Point leftBottomCorner(Point ltc) {
        return new Point(rand.nextInt(ltc.x), ltc.y + rand.nextInt(height / 2));
    }

    private Point leftTopCorner() {
        return new Point(rand.nextInt(width / 2), rand.nextInt(height / 2));
    }

    private Point rightBottomCorner(Point rtc) {
        return new Point(rtc.x + rand.nextInt(width - rtc.x), rtc.y + rand.nextInt(height / 2));
    }

    private Point rightTopCorner() {
        return new Point(width / 2 + rand.nextInt(width / 2), rand.nextInt(height / 2));
    }

    public class Point {
        Integer x;
        Integer y;

        public Point(Integer x, Integer y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }
}
