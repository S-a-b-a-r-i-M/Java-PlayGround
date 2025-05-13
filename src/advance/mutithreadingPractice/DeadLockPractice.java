package advance.mutithreadingPractice;

public class DeadLockPractice {

    public static void main(String[] args) throws InterruptedException {
        String objectA = "objectA", objectB = "objectB";

        Thread dhoni = new Thread(() -> {
            synchronized (objectA){
                System.out.println("Dhoni: locked objectA");
                try {
                    Thread.sleep(100);
                } catch (Exception ignored){
                }

                // Locking 2nd Object -- need for 2nd object
                synchronized (objectB) {
                    System.out.println("Dhoni: locked objectB");
                }
            }
        });

        Thread rohit = new Thread(() -> {
            synchronized (objectB){
                System.out.println("Rohit: locked objectB");
                try {
                    Thread.sleep(100);
                } catch (Exception ignored){
                }

                // Locking ObjectA -- need for objectA
                synchronized (objectA) {
                    System.out.println("Rohit: locked objectA");
                }
            }
        });

        dhoni.start();
        rohit.start();

        dhoni.join();
        rohit.join();
    }
}
