package org.multiverse.datastructures.collections;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import static org.multiverse.api.GlobalStmInstance.getGlobalStmInstance;
import org.multiverse.api.Stm;
import static org.multiverse.utils.ThreadLocalTransaction.setThreadLocalTransaction;

import java.util.concurrent.BlockingDeque;

public class StrictLinkedBlockingDeque_addFirstTest {

    private Stm stm;

    @Before
    public void setUp() {
        stm = getGlobalStmInstance();
        setThreadLocalTransaction(null);
    }

    @Test             
    public void addFirstWithNullFails() {
        BlockingDeque<String> deque = new StrictLinkedBlockingDeque<String>();

        long version = stm.getClockVersion();
        try {
            deque.addFirst(null);
            fail();
        } catch (NullPointerException expected) {
        }

        assertEquals(version, stm.getClockVersion());
        assertEquals(0, deque.size());
        assertEquals("[]", deque.toString());
    }

    @Test
    public void addFirstOnEmptyDeque() {
        BlockingDeque<String> deque = new StrictLinkedBlockingDeque<String>();

        long version = stm.getClockVersion();
        deque.addFirst("1");

        assertEquals(1, deque.size());
        assertEquals(version + 1, stm.getClockVersion());
        assertEquals("[1]", deque.toString());
    }

    @Test
    public void addFirstOnNonEmptyDeque() {
        BlockingDeque<String> deque = new StrictLinkedBlockingDeque<String>();
        deque.addFirst("1");

        long version = stm.getClockVersion();

        deque.addFirst("2");
        assertEquals(version + 1, stm.getClockVersion());
        assertEquals(2, deque.size());
        assertEquals("[2, 1]", deque.toString());
    }

    @Test
    public void addFirstWithFullDeque() {
        BlockingDeque<String> deque = new StrictLinkedBlockingDeque<String>(1);
        deque.addFirst("1");

        long version = stm.getClockVersion();
        try {
            deque.addFirst("2");
            fail();
        } catch (IllegalStateException expected) {
        }

        assertEquals(version, stm.getClockVersion());
        assertEquals(1, deque.size());
        assertEquals("[1]", deque.toString());
    }
}
