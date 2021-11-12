import activeObject.Proxy;

import java.util.LinkedList;
import java.util.List;

public class App {

    private static final int P = 1;
    private static final int K = 1;
    private static final int M = 100;

    public static void main(String[] args) {

        Proxy buffer =  new Proxy();
        List<Thread> workers = new LinkedList();

        for(int i = 0 ; i < P ;i++)
            workers.add(new Producer(buffer, i));
        for(int i = 0 ; i < K ;i++)
            workers.add(new Consumer(buffer, i + 1));

        workers.forEach(Thread::start);

    }
}
