package nl.carmageddon.domain;

import org.opencv.core.Point;

/**
 * @author Gijs Sijpesteijn
 */
public class Line {
    private final Point start;
    private final Point end;

    public Line(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }
}
