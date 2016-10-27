package nl.carmageddon.car;

import nl.carmageddon.domain.Dimension;
import nl.carmageddon.domain.Line;
import nl.carmageddon.domain.RoadLookoutView;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

import static nl.carmageddon.MatUtils.getCenterPoint;

/**
 * @author Gijs Sijpesteijn
 */
public class FinishLineHelper {
    private int prevNrOfFinishLines = 0;
    private Dimension cameraDimension;

    public FinishLineHelper(Dimension cameraDimension) {
        this.cameraDimension = cameraDimension;
    }

    public boolean pastFirstAndCloserThan(RoadLookoutView view, int minDistance) {
        if (isPastFirstFinsihLine(view)) {
            Line finishLine = view.getFinishLines().get(0);
            Point center = getCenterPoint(new ArrayList<Point>() {
                {
                    add(finishLine.getStart());
                    add(finishLine.getEnd());
                }
            });
            if (cameraDimension.getHeight() - center.y < minDistance) {
                return true;
            }
        }
        this.prevNrOfFinishLines = view.getFinishLines() != null ? view.getFinishLines().size() : 0;
        return false;
    }

    public boolean isPastFirstFinsihLine(RoadLookoutView roadLookoutView) {
        return roadLookoutView.getFinishLines() != null && roadLookoutView.getFinishLines().size() <
                                                           prevNrOfFinishLines;
    }

    public List<Line> findFinishLines(List<Point> horizontalPoints) {
        List<Line> lines = new ArrayList<>();
        if (horizontalPoints.size() == 0) {
            return lines;
        }
        double center_y = getCenterPoint(horizontalPoints).y;
        if (percentageOfPointsNearCenter(horizontalPoints, center_y, 10) > 80) {
            lines.add(new Line(new Point(0, center_y), new Point(cameraDimension.getWidth(), center_y)));
        } else {
            List<Point> upper = new ArrayList<>();
            List<Point> lower = new ArrayList<>();
            for (int i = 0; i < horizontalPoints.size(); i = i + 2) {
                Point start = horizontalPoints.get(i);
                Point end = horizontalPoints.get(i + 1);
                if (start.y >= center_y && end.y >= center_y) {
                    lower.add(start);
                    lower.add(end);
                }
                else {
                    upper.add(start);
                    upper.add(end);
                }
            }
            if (upper.size() > 0) {
                Point upperCenter = getCenterPoint(upper);
                if (upperCenter != null)
                    lines.add(new Line(new Point(0, upperCenter.y),
                                       new Point(cameraDimension.getWidth(), upperCenter.y)));
            }
            if (lower.size() > 0) {
                Point lowerCenter = getCenterPoint(lower);
                if (lowerCenter != null)
                    lines.add(new Line(new Point(0, lowerCenter.y),
                                       new Point(cameraDimension.getWidth(), lowerCenter.y)));
            }
        }
        return lines;
    }

    private int percentageOfPointsNearCenter(List<Point> horizontalPoints, double center_y, int range) {
        long count = horizontalPoints.stream().filter(point -> center_y - point.y < range && point.y - center_y < range)
                                     .count();
        int percentage = (int) (count*100/horizontalPoints.size());
        System.out.println("Percentage " + percentage);
        return percentage;
    }

    public void reset() {
        this.prevNrOfFinishLines = 0;
    }
}
