import activeObject.Future;
import activeObject.Proxy;

import java.util.Random;

public class Producer extends Thread{
    private final Proxy buffer;
    private static int counter=20000;
    private final int producerId;
    private final boolean randomPortion;
    private int portion;
    private Random random;

    Producer(Proxy buffer, int seed){
        this.buffer = buffer;
        this.producerId = ++counter;
        this.randomPortion = true;
        this.random = new Random(seed);
    }

    Producer(Proxy buffer, int seed , int portionSize){
        this.buffer = buffer;
        this.producerId = ++counter;
        this.randomPortion = false;
        this.portion = portionSize;
        this.random = new Random(seed);

    }

    @Override
    public void run() {

        while(true){

            if(this.randomPortion)
                this.portion = this.random.nextInt(100) + 1;

            System.out.println("( P:" + this.producerId + " ) Wait for add " + this.portion);
            try {
                Future future = buffer.put(this.portion);
                future.get();
                System.out.println("( P:" + this.producerId + " ) Just add " + this.portion);
            }catch (Exception e){;
                break;
            }




        }

    }
}
