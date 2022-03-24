import activeObject.Future;
import activeObject.IAsyncBuffer;
import activeObject.Proxy;
import syncBuffer.IBuffer;

import java.util.Random;

public class Producer extends Thread{
    private IAsyncBuffer asyncBuffer;
    private IBuffer buffer;
    private final boolean bufferIsSync;
    private int offsideJobIterations = 100;

    private static int counter=20000;
    private final int producerId;
    private final boolean randomPortion;
    private int portion;
    private Random random;
    private boolean shouldStop = false;

    Producer(IAsyncBuffer buffer, int seed, int offsideJobIterations){
        this.offsideJobIterations = offsideJobIterations;
        this.asyncBuffer = buffer;
        this.bufferIsSync = false;
        this.producerId = ++counter;
        this.randomPortion = true;
        this.random = new Random(seed);
    }

    Producer(IAsyncBuffer buffer, int seed , int portionSize, int offsideJobIterations){
        this.offsideJobIterations = offsideJobIterations;
        this.asyncBuffer = buffer;
        this.bufferIsSync = false;
        this.producerId = ++counter;
        this.randomPortion = false;
        this.portion = portionSize;
        this.random = new Random(seed);

    }

    Producer(IBuffer buffer, int seed, int offsideJobIterations){
        this.offsideJobIterations = offsideJobIterations;
        this.buffer = buffer;
        this.bufferIsSync = true;
        this.producerId = ++counter;
        this.randomPortion = true;
        this.random = new Random(seed);
    }

    Producer(IBuffer buffer, int seed , int portionSize, int offsideJobIterations){
        this.offsideJobIterations = offsideJobIterations;
        this.buffer = buffer;
        this.bufferIsSync = true;
        this.producerId = ++counter;
        this.randomPortion = false;
        this.portion = portionSize;
        this.random = new Random(seed);

    }

    @Override
    public void interrupt() {
        super.interrupt();
        this.shouldStop = true;
    }

    @Override
    public void run() {
        Future future = null;
        Double tmp = 11.0 + producerId;
        while(!this.shouldStop) {
            if (this.randomPortion)
                this.portion = this.random.nextInt(100) + 1;
            try {
                if (this.bufferIsSync) this.buffer.put(this.producerId, portion);
                else future = asyncBuffer.put(this.producerId, this.portion);

                for (int i = 0; i < this.offsideJobIterations; i++) {
                    tmp += Math.sin(tmp);
                    if (this.isInterrupted()){
                        shouldStop = true;
                        break;
                    }
                }



                if (!this.bufferIsSync) future.get();
            } catch (Exception e) {
                break;
            }

        }
        //System.out.println(tmp);
    }
}
