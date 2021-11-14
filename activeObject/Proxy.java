package activeObject;

import javax.sound.midi.Soundbank;

public class Proxy implements IAsyncBuffer{

    private final Servant servant;
    private final ActivationQueue queue;
    private final Scheduler scheduler;

    public Proxy(int M){

        this.servant = new Servant(M);
        this.queue = new ActivationQueue();
        this.scheduler = new Scheduler(servant, queue);
        this.scheduler.start();
    }


    public Future<Void> put( int pId, int value ){

        Future<Void> response = new <Void>Future();
        IMethodRequest newRequest = new RequestPut(response,value,this.servant, pId);
        this.queue.enqueue(newRequest);
        return response;

    }

    public Future<Integer> get(int pId,  int value ){

        Future<Integer> response = new <Integer>Future();
        IMethodRequest newRequest = new RequestGet(response,value,this.servant, pId);
        this.queue.enqueue(newRequest);
        return response;

    }

    public void stop() throws  InterruptedException{
        this.scheduler.shouldStop = true;
        this.scheduler.interrupt();
        //System.out.println("interupted");
        this.scheduler.join();
        //System.out.println("joined");
    }
}
