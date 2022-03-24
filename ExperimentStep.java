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

    public ExperimentStep(int producers, int consumers, int buff, int iterations, boolean syncBuffer){
        this.workers = new LinkedList<>();
       if(syncBuffer) {
           IBuffer buffer = new Buffer3Lock(buff);
           for(int i = 0 ; i < producers ;i++)
               workers.add(new Producer(buffer, i, iterations));
           for(int i = 0 ; i < consumers ;i++)
               workers.add(new Consumer(buffer, i, iterations, statistic));
       }
       else{
           IAsyncBuffer buffer = new Proxy(buff);
           for(int i = 0 ; i < producers ;i++)
               workers.add(new Producer(buffer, i, iterations));
           for(int i = 0 ; i < consumers ;i++)
               workers.add(new Consumer(buffer, i, iterations, statistic));
        }

    }

    void  start(){
        workers.forEach(Thread::start);

        try
        {
            Thread.sleep(500);
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
