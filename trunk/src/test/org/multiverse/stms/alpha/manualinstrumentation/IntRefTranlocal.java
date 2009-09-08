package org.multiverse.stms.alpha.manualinstrumentation;

import static org.multiverse.api.StmUtils.retry;
import org.multiverse.api.exceptions.ReadonlyException;
import org.multiverse.stms.alpha.AlphaAtomicObject;
import org.multiverse.stms.alpha.AlphaTranlocal;
import org.multiverse.stms.alpha.AlphaTranlocalSnapshot;
import org.multiverse.stms.alpha.DirtinessStatus;

/**
 * access modifiers for fields are public because this object is used for testing purposes. For the
 * instrumentation the fields don't need to be this public.
 */
public class IntRefTranlocal extends AlphaTranlocal {
    public IntRef atomicObject;
    public int value;
    private IntRefTranlocal origin;

    public IntRefTranlocal(IntRefTranlocal origin) {
        this.origin = origin;
        this.version = origin.version;
        this.value = origin.value;
        this.atomicObject = origin.atomicObject;
    }

    public IntRefTranlocal(IntRef atomicObject, int value) {
        this.atomicObject = atomicObject;
        this.value = value;
    }

    @Override
    public AlphaAtomicObject getAtomicObject() {
        return atomicObject;
    }

    public IntRefTranlocal(int value) {
        this.value = value;
    }

    public void loopInc(int amount) {
        if (committed) {
            throw new ReadonlyException();
        } else {
            for (int k = 0; k < amount; k++) {
                inc();
            }
        }
    }

    public void set(int newValue) {
        if (committed) {
            throw new ReadonlyException();
        } else {
            this.value = newValue;
        }
    }

    public int get() {
        return value;
    }

    public void inc() {
        if (committed) {
            throw new ReadonlyException();
        } else {
            value++;
        }
    }

    public void dec() {
        if (committed) {
            throw new ReadonlyException();
        } else {
            value--;
        }
    }

    public void await(int expectedValue) {
        if (value != expectedValue) {
            retry();
        }
    }

    @Override
    public void prepareForCommit(long writeVersion) {
        this.version = writeVersion;
        this.committed = true;
        this.origin = null;
    }

    @Override
    public DirtinessStatus getDirtinessStatus() {
        if (committed) {
            return DirtinessStatus.committed;
        } else if (origin == null) {
            return DirtinessStatus.fresh;
        } else if (origin.value != this.value) {
            return DirtinessStatus.dirty;
        } else {
            return DirtinessStatus.clean;
        }
    }

    @Override
    public IntRefTranlocalSnapshot takeSnapshot() {
        return new IntRefTranlocalSnapshot(this);
    }
}

class IntRefTranlocalSnapshot extends AlphaTranlocalSnapshot {

    final IntRefTranlocal tranlocal;
    final int value;

    public IntRefTranlocalSnapshot(IntRefTranlocal tranlocal) {
        this.tranlocal = tranlocal;
        this.value = tranlocal.value;
    }

    @Override
    public AlphaTranlocal getTranlocal() {
        return tranlocal;
    }

    @Override
    public void restore() {
        tranlocal.value = value;
    }
}
