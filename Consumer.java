import activeObject.Future;
import activeObject.IAsyncBuffer;
import activeObject.Proxy;
import basic.ThreadStatisticCollector;
import syncBuffer.IBuffer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Consumer extends Thread{

    private  IAsyncBuffer asyncBuffer;
    private  IBuffer buffer;
    private final boolean bufferIsSync;
    private int offsideJobIterations = 100000;

    private static int counter=10000;
    private final int consumerId;
    private final boolean randomPortion;
    private int portion;
    private Random random;
    private boolean shouldStop = false;

    private ThreadStatisticCollector collector = null;


    Consumer(IAsyncBuffer buffer, int seed, int offsideJobIterations) {
        this.offsideJobIterations = offsideJobIterations;
        this.bufferIsSync = false;
        this.asyncBuffer = buffer;
        this.consumerId = ++counter;
        this.randomPortion = true;
        this.random = new Random(seed);
    }

    Consumer(IAsyncBuffer buffer, int seed, int offsideJobIterations, ThreadStatisticCollector collector){
        this(buffer, seed, offsideJobIterations);
        this.collector = collector;
    }

    Consumer(IAsyncBuffer buffer, int seed, int portionSize, int offsideJobIterations, ThreadStatisticCollector collector){
        this.offsideJobIterations = offsideJobIterations;
        this.bufferIsSync = false;
        this.asyncBuffer = buffer;
        this.consumerId = ++counter;
        this.randomPortion = false;
        this.portion = portionSize;
        this.random = new Random(seed);
        this.collector = collector;
    }

    Consumer(IBuffer buffer, int seed, int offsideJobIterations){
        this.offsideJobIterations = offsideJobIterations;
        this.bufferIsSync = true;
        this.buffer = buffer;
        this.consumerId = ++counter;
        this.randomPortion = true;
        this.random = new Random(seed);
    }

    Consumer(IBuffer buffer, int seed, int offsideJobIterations, ThreadStatisticCollector collector){
        this(buffer, seed, offsideJobIterations);
        this.collector = collector;
    }

    Consumer(IBuffer buffer, int seed, int portionSize, int offsideJobIterations, ThreadStatisticCollector collector){
        this.offsideJobIterations = offsideJobIterations;
        this.bufferIsSync = true;
        this.buffer = buffer;
        this.consumerId = ++counter;
        this.randomPortion = false;
        this.portion = portionSize;
        this.random = new Random(seed);
        this.collector = collector;
    }


    @Override
    public void run() {
        Future future = null;
        Double tmp = 10.0 + consumerId;
        while(!this.shouldStop){

            if(this.randomPortion)
                this.portion = this.random.nextInt(100) + 1;
            try {
                if(this.bufferIsSync) this.buffer.get(this.consumerId,this.portion);
                else future = asyncBuffer.get(this.consumerId, this.portion );

                for(int i =0 ; i < this.offsideJobIterations;i++){
                    tmp += Math.sin(tmp + 123);
                    if (!this.bufferIsSync && future != null && future.isAvailable()){
                        future.get();
                    }
                    if (this.isInterrupted()){
                        shouldStop = true;
                        break;
                    }
                }

                if(!this.bufferIsSync && future != null) future.get();
            }catch (Exception e){;
                break;
            }
        }
    }
}
