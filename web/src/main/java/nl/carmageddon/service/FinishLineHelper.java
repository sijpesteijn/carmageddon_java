package nl.carmageddon.service;

import nl.carmageddon.domain.Line;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

import static nl.carmageddon.service.MatUtils.getCenterPoint;

/**
 * @author Gijs Sijpesteijn
 */
public class FinishLineHelper {
    private boolean tooClose;

    private boolean finishLinesCloserThan(List<Line> finishLines, int height, int minDistance) {
        tooClose = false;
        finishLines.forEach(line -> {
            int distance = (int) ((line.getStart().y + line.getEnd().y) / 2);
            if (height - distance <  minDistance) {
                tooClose = true;
            }

        });
        return tooClose;
    }

    public List<Line> findFinishLines(List<Point> horizontalPoints) {
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
        List<Line> lines = new ArrayList<>();
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

}
