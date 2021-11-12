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

        System.out.println("Scheduler start");
        while(!shouldStop){
            try{
                System.out.println("Scheduler try get Task");
                IMethodRequest request = this.queue.dequeue();
                System.out.println("Scheduler  get Task!!!");
                System.out.println(request.guard());
                if(request.guard()) request.call();
                else this.queue.enqueue(request);

            }catch(InterruptedException e){
               shouldStop = true;
            }
        }

    }
}
