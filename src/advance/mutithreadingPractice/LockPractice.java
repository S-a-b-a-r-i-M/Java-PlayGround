package advance.mutithreadingPractice;

import java.util.EmptyStackException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class ConcurrentStackWithLocker <T> {
    private static final int DEFAULT_CAPACITY = 3;
    private final int maxCapacity;
    protected Object[] items;
    protected int lastPosition;
    ReentrantLock locker;
    private final Condition waitPop, waitPush;

    public ConcurrentStackWithLocker() {
        this(DEFAULT_CAPACITY);
    }

    public ConcurrentStackWithLocker(int maxCapacity){
        this.maxCapacity = maxCapacity;
        items = new Object[maxCapacity];
        locker = new ReentrantLock();
        waitPop = locker.newCondition();
        waitPush = locker.newCondition();
    }

    public boolean isEmpty() {
        return lastPosition == 0;
    }

    public boolean isFull() {
        return lastPosition == maxCapacity;
    }

    public T peek(){
        if (lastPosition == 0) throw new EmptyStackException();
        return (T) items[lastPosition-1];
    }

    public T push(T element) throws InterruptedException {
        System.out.println("Trying to push : " + element);
        try {
            locker.lock();
            while (isFull()) {
                System.out.println("pushing is waiting.....");
                waitPop.await();
            } // Wait for a pop operation

            System.out.println("Push : index=" + lastPosition + " element=" + element);
            items[lastPosition++] = element;

            waitPush.signalAll();
            return element;
        } finally {
            locker.unlock();
        }
    }

    public T pop() throws InterruptedException {
        System.out.println("Trying to pop");
        try {
            locker.lock();
            while (isEmpty()) {
                System.out.println("popping is waiting.....");
                waitPush.await();
            }

            --lastPosition;
            T item = (T)items[lastPosition];
            items[lastPosition] = null;
            System.out.println("Pop : index=last element="+item);
            waitPop.signalAll();
            return item;
        } finally {
            locker.unlock();
        }
    }

    public void display() {
        System.out.print("[");
        for (Object item : items) {
            System.out.print(item + " ");
        }
        System.out.println("]");
    }
}

public class LockPractice {

    public static void main(String[] args) {
        ConcurrentStackWithLocker<Integer> stackWithLocker = new ConcurrentStackWithLocker<>();

        new Thread(() -> {
            try {
//                Thread.sleep(2000); // Let's try to pop
                stackWithLocker.push(10);
                stackWithLocker.push(20);
                Thread.sleep(1000);
                stackWithLocker.push(30);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        new Thread(() -> {
            try {
//                stackWithLocker.pop();
                stackWithLocker.push(40);
                Thread.sleep(1000);
                stackWithLocker.pop();
//                stackWithLocker.pop();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
