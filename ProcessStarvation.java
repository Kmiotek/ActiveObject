import activeObject.Proxy;
import basic.ThreadStatisticCollector;

import java.util.LinkedList;
import java.util.List;

public class ProcessStarvation {

    private static final int P = 3;
    private static final int K = 1;
    private static final int M = 100;
    private static final int C = 100;


    public static void main(String[] args) {

        Proxy buffer =  new Proxy(200, 10);
        List<Thread> workers = new LinkedList();

        workers.add(new Producer(buffer, 1,1));
        workers.add(new Producer(buffer, 2,1));
        workers.add(new Consumer(buffer, 3,1));
        workers.add(new Consumer(buffer, 4,1));
        workers.add(new Consumer(buffer, 5,1));
        workers.add(new Consumer(buffer, 6,1));
        workers.add(new Consumer(buffer, 7,10));

        workers.forEach(Thread::start);
        try
        {
            Thread.sleep(10000);
            buffer.stop();
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }

        for(Thread worker : workers){

            try{
                worker.interrupt();
                worker.join();
            }catch(Exception e){
                System.out.println("Exeption" + e);
            }
        }


        System.out.println(ThreadStatisticCollector.instance);

    }
}
