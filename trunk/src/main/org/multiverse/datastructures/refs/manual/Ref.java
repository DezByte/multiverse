package org.multiverse.datastructures.refs.manual;

import static org.multiverse.api.StmUtils.retry;
import org.multiverse.api.Transaction;
import org.multiverse.api.exceptions.LoadUncommittedException;
import org.multiverse.api.exceptions.ReadonlyException;
import org.multiverse.datastructures.refs.ManagedRef;
import org.multiverse.stms.alpha.*;
import org.multiverse.stms.alpha.mixins.FastAtomicObjectMixin;
import org.multiverse.templates.AtomicTemplate;

import static java.lang.String.format;

/**
 * A manual instrumented {@link org.multiverse.datastructures.refs.ManagedRef} implementation.
 * If this class is used, you don't need to worry about instrumentation/javaagents and
 * stuff like this.
 * <p/>
 * It is added to get the Akka project up and running, but probably will removed when the instrumentation
 * is 100% up and running and this can be done compiletime instead of messing with javaagents.
 *
 * @author Peter Veentjer
 */
public final class Ref<E> extends FastAtomicObjectMixin implements ManagedRef<E> {

    public Ref() {
        new AtomicTemplate() {
            @Override
            public Object execute(Transaction t) throws Exception {
                ((AlphaTransaction) t).attachNew(new RefTranlocal(Ref.this));
                return null;
            }
        }.execute();
    }

    public Ref(Transaction t) {
        ((AlphaTransaction) t).attachNew(new RefTranlocal(Ref.this));
    }

    public Ref(final E value) {
        new AtomicTemplate() {
            @Override
            public Object execute(Transaction t) throws Exception {
                ((AlphaTransaction) t).attachNew(new RefTranlocal(Ref.this, value));
                return null;
            }
        }.execute();
    }

    public Ref(Transaction t, final E value) {
        ((AlphaTransaction) t).attachNew(new RefTranlocal(Ref.this, value));
    }

    public E get() {
        return new AtomicTemplate<E>() {
            @Override
            public E execute(Transaction t) throws Exception {
                RefTranlocal<E> tranlocalRef = (RefTranlocal) ((AlphaTransaction) t).load(Ref.this);
                return tranlocalRef.get();
            }
        }.execute();
    }

    public E get(Transaction t) {
        RefTranlocal<E> tranlocalRef = (RefTranlocal) ((AlphaTransaction) t).load(Ref.this);
        return tranlocalRef.get();
    }

    @Override
    public E getOrAwait() {
        return new AtomicTemplate<E>() {
            @Override
            public E execute(Transaction t) throws Exception {
                RefTranlocal<E> tranlocalRef = (RefTranlocal) ((AlphaTransaction) t).load(Ref.this);
                return tranlocalRef.getOrAwait();
            }
        }.execute();
    }

    public E getOrAwait(Transaction t) {
        RefTranlocal<E> tranlocalRef = (RefTranlocal) ((AlphaTransaction) t).load(Ref.this);
        return tranlocalRef.getOrAwait();
    }


    @Override
    public E set(final E newRef) {
        return new AtomicTemplate<E>() {
            @Override
            public E execute(Transaction t) throws Exception {
                RefTranlocal<E> tranlocalRef = (RefTranlocal) ((AlphaTransaction) t).load(Ref.this);
                return tranlocalRef.set(newRef);
            }
        }.execute();
    }

    public E set(Transaction t, final E newRef) {
        RefTranlocal<E> tranlocalRef = (RefTranlocal) ((AlphaTransaction) t).load(Ref.this);
        return tranlocalRef.set(newRef);
    }

    @Override
    public boolean isNull() {
        return new AtomicTemplate<Boolean>() {
            @Override
            public Boolean execute(Transaction t) throws Exception {
                RefTranlocal<E> tranlocalRef = (RefTranlocal) ((AlphaTransaction) t).load(Ref.this);
                return tranlocalRef.isNull();
            }
        }.execute();
    }

    public boolean isNull(Transaction t) {
        RefTranlocal<E> tranlocalRef = (RefTranlocal) ((AlphaTransaction) t).load(Ref.this);
        return tranlocalRef.isNull();
    }

    @Override
    public E clear() {
        return new AtomicTemplate<E>() {
            @Override
            public E execute(Transaction t) throws Exception {
                RefTranlocal<E> tranlocalRef = (RefTranlocal) ((AlphaTransaction) t).load(Ref.this);
                return tranlocalRef.clear();
            }
        }.execute();
    }

    public E clear(Transaction t) {
        RefTranlocal<E> tranlocalRef = (RefTranlocal) ((AlphaTransaction) t).load(Ref.this);
        return tranlocalRef.clear();
    }

    @Override
    public String toString() {
        return new AtomicTemplate<String>() {
            @Override
            public String execute(Transaction t) throws Exception {
                RefTranlocal<E> tranlocalRef = (RefTranlocal) ((AlphaTransaction) t).load(Ref.this);
                return tranlocalRef.toString();
            }
        }.execute();
    }

    public String toString(Transaction t) {
        RefTranlocal<E> tranlocalRef = (RefTranlocal) ((AlphaTransaction) t).load(Ref.this);
        return tranlocalRef.toString();
    }

    @Override
    public RefTranlocal<E> privatize(long readVersion) {
        RefTranlocal<E> origin = (RefTranlocal<E>) load(readVersion);
        if (origin == null) {
            throw new LoadUncommittedException();
        }
        return new RefTranlocal<E>(origin);
    }
}

class RefTranlocal<E> extends AlphaTranlocal {
    //field belonging to the stm.
    Ref atomicObject;
    RefTranlocal origin;

    E ref;

    RefTranlocal(RefTranlocal<E> origin) {
        this.version = origin.version;
        this.atomicObject = origin.atomicObject;
        this.ref = origin.ref;
        this.origin = origin;
    }

    RefTranlocal(Ref<E> owner) {
        this(owner, null);
    }

    RefTranlocal(Ref<E> owner, E ref) {
        this.version = Long.MIN_VALUE;
        this.atomicObject = owner;
        this.ref = ref;
    }

    @Override
    public AlphaAtomicObject getAtomicObject() {
        return atomicObject;
    }

    public E clear() {
        E oldValue = ref;
        ref = null;
        return oldValue;
    }

    public boolean isNull() {
        return ref == null;
    }

    public E get() {
        return ref;
    }

    public E set(E newValue) {
        if (committed) {
            throw new ReadonlyException();
        }
        E oldValue = ref;
        this.ref = newValue;
        return oldValue;
    }

    public E getOrAwait() {
        if (isNull()) {
            retry();
        }

        return ref;
    }

    @Override
    public String toString() {
        if (ref == null) {
            return "Ref(reference=null)";
        } else {
            return format("Ref(reference=%s)", ref);
        }
    }

    @Override
    public void prepareForCommit(long writeVersion) {
        this.version = writeVersion;
        this.committed = true;
        this.origin = null;
    }

    @Override
    public AlphaTranlocalSnapshot takeSnapshot() {
        return new RefTranlocalSnapshot<E>(this);
    }

    @Override
    public DirtinessStatus getDirtinessStatus() {
        if (committed) {
            return DirtinessStatus.committed;
        } else if (origin == null) {
            return DirtinessStatus.fresh;
        } else if (origin.ref != this.ref) {
            return DirtinessStatus.dirty;
        } else {
            return DirtinessStatus.clean;
        }
    }
}

class RefTranlocalSnapshot<E> extends AlphaTranlocalSnapshot {
    final RefTranlocal tranlocal;
    final E value;

    RefTranlocalSnapshot(RefTranlocal<E> tranlocal) {
        this.tranlocal = tranlocal;
        this.value = tranlocal.ref;
    }

    @Override
    public AlphaTranlocal getTranlocal() {
        return tranlocal;
    }

    @Override
    public void restore() {
        tranlocal.ref = value;
    }
}