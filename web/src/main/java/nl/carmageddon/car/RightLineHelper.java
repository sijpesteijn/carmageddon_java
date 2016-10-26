package nl.carmageddon.car;

import nl.carmageddon.domain.Dimension;
import nl.carmageddon.domain.Line;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static nl.carmageddon.MatUtils.getCenterPoint;

/**
 * @author Gijs Sijpesteijn
 */
public class RightLineHelper {

    private Dimension cameraDimension;

    public RightLineHelper(Dimension cameraDimension) {
        this.cameraDimension = cameraDimension;
    }

    public Line findRightLine(List<Point> allVerticalPoints) {
        double center_x = getCenterPoint(allVerticalPoints).x;
        List<Point> rightPoints = null;
        if (center_x > (cameraDimension.getWidth()/2)) { // liggen alle punten rechts van het midden
            rightPoints = getPointsLeftOfCenter(allVerticalPoints, center_x);
        } else { // geef alle punten rechts van het midden
            rightPoints = allVerticalPoints.stream().sorted((p1, p2) -> (p1.y < p2.y) ? -1 : ((p1.y == p2.y) ? 0 : 1))
                                          .filter(p -> p.x > cameraDimension.getWidth()/2)
                                          .collect(toList());
            if (rightPoints.size() > 0) {
                double center_left_x = getCenterPoint(rightPoints).x;
                rightPoints = getPointsLeftOfCenter(rightPoints, center_left_x);
            }
        }
        if (rightPoints.size() > 0) {
            Point center = getCenterPoint(rightPoints);
            if (center != null)
                return new Line(new Point(center.x, 0), new Point(center.x, 240));
        }
        return null;
    }

    private List<Point> getPointsLeftOfCenter(List<Point> allVerticalPoints, double center_x) {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < allVerticalPoints.size(); i = i + 2) {
            if (i+1 < allVerticalPoints.size()) {
                Point start = allVerticalPoints.get(i);
                Point end = allVerticalPoints.get(i + 1);
                Point center = getCenterPoint(new ArrayList<Point>() {{
                    add(start);
                    add(end);
                }});
                if (center.x <= center_x) {
                    points.add(start);
                    points.add(end);
                }
            }
        }
        return points;
    }
}
