package nl.carmageddon.service;

import nl.carmageddon.domain.PCA;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.opencv.core.Point;

import java.util.List;

import static java.lang.Math.atan;
import static java.lang.Math.toDegrees;

/**
 * @author Gijs Sijpesteijn
 */
public class MatUtils {

    public static Point getCenterPoint(List<Point> points) {
        double averageX = points.stream().mapToDouble(point -> point.x).average().getAsDouble();
        double averageY = points.stream().mapToDouble(point -> point.y).average().getAsDouble();
        return new Point(averageX, averageY);
    }

    public static PCA calculatePCA(List<Point> points) {
        PCA pca = new PCA();
        Point center = getCenterPoint(points);
        pca.setCenter(center);

        double[][] pointMatrix = points.stream().map(point ->
                                                             new double[] { point.x - center.x, point.y - center.y }).toArray(double[][] :: new);

        RealMatrix mx = MatrixUtils.createRealMatrix(pointMatrix);
        RealMatrix cov = new Covariance(mx).getCovarianceMatrix();
        EigenDecomposition decomposition = new EigenDecomposition(cov);

        Point axisX = new Point(center.x + (0.02 * decomposition.getRealEigenvalue(0) * decomposition.getV().getEntry(0,0)),
                                center.y + (0.02 * decomposition.getRealEigenvalue(0) * decomposition.getV().getEntry(0,1)));

        Point axisY = new Point(center.x - (0.02 * decomposition.getRealEigenvalue(1) * decomposition.getV().getEntry(1,0)),
                                center.y + (0.02 * decomposition.getRealEigenvalue(1) * decomposition.getV().getEntry(1,1)));
        pca.setAxisX(axisX);
        pca.setAxisY(axisY);

        double angle = toDegrees(atan(decomposition.getRealEigenvalues()[1]/decomposition.getRealEigenvalues()[0]));
        pca.setAngle(angle);
        return pca;
    }

}
