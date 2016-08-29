package nl.carmageddon.domain;

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
public class EngineTest {

    @Mock
    private PwmImpl pwmImpl;

    @InjectMocks
    private Engine engine;

    @Test
    public void getThrottle() throws Exception {
        assertEquals(0, engine.getThrottle());
    }

    @Test
    public void setThrottle() throws Exception {
        engine.setThrottle(10);
        assertEquals(10, engine.getThrottle());
        engine.setThrottle(15);
        assertEquals(15, engine.getThrottle());
        engine.setThrottle(-15);
        assertEquals(-15, engine.getThrottle());
        engine.setThrottle(-18);
        assertEquals(-18, engine.getThrottle());
    }

    @Test
    public void setMaxThrottle() throws Exception {
        engine.setThrottle(130);
        assertEquals(120, engine.getThrottle());
    }

    @Test
    public void setMinThrottle() throws Exception {
        engine.setThrottle(-130);
        assertEquals(-120, engine.getThrottle());
    }

    @Test
    public void speedup() throws Exception {
        engine.setThrottle(0);
        engine.speedUp();
        assertEquals(-1, engine.getThrottle());
    }

    @Test
    public void slowdown() throws Exception {
        engine.setThrottle(0);
        engine.slowDown();
        assertEquals(1, engine.getThrottle());
    }
}