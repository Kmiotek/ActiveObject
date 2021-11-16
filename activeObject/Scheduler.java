package activeObject;


// Scheduler wywołuje zapytania dodawane do kolejki przez Proxy. Przez konstrukcje kolejki po pobraniu musi sprawdzić
// czy może wykonać dane zapytanie, ponieważ kolejka nie sprawdza czy zapytanie może zostać obsłużone. Jeśli nie może
// zapytania obsłużyć zwraca je z powrotem do kolejki

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

                try{
                    if (this.delay > 0)
                    Thread.sleep(this.delay);
                }catch (InterruptedException e){
                    System.out.println(e);
                }

                if(request.guard()) request.call();
                else this.queue.refund(request);

            }catch(InterruptedException e){//System.out.println("Get interrupt");
             //   System.out.println("Get InterruptedException");
                shouldStop = true;
            }
        }

    }
}
