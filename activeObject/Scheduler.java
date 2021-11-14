package activeObject;

public class Scheduler extends Thread{



    private final ActivationQueue queue;
    protected boolean shouldStop = false;

    public Scheduler(Servant servant, ActivationQueue queue){
        super();
        this.queue = queue;

    }

    @Override
    public void run() {


      //  System.out.println("Scheduler start");
        while(!shouldStop){
            try{
           //     System.out.println("Scheduler try get Task");
                IMethodRequest request = this.queue.dequeue();
           //     System.out.println("Scheduler  get Task!!!");
                if(request.guard()) request.call();
                else this.queue.refund(request);





            }catch(InterruptedException e){//System.out.println("Get interrupt");
               shouldStop = true;
            }
        }

    }
}
