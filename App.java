import activeObject.Proxy;
import basic.ThreadStatisticCollector;

import java.util.LinkedList;
import java.util.List;

public class App {

    private static final int P = 3;
    private static final int K = 1;
    private static final int M = 100;
    private static final int C = 100;


    public static void main(String[] args) {

        Proxy buffer =  new Proxy(200);
        List<Thread> workers = new LinkedList();

        workers.add(new Producer(buffer, 0,1));
        workers.add(new Producer(buffer, 0,1));
        workers.add(new Consumer(buffer, 0,1));
        workers.add(new Consumer(buffer, 0,1));
        workers.add(new Consumer(buffer, 0,1));
        workers.add(new Consumer(buffer, 0,1));
        workers.add(new Consumer(buffer, 0,10));

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

//        for(int i = 0 ; i < P ;i++)
//            workers.add(new Producer(buffer, i));
//        for(int i = 0 ; i < K ;i++)
//            workers.add(new Consumer(buffer, i + 10000));


    }
}
