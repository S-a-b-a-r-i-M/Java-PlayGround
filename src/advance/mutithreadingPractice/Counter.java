package advance.mutithreadingPractice;

/*
 * Syntax : synchronized(sync_object)
    {
       // Access shared variables and other
       // shared resources
    }
 *
 */

public class Counter {
    public static int counter = 0;

    public static void increment(){
        for(int i = 0; i < 1_00_000; i++) counter++;
    }

    // Static Method - Method Level
    public synchronized static void syncIncrement(){
        for(int i = 0; i < 1_00_000; i++) counter++;
    }


   // Static Method - Block Level
    public static void syncIncrementB(){
        synchronized(Counter.class) {
            for (int i = 0; i < 1_00_000; i++) counter++;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread threadOne = new Thread(Counter::syncIncrement);
        Thread threadTwo = new Thread(Counter::syncIncrement);

        threadOne.start();
        threadTwo.start();

        threadOne.join();
        threadTwo.join();

        System.out.println("Finally Counter value is : " + counter);
    }
}


