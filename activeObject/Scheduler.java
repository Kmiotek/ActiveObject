package activeObject;

public class Scheduler extends Thread{

    private final Servant servant;

    private final ActivationQueue queue;

    public Scheduler(Servant servant, ActivationQueue queue){
        super();

       this.servant = servant;
       this.queue = queue;

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
