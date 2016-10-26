package nl.carmageddon.car;

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

    public boolean finishLinesCloserThan(RoadLookoutView view, int height, int minDistance) {
        if (isPastFirstFinsihLine(view)) {
             Line finishLine = view.getFinishLines().get(0);
            Point center = getCenterPoint(new ArrayList<Point>() {
                {
                    add(finishLine.getStart());
                    add(finishLine.getEnd());
                }
            });
            if (height - center.y < minDistance) {
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
        List<Point> upper = new ArrayList<>();
        List<Point> lower = new ArrayList<>();
        for(int i = 0;i < horizontalPoints.size();i=i+2) {
            Point start = horizontalPoints.get(i);
            Point end = horizontalPoints.get(i+1);
            if (start.y >= center_y - 5 && end.y >= center_y -5) {
                lower.add(start);
                lower.add(end);
            } else {
                upper.add(start);
                upper.add(end);
            }
        }
        if (upper.size() > 0) {
            Point upperCenter = getCenterPoint(upper);
            if (upperCenter != null)
                lines.add(new Line(new Point(0, upperCenter.y), new Point(320, upperCenter.y)));
        }
        if (lower.size() > 0) {
            Point lowerCenter = getCenterPoint(lower);
            if (lowerCenter != null)
                lines.add(new Line(new Point(0, lowerCenter.y), new Point(320, lowerCenter.y)));
        }
        return lines;
    }

    public void reset() {
        this.prevNrOfFinishLines = 0;
    }
}
