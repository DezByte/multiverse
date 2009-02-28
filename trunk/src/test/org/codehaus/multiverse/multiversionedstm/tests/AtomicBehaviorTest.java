package org.codehaus.multiverse.multiversionedstm.tests;

import org.codehaus.multiverse.core.Transaction;
import org.codehaus.multiverse.multiversionedheap.standard.DefaultMultiversionedHeap;
import org.codehaus.multiverse.multiversionedstm.MultiversionedStm;
import org.codehaus.multiverse.multiversionedstm.examples.Queue;
import org.junit.After;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 * This test makes sure that all of the changes are committed, or none of them are committed.
 *
 * @author Peter Veentjer.
 */
public class AtomicBehaviorTest {

    private MultiversionedStm stm;
    private long[] queues;
    private int queueCount = 10;

    private DefaultMultiversionedHeap heap;

    @Before
    public void setUp() {
        heap = new DefaultMultiversionedHeap();
        stm = new MultiversionedStm(heap);
    }

    @After
    public void teatDown() {
        System.out.println(stm.getStatistics());
        System.out.println(heap.getStatistics());
    }

    @Test
    public void test() {
        queues = createQueues();

        Transaction t = stm.startTransaction();
        for (long handle : queues) {
            Queue queue = (Queue) t.read(handle);
            queue.push("foo");
            queue.push("bar");
        }
        t.abort();

        assertQueuesAreEmpty();
    }

    public void assertQueuesAreEmpty() {
        Transaction t = stm.startTransaction();
        for (long handle : queues) {
            Queue queue = (Queue) t.read(handle);
            if (!queue.isEmpty())
                fail();
        }

        t.commit();
    }

    private long[] createQueues() {
        Transaction t = stm.startTransaction();
        long[] handles = new long[queueCount];
        for (int k = 0; k < queueCount; k++)
            handles[k] = t.attachAsRoot(new Queue());
        t.commit();
        return handles;
    }
}
