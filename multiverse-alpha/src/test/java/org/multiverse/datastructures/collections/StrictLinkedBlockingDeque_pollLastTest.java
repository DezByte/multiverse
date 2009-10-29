package org.multiverse.datastructures.collections;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import static org.multiverse.api.GlobalStmInstance.getGlobalStmInstance;
import org.multiverse.api.Stm;
import static org.multiverse.utils.ThreadLocalTransaction.setThreadLocalTransaction;

import java.util.concurrent.BlockingDeque;

public class StrictLinkedBlockingDeque_pollLastTest {

    private Stm stm;

    @Before
    public void setUp() {
        stm = getGlobalStmInstance();
        setThreadLocalTransaction(null);
    }

    @Test
    public void pollLastFromEmptyDeque() {
        BlockingDeque<String> deque = new StrictLinkedBlockingDeque<String>();

        long version = stm.getClockVersion();
        String result = deque.pollLast();
        assertNull(result);
        assertEquals(version, stm.getClockVersion());
        assertEquals(0, deque.size());
        assertEquals("[]", deque.toString());
    }

    @Test
    public void pollLastFromDequeWithSingleItem() {
        BlockingDeque<String> deque = new StrictLinkedBlockingDeque<String>();
        deque.add("1");

        long version = stm.getClockVersion();
        String result = deque.pollLast();
        assertEquals("1", result);
        assertEquals(version + 1, stm.getClockVersion());
        assertEquals(0, deque.size());
        assertEquals("[]", deque.toString());
    }

    @Test
    public void pollLastFromDequeWithMultipleItems() {
        BlockingDeque<String> deque = new StrictLinkedBlockingDeque<String>();
        deque.add("1");
        deque.add("2");

        long version = stm.getClockVersion();
        String result = deque.pollLast();
        assertEquals("2", result);
        assertEquals(version + 1, stm.getClockVersion());
        assertEquals(1, deque.size());
        assertEquals("[1]", deque.toString());
    }


}