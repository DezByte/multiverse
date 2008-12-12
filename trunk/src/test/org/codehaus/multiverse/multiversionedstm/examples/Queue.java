package org.codehaus.multiverse.multiversionedstm.examples;

import org.codehaus.multiverse.multiversionedstm.DehydratedStmObject;
import org.codehaus.multiverse.multiversionedstm.*;
import org.codehaus.multiverse.util.iterators.ArrayIterator;
import org.codehaus.multiverse.core.Transaction;

import java.util.Iterator;

public class Queue<E> implements StmObject {

    private Stack<E> readyToPopStack = new Stack<E>();
    private Stack<E> pushedStack = new Stack<E>();

    public E pop() {
        if (!readyToPopStack.isEmpty())
            return readyToPopStack.pop();

        while (!pushedStack.isEmpty()) {
            E item = pushedStack.pop();
            readyToPopStack.push(item);
        }

        return readyToPopStack.pop();
    }

    public void push(E value) {
        pushedStack.push(value);
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return readyToPopStack.size() + pushedStack.size();
    }

    //================== generated =================

    private long handle;
    private Transaction transaction;
    private DehydratedQueue initialDehydratedQueue;

    public DehydratedStmObject ___getInitialDehydratedStmObject() {
        return initialDehydratedQueue;
    }

    public void ___onAttach(Transaction transaction, long handle) {
        this.transaction = transaction;
        this.handle = handle;
    }

    public Transaction ___getTransaction() {
        return transaction;
    }

    public Iterator<StmObject> ___loadedMembers() {
        return new ArrayIterator<StmObject>(readyToPopStack, pushedStack);
    }

    public long ___getHandle() {
        return handle;
    }

    public DehydratedStmObject ___dehydrate() {
        return new DehydratedQueue(this);
    }

    public boolean ___isDirty() {
        if(initialDehydratedQueue == null)
            return true;

        return false;
    }

    public static class DehydratedQueue extends DehydratedStmObject {
        private final long readyToPopStackPtr;
        private final long pushedStackPtr;

        DehydratedQueue(Queue queue) {
            super(queue.___getHandle());
            this.readyToPopStackPtr = queue.readyToPopStack.___getHandle();
            this.pushedStackPtr = queue.pushedStack.___getHandle();
        }

        public Iterator<Long> members() {
            throw new RuntimeException();
        }

        public Queue hydrate(Transaction transaction) {
            Queue queue = new Queue();
            queue.handle = getHandle();
            queue.transaction = transaction;
            queue.readyToPopStack = (Stack) transaction.read(readyToPopStackPtr);
            queue.pushedStack = (Stack) transaction.read(pushedStackPtr);
            queue.initialDehydratedQueue = this;
            return queue;
        }
    }
}
