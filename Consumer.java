import activeObject.Future;
import activeObject.Proxy;

import java.util.Random;

public class Consumer extends Thread{

    private final Proxy buffer;
    private static int counter=10000;
    private final int consumerId;
    private final boolean randomPortion;
    private int portion;
    private Random random;

    Consumer(Proxy buffer, int seed){
        this.buffer = buffer;
        this.consumerId = ++counter;
        this.randomPortion = true;
        this.random = new Random(seed);
    }

//    Consumer(IBuffer buffer, int seed, int portionSize){
//        this.buffer = buffer;
//        this.consumerId = ++counter;
//        this.randomPortion = false;
//        this.portion = portionSize;
//        this.random = new Random(seed);
//    }


    @Override
    public void run() {

        while(true){

            if(this.randomPortion)
                this.portion = this.random.nextInt(100) + 1;

            System.out.println("( C:" + this.consumerId + " ) Wait for get " + this.portion);

            try {

                Future future = buffer.get(this.portion);
                future.get();
                System.out.println("( C:" + this.consumerId + " ) Just got " + this.portion);
            }catch (Exception e){;
                break;
            }





        }

    }
}
