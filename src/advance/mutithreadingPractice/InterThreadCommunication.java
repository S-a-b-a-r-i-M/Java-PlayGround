package advance.mutithreadingPractice;

// Q/A
/*
Q:  What Happens If You Remove synchronized Keywords
A:  If you remove the synchronized keywords from both the produce() and consume() methods in the example, you'll encounter several critical issues:
    Immediate Technical Problem:
    java// Without synchronized
    public void consume() {
        while (!isDataReady) {
            try {
                wait(); // This will throw IllegalMonitorStateException
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Rest of the code...
    }
    Result: IllegalMonitorStateException will be thrown immediately when either method tries to call wait() or notify()
    Why This Happens:
    The wait(), notify(), and notifyAll() methods can only be called by a thread that "owns the monitor" (has the lock) on the object. Without synchronized, the thread never acquires this lock.
 */

public class InterThreadCommunication {
    private boolean isDataReady = false;
    private String data = null;

    public synchronized void produce(){
        System.out.println("Producer started");

        // Making Process
        try {
            Thread.sleep(1500);
        } catch (InterruptedException exp) {
            System.out.println(exp.getMessage());
        }

        isDataReady = true;
        data = "Laptop";

        // Notify waiting consumer thread
        notify();
        System.out.println("Producer notified the consumer");
    }

    public synchronized void consume() throws InterruptedException {
        System.out.println("Consumer started");

        // If data isn't ready, wait
        while (!isDataReady) {
            System.out.println("Data isn't ready by producer....");
            wait();
            System.out.println("Consumer woke up!");
        }

        System.out.println("Data received to consumer : " + data);
        isDataReady = false;
        data = null;
    }

    public static void main(String[] args) {
        InterThreadCommunication interThread = new InterThreadCommunication();

        // Create consumer thread
        Thread consumer = new Thread(() -> {
            try {
                interThread.consume();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        // Create producer thread
        Thread producer = new Thread(interThread::produce);

        consumer.start(); // Consumer will wait

        try {
            Thread.sleep(1000);
        } catch (InterruptedException exp){
            System.out.println(exp.getMessage());
        }

        producer.start(); // Producer will notify
    }
}
