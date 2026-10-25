# Java Concurrent Warehouse

A small, runnable Java concurrency project demonstrating ReentrantLock, Condition, and executor lifecycle management. It is an educational in-memory application, with no external dependencies.

## What it demonstrates

- A bounded FIFO queue: producers wait when full and consumers wait when empty.
- Multiple consumers sharing work without duplicate delivery.
- Condition checks in loops to handle spurious wakeups.
- Cooperative interruption, explicit shutdown, and draining accepted work.
- No simulated I/O or sleeps inside the critical section.

## Run locally

Requires JDK 17 or newer. Run from the repository root:

~~~sh
mkdir out
javac --release 17 -d out src/*.java test/*.java
java -cp out WarehouseDemo
java -cp out ConcurrencyChecks
~~~

The demo produces 20 items and distributes them across four consumers. Completion order and worker names vary; the final line confirms all 20 were consumed.

## Design

ShoeWareHouse uses a fair ReentrantLock and separate notEmpty and notFull conditions. Producers signal consumers after enqueueing; consumers signal producers after dequeueing. close signals both conditions. The legacy main and Main2 entry points delegate to WarehouseDemo. Store remains a separate, single-threaded inventory example.

Closing is idempotent. New submissions after close throw IllegalStateException. Consumers drain previously accepted items, then receive null. Blocking methods propagate InterruptedException so callers decide how to cancel work.

This queue is process-local: it does not persist orders, retry failed business operations, or provide distributed delivery guarantees. FIFO applies to removal from the queue, not to the order workers finish processing.

## Verification

The dependency-free checks exercise FIFO order, invalid capacity, 300 unique deliveries from three producers to four consumers, bounded backpressure, interruption of blocked producers and consumers, and waking both kinds of worker on close. Deadlines turn hangs into failures. GitHub Actions compiles and runs these checks on JDK 17.

See [the design notes](docs/design.md) for invariants and tradeoffs.