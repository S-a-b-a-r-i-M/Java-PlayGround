package intermediate;

interface JavaCar {
    String brand = ""; // By default, public, static, and final.
    default void start() { // This be
        System.out.println(this.getClass().getName());
    }
}

interface Robot extends JavaCar {
    int power = 0; // By default, this is static final
    default void start(){
        System.out.println(this.getClass().getName());
        System.out.println("hello");
        System.out.println(brand);
    }
}

public class InterfacePractice implements Robot{

    @Override
    public void start() {
        Robot.super.start();
    }

    public static void main(String[] args) {
        InterfacePractice interfacePractice = new InterfacePractice();
        interfacePractice.start();

        System.out.println(InterfacePractice.power); // Accessing by class
    }
}
