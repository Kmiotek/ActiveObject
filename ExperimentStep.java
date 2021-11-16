import activeObject.IAsyncBuffer;
import activeObject.Proxy;
import basic.ThreadStatisticCollector;
import syncBuffer.Buffer3Lock;
import syncBuffer.IBuffer;

import java.util.LinkedList;
import java.util.List;

public class ExperimentStep {


    private List<Thread> workers;
    private ThreadStatisticCollector statistic;

    public ExperimentStep(int K, int P, int M , int C, boolean syncBuffer){
        this.workers = new LinkedList<>();
       if(syncBuffer) {
           IBuffer buffer = new Buffer3Lock(M);
           for(int i = 0 ; i < K ;i++)
               workers.add(new Producer(buffer, i, C));
           for(int i = 0 ; i < P ;i++)
               workers.add(new Consumer(buffer, i, C));
       }
       else{
           IAsyncBuffer buffer = new Proxy(M);
           for(int i = 0 ; i < K ;i++)
               workers.add(new Producer(buffer, i, C));
           for(int i = 0 ; i < P ;i++)
               workers.add(new Consumer(buffer, i, C));
        }

    }

    void  start(){
        workers.forEach(Thread::start);

        try
        {
            Thread.sleep(5000);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }

        for(Thread worker : workers){

            try{
                worker.interrupt();
                //System.out.println("Interrupted");
                worker.join();
               // System.out.println("Joined");
            }catch(Exception e){
                System.out.println("Exeption" + e);
            }
        }
    }


}
