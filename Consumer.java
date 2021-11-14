import activeObject.Future;
import activeObject.IAsyncBuffer;
import activeObject.Proxy;
import syncBuffer.IBuffer;

import java.util.Random;

public class Consumer extends Thread{

    private  IAsyncBuffer asyncBuffer;
    private  IBuffer buffer;
    private boolean bufferIsSync;
    private int offsideJobIterations = 100000;

    private static int counter=10000;
    private final int consumerId;
    private final boolean randomPortion;
    private int portion;
    private Random random;
    private boolean shouldStop = false;


    Consumer(IAsyncBuffer buffer, int seed, int offsideJobIterations){
        this.offsideJobIterations = offsideJobIterations;
        this.bufferIsSync = false;
        this.asyncBuffer = buffer;
        this.consumerId = ++counter;
        this.randomPortion = true;
        this.random = new Random(seed);
    }

    Consumer(IAsyncBuffer buffer, int seed, int portionSize, int offsideJobIterations){
        this.offsideJobIterations = offsideJobIterations;
        this.bufferIsSync = false;
        this.asyncBuffer = buffer;
        this.consumerId = ++counter;
        this.randomPortion = false;
        this.portion = portionSize;
        this.random = new Random(seed);
    }

    Consumer(IBuffer buffer, int seed, int offsideJobIterations){
        this.offsideJobIterations = offsideJobIterations;
        this.bufferIsSync = true;
        this.buffer = buffer;
        this.consumerId = ++counter;
        this.randomPortion = true;
        this.random = new Random(seed);
    }

    Consumer(IBuffer buffer, int seed, int portionSize, int offsideJobIterations){
        this.offsideJobIterations = offsideJobIterations;
        this.bufferIsSync = true;
        this.buffer = buffer;
        this.consumerId = ++counter;
        this.randomPortion = false;
        this.portion = portionSize;
        this.random = new Random(seed);
    }


    @Override
    public void run() {
        Future future = null;
        Double tmp = 10d;
        while(!this.shouldStop){

            if(this.randomPortion)
                this.portion = this.random.nextInt(100) + 1;

           // System.out.println("( C:" + this.consumerId + " ) Wait for get " + this.portion);

            try {
                //System.out.println("A");
                if(this.bufferIsSync) this.buffer.get(this.consumerId,this.portion);
                else future = asyncBuffer.get(this.consumerId, this.portion );

                for(int i =0 ; i < this.offsideJobIterations;i++){
                    tmp += Math.sin(tmp);
                }

                if(!this.bufferIsSync && future != null) future.get();
              //  System.out.println("( C:" + this.consumerId + " ) Just got " + this.portion);
            }catch (Exception e){;
                //System.out.println("Catch eception");
                break;
            }
        }
        System.out.println(tmp);
    }
}
