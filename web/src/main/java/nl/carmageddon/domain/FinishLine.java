package nl.carmageddon.domain;

/**
 * @author Gijs Sijpesteijn
 */
public class FinishLine  {
    private Line line;

    public FinishLine(Line line) {
        this.line = line;
    }

    public int getDistance() {
        int distance = (int) ((line.getStart().y + line.getEnd().y) / 2);
        System.out.println("Distance: " + distance);
        return distance;
    }

    public Line getLine() {
        return line;
    }
}
