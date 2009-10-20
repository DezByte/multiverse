package org.multiverse.integrationtests;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.multiverse.TestThread;
import static org.multiverse.TestUtils.joinAll;
import static org.multiverse.TestUtils.startAll;
import static org.multiverse.api.GlobalStmInstance.getGlobalStmInstance;
import org.multiverse.datastructures.refs.IntRef;
import static org.multiverse.utils.TransactionThreadLocal.setThreadLocalTransaction;

import java.util.concurrent.TimeUnit;

/**
 * @author Peter Veentjer
 */
public class ConcurrentUpdateLongTest {

    public IntRef intValue;
    public int incCount = 10 * 1000 * 1000;
    public int threadCount = 3;

    @Before
    public void setUp() {
        setThreadLocalTransaction(null);
        intValue = new IntRef(0);
    }

    @After
    public void tearDown() {
        //    stm.getProfiler().print();
    }

    @Test
    public void test() {
        IncThread[] threads = createThreads();

        long startNs = System.nanoTime();

        startAll(threads);
        joinAll(threads);

        System.out.println("version: " + getGlobalStmInstance().getClockVersion());
        assertEquals(threadCount * incCount, intValue.get());

        long periodNs = System.nanoTime() - startNs;
        double transactionPerSecond = (incCount * threadCount * 1.0d * TimeUnit.SECONDS.toNanos(1)) / periodNs;
        System.out.printf("%s Transaction/second\n", transactionPerSecond);
    }

    public IncThread[] createThreads() {
        IncThread[] results = new IncThread[threadCount];
        for (int k = 0; k < threadCount; k++) {
            results[k] = new IncThread(k);
        }
        return results;
    }

    public class IncThread extends TestThread {

        public IncThread(int id) {
            super("StressThread" + id);
        }

        @Test
        public void doRun() {
            for (int k = 0; k < incCount; k++) {
                intValue.inc();

                if (k % (1000 * 1000) == 0) {
                    System.out.printf("%s at %s\n", getName(), k);
                }
            }
        }
    }
}
