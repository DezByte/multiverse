package org.multiverse.multiversionedstm.examples;

import org.multiverse.api.Handle;
import org.multiverse.api.LazyReference;
import org.multiverse.api.Transaction;
import org.multiverse.multiversionedstm.*;

public final class Pair<L, R> implements MaterializedObject {

    private L left;
    private R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
        this.handle = new DefaultHandle<Pair<L, R>>();
    }

    public Pair() {
        this.handle = new DefaultHandle<Pair<L, R>>();
    }

    public L getLeft() {
        if (lazyLeft != null) {
            left = lazyLeft.get();
            lazyLeft = null;
        }
        return left;
    }

    public void setLeft(L left) {
        lazyLeft = null;
        this.left = left;
    }

    public R getRight() {
        if (lazyRight != null) {
            right = lazyRight.get();
            lazyRight = null;
        }
        return right;
    }

    public void setRight(R right) {
        lazyRight = null;
        this.right = right;
    }

    //@V
    //public String toString(){
    //
    //}


    //@Override
    //public int hashCode(){
    //    return getLeft()
    //}

    @Override
    public boolean equals(Object thatObj) {
        if (thatObj == this)
            return true;

        if (!(thatObj instanceof Pair))
            return false;

        Pair that = (Pair) thatObj;

        if (!equals(that.getLeft(), this.getLeft()))
            return false;

        if (!equals(that.getRight(), this.getRight()))
            return false;

        return true;
    }

    private static boolean equals(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        } else {
            return o1.equals(o2);
        }
    }

    //========== generated ========================
    private DematerializedPair<L, R> lastDematerialized;
    private LazyReference<L> lazyLeft;
    private LazyReference<R> lazyRight;
    private final Handle<Pair<L, R>> handle;

    private Pair(DematerializedPair<L, R> dematerializedPair, Transaction t) {
        this.handle = dematerializedPair.handle;
        this.lastDematerialized = dematerializedPair;

        if (dematerializedPair.left instanceof Handle) {
            lazyLeft = t.readLazy((Handle) dematerializedPair.left);
        } else {
            left = (L) dematerializedPair.left;
        }

        if (dematerializedPair.right instanceof Handle) {
            lazyRight = t.readLazy((Handle) dematerializedPair.right);
        } else {
            right = (R) dematerializedPair.right;
        }
    }

    @Override
    public Handle<Pair<L, R>> getHandle() {
        return handle;
    }

    @Override
    public boolean isDirty() {
        if (lastDematerialized == null)
            return true;

        if (lastDematerialized.left != MultiversionedStmUtils.getValueOrHandle(lazyLeft, left))
            return true;

        if (lastDematerialized.right != MultiversionedStmUtils.getValueOrHandle(lazyRight, right))
            return true;

        return false;
    }

    @Override
    public DematerializedObject dematerialize() {
        return new DematerializedPair<L, R>(this);
    }

    private MaterializedObject nextInChain;

    @Override
    public MaterializedObject getNextInChain() {
        return nextInChain;
    }

    @Override
    public void setNextInChain(MaterializedObject next) {
        this.nextInChain = next;
    }

    @Override
    public void walkMaterializedMembers(MemberWalker memberWalker) {
        if (left instanceof MaterializedObject) memberWalker.onMember((MaterializedObject) left);
        if (right instanceof MaterializedObject) memberWalker.onMember((MaterializedObject) right);
    }

    public static class DematerializedPair<L, R> implements DematerializedObject {
        private final Handle<Pair<L, R>> handle;
        private final Object left;
        private final Object right;

        public DematerializedPair(Pair<L, R> pair) {
            this.handle = pair.handle;
            this.left = MultiversionedStmUtils.getValueOrHandle(pair.lazyLeft, pair.left);
            this.right = MultiversionedStmUtils.getValueOrHandle(pair.lazyRight, pair.right);
        }

        @Override
        public Handle<Pair<L, R>> getHandle() {
            return handle;
        }

        @Override
        public Pair<L, R> rematerialize(Transaction t) {
            return new Pair<L, R>(this, t);
        }
    }
}
