package nl.carmageddon.car;

import nl.carmageddon.domain.LookoutResult;
import nl.carmageddon.domain.View;
import org.opencv.core.Mat;

/**
 * @author Gijs Sijpesteijn
 */
public interface Lookout<V> {

    LookoutResult start();

    void stop();

    View getCurrentView(Mat snapshot);

    void addViewToMat(Mat snapshot, V view);
}
