package activeObject;

public class Proxy {



    private final Servant servant;
    private final ActivationQueue queue;
    private final Scheduler scheduler;

    public Proxy(){

        this.servant = new Servant();
        this.queue = new ActivationQueue();
        this.scheduler = new Scheduler(servant, queue);
        this.scheduler.start();
    }


    Future<Void> put( int value ){

        Future<Void> response = new <Void>Future();
        IMethodRequest newRequest = new RequestPut(response,value,this.servant);
        this.queue.enqueue(newRequest);
        return response;

    }

    Future<Integer> get( int value ){

        Future<Integer> response = new <Integer>Future();
        IMethodRequest newRequest = new RequestGet(response,value,this.servant);
        this.queue.enqueue(newRequest);
        return response;

    }
}
