package org.codehaus.multiverse.multiversionedstm;

import org.codehaus.multiverse.transaction.NoSuchObjectException;

public class Transaction_DeleteTest extends AbstractMultiversionedStmTest {

    public void testIllegalPointer() {
        createActiveTransaction();

        try {
            transaction.delete(-1);
            fail();
        } catch (NoSuchObjectException ex) {
        }

        assertTransactionIsActive();
    }

    //============================== other states ===================================

    public void testWhileCommitted() {
        long version = stm.getActiveVersion();
        createCommittedTransaction();

        try {
            transaction.delete(1);
            fail();
        } catch (IllegalStateException ex) {
        }

        assertTransactionIsCommitted();
        assertStmActiveVersion(version);
    }

    public void testWhileAborted() {
        createAbortedTransaction();

        try {
            transaction.delete(1);
            fail();
        } catch (IllegalStateException ex) {

        }

        assertTransactionHasNoWrites();
        assertStmVersionHasNotChanged();
        assertCommitCount(0);
        assertAbortedCount(1);
        assertTransactionIsAborted();
    }
}
