import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/** Bounded FIFO with separate producer and consumer conditions. */
public class ShoeWareHouse implements AutoCloseable {
    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition notEmpty = lock.newCondition();
    private final Condition notFull = lock.newCondition();
    private final Queue<Item> orders = new ArrayDeque<>();
    private final int capacity;
    private boolean closed;

    public ShoeWareHouse() { this(10); }
    public ShoeWareHouse(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.capacity = capacity;
    }
    public void receiveOrder(Item item) throws InterruptedException {
        Objects.requireNonNull(item, "item");
        lock.lockInterruptibly();
        try {
            while (orders.size() >= capacity && !closed) notFull.await();
            if (closed) throw new IllegalStateException("Warehouse is closed");
            orders.add(item);
            notEmpty.signal();
        } finally { lock.unlock(); }
    }
    /** Legacy convenience entry point; explicit item submission is preferred. */
    public void receiveOrder() throws InterruptedException {
        receiveOrder(new Item(0, Thread.currentThread().getName()));
    }
    /** Returns null only after closing and draining the queue. */
    public Item fulfillOrder() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (orders.isEmpty() && !closed) notEmpty.await();
            Item item = orders.poll();
            notFull.signal();
            return item;
        } finally { lock.unlock(); }
    }
    @Override
    public void close() {
        lock.lock();
        try {
            closed = true;
            notEmpty.signalAll();
            notFull.signalAll();
        } finally { lock.unlock(); }
    }
}