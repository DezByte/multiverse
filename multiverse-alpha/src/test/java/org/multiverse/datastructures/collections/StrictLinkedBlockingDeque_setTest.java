package org.multiverse.datastructures.collections;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import static org.multiverse.api.GlobalStmInstance.getGlobalStmInstance;
import org.multiverse.api.Stm;
import static org.multiverse.utils.ThreadLocalTransaction.setThreadLocalTransaction;

public class StrictLinkedBlockingDeque_setTest {

    private Stm stm;

    @Before
    public void setUp() {
        stm = getGlobalStmInstance();
        setThreadLocalTransaction(null);
    }

    @Test
    public void setFailsIfIndexOutOfBounds() {
        StrictLinkedBlockingDeque<String> deque = new StrictLinkedBlockingDeque<String>();
        deque.add("1");

        setFailsIfIndexOutOfBounds(deque, -1);
        setFailsIfIndexOutOfBounds(deque, 1);
        setFailsIfIndexOutOfBounds(deque, 2);
    }

    private void setFailsIfIndexOutOfBounds(StrictLinkedBlockingDeque<String> deque, int index) {
        String original = deque.toString();
        long version = stm.getClockVersion();
        try {
            deque.get(index);
            fail();
        } catch (IndexOutOfBoundsException ignore) {
        }
        assertEquals(version, stm.getClockVersion());
        assertEquals(original, deque.toString());
    }

    @Test
    public void set(){
        StrictLinkedBlockingDeque<String> deque = new StrictLinkedBlockingDeque<String>();
        deque.add("1");
        deque.add("2");
        deque.add("3");


        String result = deque.set(0, "a");
        assertEquals(result, "1");
        assertEquals("[a, 2, 3]", deque.toString());

        result = deque.set(1, "b");
        assertEquals(result, "2");
        assertEquals("[a, b, 3]", deque.toString()) ;
    }
}
