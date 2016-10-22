package nl.carmageddon.service;

import nl.carmageddon.domain.Dimension;
import nl.carmageddon.domain.Line;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static nl.carmageddon.service.MatUtils.getCenterPoint;

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
        List<Point> rightPoints = new ArrayList<>();
        if (center_x < (cameraDimension.getWidth()/2) + 20) {
            for (int i = 0; i < allVerticalPoints.size(); i = i + 2) {
                Point start = allVerticalPoints.get(i);
                Point end = allVerticalPoints.get(i + 1);
                Point center = getCenterPoint(new ArrayList<Point>() {{ add(start); add(end);}});
                if (center.x >= center_x - 5) {
                    rightPoints.add(start);
                    rightPoints.add(end);
                }
            }
        } else {
            rightPoints = allVerticalPoints.stream().sorted((p1, p2) -> (p1.y < p2.y) ? -1 : ((p1.y == p2.y) ? 0 : 1))
                                          .filter(p -> p.x > center_x)
                                          .collect(toList());
        }
        Point center = getCenterPoint(rightPoints);
        if (center != null)
            return new Line(new Point(center.x, 0), new Point(center.x, 240));

        return null;
    }
}
