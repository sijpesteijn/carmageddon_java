package nl.carmageddon.car;

import nl.carmageddon.domain.Line;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrackLineHelper {

    public static List<Line> convert2Lines(List<Point> points) {
        List<Line> lines = new ArrayList<>();
        for (int i = 0; i < points.size(); i = i + 2) {
            lines.add(new Line(points.get(i), points.get(i + 1)));
        }
        return lines;
    }

    public static Optional<Line> getRightLine(List<Line> allVerticalLines, boolean isRightTrack) {

        List<Integer> lineAngles = new ArrayList<>();

        List<Line> lines = new ArrayList<>();

        for (Line line : allVerticalLines) {

            double dx = line.getStart().x - line.getEnd().x + 0.00000001;
            double dy = line.getStart().y - line.getEnd().y;

            final int angleLine = (int) Math.toDegrees(Math.atan2(dy, dx));

            final boolean present = lineAngles.stream()
                    .filter(angle ->
                            (angleLine <= angle + 5.0 && angleLine >= angle - 5.0))
                    .filter(angle -> {
                        return Math.signum(angle) == Math.signum(angleLine);
                    })
                    .findAny().isPresent();

            if (!present) {
                lineAngles.add(angleLine);
                if (angleLine >= 0) {

                } else {
                    lines.add(line);
                }
            }
        }

        if (isRightTrack) {
            return lines.stream()
                    .sorted((o1, o2) -> compare(o2, o1))
                    .findFirst().map(x -> x.getStart().y < x.getEnd().y ? x : new Line(x.getEnd(), x.getStart()));
        } else {
            return lines.stream()
                    .sorted((o1, o2) -> compare(o1, o2))
                    .findFirst().map(x -> x.getStart().y < x.getEnd().y ? x : new Line(x.getEnd(), x.getStart()));
        }
    }

    public static Optional<Line> getLeftLine(List<Line> allVerticalLines, boolean isRightTrack) {

        List<Integer> lineAngles = new ArrayList<>();

        List<Line> lines = new ArrayList<>();

        for (Line line : allVerticalLines) {

            double dx = line.getStart().x - line.getEnd().x + 0.00000001;
            double dy = line.getStart().y - line.getEnd().y;

            final int angleLine = (int) Math.toDegrees(Math.atan2(dy, dx));

            final boolean present = lineAngles.stream()
                    .filter(angle ->
                            (angleLine <= angle + 5.0 && angleLine >= angle - 5.0))
                    .filter(angle -> {
                        return Math.signum(angle) == Math.signum(angleLine);
                    })
                    .findAny().isPresent();

            if (!present) {
                lineAngles.add(angleLine);
                if (angleLine >= 0) {
                    lines.add(line);
                } else {
                    //
                }
            }
        }

        if (isRightTrack) {
            return lines.stream()
                    .sorted((o1, o2) -> compare(o1, o2))
                    .findFirst().map(x -> x.getStart().y < x.getEnd().y ? x : new Line(x.getEnd(), x.getStart()));
        } else {
            return lines.stream()
                    .sorted((o1, o2) -> compare(o2, o1))
                    .findFirst().map(x -> x.getStart().y < x.getEnd().y ? x : new Line(x.getEnd(), x.getStart()));
        }
    }


    public static Optional<Point> getIntersection(Line line1, Line line2) {

        double a1 = (line1.getStart().y - line1.getEnd().y) / (double) (line1.getStart().x - line1.getEnd().x);
        double b1 = line1.getStart().y - a1 * line1.getStart().x;

        double a2 = (line2.getStart().y - line2.getEnd().y) / (double) (line2.getStart().x - line2.getEnd().x);
        double b2 = line2.getStart().y - a2 * line2.getStart().x;

        if (Math.abs(a1 - a2) < 0.00001)
            return Optional.empty();

        double x = (b2 - b1) / (a1 - a2);
        double y = a1 * x + b1;
        return Optional.of(new Point((int) x, (int) y));
    }

    private static int compare(Line o1, Line o2) {
        return (o1.getStart().x > o2.getStart().x) ? -1 : ((o1.getStart().x == o2.getStart().x) ? 0 : 1);
    }


}
