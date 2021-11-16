package activeObject;


// Scheduler wywołuje zapytania dodawane do kolejki przez Proxy.

public class Scheduler extends Thread{

    private final ActivationQueue queue;
    protected boolean shouldStop = false;
    private Integer delay = -1;

    public Scheduler( ActivationQueue queue){
        super();
        this.queue = queue;
        super.setPriority(Thread.MAX_PRIORITY);

    }

    public Scheduler( ActivationQueue queue, Integer delay){
        super();
        this.queue = queue;
        this.delay = delay;
        super.setPriority(Thread.MAX_PRIORITY);
    }

    @Override
    public void run() {

        while(!shouldStop){
            try{
              //  System.out.println("Scheduler try get Task");
                IMethodRequest request = this.queue.dequeue();
              //  System.out.println("Scheduler  get Task!!!");

                request.call();         // Nie sprawdzamy czy możemy wykonać request bo kolejka nam to gwarantuje.

            }catch(InterruptedException e){//System.out.println("Get interrupt");
             //   System.out.println("Get InterruptedException");
                shouldStop = true;
            }
        }

    }
}
