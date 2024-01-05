import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

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
    private final Semaphore S = new Semaphore(1);
    private final Semaphore sd = new Semaphore(1);
    private int numberOfReaders = 0;

    public void readLock() {
        try {
            S.acquire();
        } catch (InterruptedException e) {
            System.out.println("interrupt occurred");
            Thread.currentThread().interrupt();
        }
        numberOfReaders++;
        if (numberOfReaders == 1) {
            try {
                sd.acquire();
            } catch (InterruptedException e) {
                System.out.println("interrupt occurred");
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Reader is reading. Number of Readers = " + numberOfReaders);
        S.release();
    }

    public void writeLock() {
        try {
            sd.acquire();
        } catch (InterruptedException e) {
            System.out.println("interrupt occurred");
            Thread.currentThread().interrupt();
        }
        System.out.println("Writer is now writing");
    }

    public void readUnLock() {
        try {
            S.acquire();
        } catch (InterruptedException e) {
            System.out.println("interrupt occurred");
            Thread.currentThread().interrupt();
        }
        numberOfReaders--;
        System.out.println("Reader finished reading. Number of Readers = " + numberOfReaders);
        if (numberOfReaders == 0) {
            sd.release();
        }
        S.release();
    }

    public void writeUnLock() {
        System.out.println("Writer finished writing");
        sd.release();
    }
}

 class Writer implements Runnable {
    private final ReadWriteLock RW_lock;

    public Writer(ReadWriteLock rw) {
        RW_lock = rw;
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                System.out.println("interrupt occurred");
                Thread.currentThread().interrupt();

            }
            System.out.println("A writer asks permission to write");
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
}

class Reader implements Runnable {
    private final ReadWriteLock RW_lock;

    public Reader(ReadWriteLock rw) {
        RW_lock = rw;
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                System.out.println("interrupt occurred");
                Thread.currentThread().interrupt();
            }
            System.out.println("A reader asks permission to read");
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
}

