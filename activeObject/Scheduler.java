package activeObject;

public class Scheduler extends Thread{

    private final Servant servant;
    private final Proxy proxy;
    private final ActivationQueue queue;

    public Scheduler(){
        super();

        this.servant = new Servant();
        this.proxy = new Proxy();
        this.queue = new ActivationQueue();

    }

    @Override
    public void run() {
        boolean shouldStop = false;


        while(!shouldStop){
            try{

                MethodRequest request = this.queue.dequeue();
                if(request.guard()) request.call();
                else this.queue.enqueue(request);

            }catch(InterruptedException e){
               shouldStop = true;
            }
        }

    }
}
