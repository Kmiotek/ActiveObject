import activeObject.Proxy;
import basic.ThreadStatisticCollector;

import java.util.LinkedList;
import java.util.List;

public class NoActiveWaiting {

    public static void main(String[] args) {

        Proxy buffer =  new Proxy(200, 100);
        List<Thread> workers = new LinkedList();

        for(int i = 0 ; i < 30 ; i ++){
            Thread newConsumer = new Consumer(buffer,1,1);
            newConsumer.start();
            workers.add(newConsumer);
            System.out.println("Added Consumer");
            try
            {
                Thread.sleep(1000);

            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
        }

        Thread newConsumer = new Producer(buffer,1,1);
        newConsumer.start();
        workers.add(newConsumer);
        System.out.println("Added Producer");
        try
        {
            Thread.sleep(10000);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }
}
