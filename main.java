import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

// reader-priority this means if someone is reading lock the writer
public class Main {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        ReadWriteLock RW = new ReadWriteLock();

        executorService.execute(new Writer(RW));
        executorService.execute(new Writer(RW));
        executorService.execute(new Writer(RW));
        executorService.execute(new Writer(RW));

        executorService.execute(new Reader(RW));
        executorService.execute(new Reader(RW));
        executorService.execute(new Reader(RW));
        executorService.execute(new Reader(RW));

        executorService.shutdown();
    }
}

class ReadWriteLock {
    private final Semaphore readerSemaphore = new Semaphore(1);
    private final Semaphore writerSemaphore = new Semaphore(1);
    private int numberOfReaders = 0;

    public void readLock() {
        try {
            readerSemaphore.acquire();
        } catch (InterruptedException e) {
            System.out.println("interrupt occurred");
            Thread.currentThread().interrupt();
        }
        numberOfReaders++;
        if (numberOfReaders == 1) {
            try {
                writerSemaphore.acquire();
            } catch (InterruptedException e) {
                System.out.println("interrupt occurred");
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Reader reading. Total Readers = " + numberOfReaders);
        readerSemaphore.release();
    }

    public void writeLock() {
        try {
            writerSemaphore.acquire();
        } catch (InterruptedException e) {
            System.out.println("interrupt occurred");
            Thread.currentThread().interrupt();
        }
        System.out.println("Writer started writing");
    }

    public void readUnLock() {
        try {
            readerSemaphore.acquire();
        } catch (InterruptedException e) {
            System.out.println("interrupt occurred");
            Thread.currentThread().interrupt();
        }
        numberOfReaders--;
        System.out.println("Reader readed . Total Readers = " + numberOfReaders);
        if (numberOfReaders == 0) {
            writerSemaphore.release();
        }
        readerSemaphore.release();
    }

    public void writeUnLock() {
        System.out.println("Writer finished");
        writerSemaphore.release();
    }
}

class Writer implements Runnable {
    private final ReadWriteLock RW_lock;
    private volatile boolean running = true;

    public Writer(ReadWriteLock rw) {
        RW_lock = rw;
    }

    public void run() {
        while (running) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                System.out.println("interrupt occurred");
                Thread.currentThread().interrupt();
            }

            if (!running) {
                break;
            }
            System.out.println("Writer requesting permission");
            RW_lock.writeLock();
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                System.out.println("interrupt occurred");
                Thread.currentThread().interrupt();
            }
            RW_lock.writeUnLock();
        }
    }

    public void stop() {
        running = false;
    }
}

class Reader implements Runnable {
    private final ReadWriteLock RW_lock;
    private volatile boolean running = true;

    public Reader(ReadWriteLock rw) {
        RW_lock = rw;
    }

    public void run() {
        while (running) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                System.out.println("interrupt occurred");
                Thread.currentThread().interrupt();
            }
             if (!running) {
                break;
            }

            System.out.println("Reader asks permission");
            RW_lock.readLock();

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                System.out.println("interrupt occurred");
                Thread.currentThread().interrupt();
            }
            RW_lock.readUnLock();
        }
    }
    public void stop() {
        running = false;
    }
}

