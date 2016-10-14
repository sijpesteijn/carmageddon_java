package nl.carmageddon.car.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

/**
 * @author Gijs Sijpesteijn
 */
@RunWith(MockitoJUnitRunner.class)
public class SteerTest {

    @Mock
    private PwmImpl pwmImpl;

    @InjectMocks
    private Steer steer;

    @Before
    public void setup() {
        steer.setConnected(true);
    }


    @Test
    public void getAngle() throws Exception {
        assertEquals(0, steer.getAngle());
    }

    @Test
    public void setAngle() throws Exception {
        steer.setAngle(10);
        assertEquals(10, steer.getAngle());
        steer.setAngle(15);
        assertEquals(15, steer.getAngle());
        steer.setAngle(-15);
        assertEquals(-15, steer.getAngle());
        steer.setAngle(-18);
        assertEquals(-18, steer.getAngle());
    }

    @Test
    public void setMaxAngle() throws Exception {
        steer.setAngle(50);
        assertEquals(40, steer.getAngle());
    }

    @Test
    public void setMinAngle() throws Exception {
        steer.setAngle(-50);
        assertEquals(-40, steer.getAngle());
    }

    @Test
    public void leftAngle() throws Exception {
        steer.setAngle(0);
        steer.left();
        assertEquals(-1, steer.getAngle());
    }

    @Test
    public void rightAngle() throws Exception {
        steer.setAngle(0);
        steer.right();
        assertEquals(1, steer.getAngle());
    }
}