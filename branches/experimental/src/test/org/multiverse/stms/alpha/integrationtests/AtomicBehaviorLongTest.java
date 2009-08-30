package org.multiverse.stms.alpha.integrationtests;

import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.multiverse.TestThread;
import static org.multiverse.TestUtils.*;
import org.multiverse.api.Stm;
import org.multiverse.api.annotations.AtomicMethod;
import org.multiverse.api.exceptions.DeadTransactionException;
import org.multiverse.datastructures.refs.IntRef;
import org.multiverse.stms.alpha.AlphaStm;
import org.multiverse.utils.GlobalStmInstance;
import static org.multiverse.utils.TransactionThreadLocal.getThreadLocalTransaction;
import static org.multiverse.utils.TransactionThreadLocal.setThreadLocalTransaction;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A test that checks if modifications are done atomically. So a transactions that are aborted, should
 * not be committed (not even partially) to the heap.
 * <p/>
 * The test: there is a modification thread that updates an integervalue. The only valid value that is permitted
 * in the heap is a value that can be divided by 2. The update is done in 2 staps that increase the value by one
 * and in some cases the transaction is aborted.
 *
 * @author Peter Veentjer.
 */
public class AtomicBehaviorLongTest {

    private Stm stm;

    private IntRef intValue;
    private int modifyCount = 500;
    private AtomicInteger modifyCountDown = new AtomicInteger();

    @Before
    public void setUp() {
        stm = new AlphaStm();
        GlobalStmInstance.set(stm);
        setThreadLocalTransaction(null);
        intValue = new IntRef(0);
    }

    @Test
    public void test() {
        modifyCountDown.set(modifyCount);

        ModifyThread modifyThread = new ModifyThread(0);
        ObserverThread observerThread = new ObserverThread();

        startAll(modifyThread, observerThread);
        joinAll(modifyThread, observerThread);
    }

    class ModifyThread extends TestThread {
        public ModifyThread(int id) {
            super("ModifyThread-" + id);
        }

        public void run() {
            while (modifyCountDown.getAndDecrement() > 0) {
                try {
                    doit();
                } catch (DeadTransactionException ignore) {
                }

            }
        }

        @AtomicMethod
        public void doit() {
            if (intValue.get() % 2 != 0) {
                fail();
            }

            intValue.inc();

            sleepRandomMs(20);

            if (randomBoolean()) {
                getThreadLocalTransaction().abort();
            } else {
                intValue.inc();
            }
        }
    }

    class ObserverThread extends TestThread {
        public ObserverThread() {
            super("ObserverThread");
        }

        @Override
        public void run() {
            while (modifyCountDown.get() > 0) {
                doit();
                sleepRandomMs(5);
            }
        }

        @AtomicMethod
        public void doit() {
            if (intValue.get() % 2 != 0) {
                fail();
            }
        }
    }
}
