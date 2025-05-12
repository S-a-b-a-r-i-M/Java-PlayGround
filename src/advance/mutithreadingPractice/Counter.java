package advance.mutithreadingPractice;

public class Counter {
    public static int counter = 0;

    public static void increment(){
        for(int i = 0; i < 10_0000; i++) counter++;
    }

    public static void main(String[] args) throws InterruptedException {
        Thread threadOne = new Thread(Counter::increment);
        Thread threadTwo = new Thread(Counter::increment);

        threadOne.start();
        threadTwo.start();

        threadOne.join();
        threadTwo.join();

        System.out.println("Finally Counter value is : " + counter);
    }
}


