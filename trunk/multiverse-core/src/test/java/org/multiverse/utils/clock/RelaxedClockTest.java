package org.multiverse.utils.clock;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * @author Peter Veentjer
 */
public class RelaxedClockTest {

    @Test
    public void testNoArgConstructor(){
        RelaxedClock clock  = new RelaxedClock();
        assertEquals(0, clock.getDawn());
        assertEquals(0, clock.getTime());
    }

    @Test
    public void testConstructorWithDawn(){
        RelaxedClock clock  = new RelaxedClock(10);
        assertEquals(10, clock.getDawn());
        assertEquals(10, clock.getTime());
    }

    @Test
    public void testTick(){
        RelaxedClock clock = new RelaxedClock();
        long old = clock.getTime();
        long returned = clock.tick();
        assertEquals(old+1, clock.getTime());
        assertEquals(returned, clock.getTime());
    }

    @Test
    public void testToString(){
        RelaxedClock clock = new RelaxedClock(1000);
        //make sure that the toString function makes use of the time and not of the dawn.
        clock.tick();

        assertEquals("RelaxedClock(time=1001)", clock.toString());
    }
}
