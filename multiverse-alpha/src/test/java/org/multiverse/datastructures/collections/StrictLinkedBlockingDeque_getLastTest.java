package org.multiverse.datastructures.collections;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import static org.multiverse.api.GlobalStmInstance.getGlobalStmInstance;
import org.multiverse.api.Stm;
import static org.multiverse.utils.ThreadLocalTransaction.setThreadLocalTransaction;

import java.util.NoSuchElementException;
import java.util.concurrent.BlockingDeque;

public class StrictLinkedBlockingDeque_getLastTest {

    private Stm stm;

     @Before
     public void setUp() {
         stm = getGlobalStmInstance();
         setThreadLocalTransaction(null);
     }

     @Test
    public void getLastFromEmptyDeque() {
        BlockingDeque<String> deque = new StrictLinkedBlockingDeque<String>();

        long version = stm.getClockVersion();

        try {
            deque.getLast();
            fail();
        } catch (NoSuchElementException expected) {
        }

        assertEquals(version, stm.getClockVersion());
        assertEquals("[]", deque.toString());
    }

    @Test
    public void getLastFromSingleElementDeque() {
        BlockingDeque<String> deque = new StrictLinkedBlockingDeque<String>();
        deque.add("1");
        long version = stm.getClockVersion();

        String result = deque.getLast();

        assertEquals("1", result);
        assertEquals(version, stm.getClockVersion());
        assertEquals("[1]", deque.toString());
    }

    @Test
    public void getLastFromNonEmptyDeque() {
        BlockingDeque<String> deque = new StrictLinkedBlockingDeque<String>();
        deque.add("1");
        deque.add("2");
        long version = stm.getClockVersion();

        String result = deque.getLast();

        assertEquals("2", result);
        assertEquals(version, stm.getClockVersion());
        assertEquals("[1, 2]", deque.toString());
    }
}