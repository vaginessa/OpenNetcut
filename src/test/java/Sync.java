/**
 * Created by root on 3/20/17.
 */
public class Sync {

    public static void main(String[] args) {
        Sync s = new Sync();
        s.printOut();
    }


    int counter = 0;

    void printOut() {
        Thread t1 = new Thread(() -> {
            for (int i=0; i<100000; i++) {
                add();
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i=0; i<100000; i++) {
                sub();
            }
        });
        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(counter);
    }

    synchronized void add() {
        counter++;
    }

    synchronized void sub() {
        counter--;
    }

}
