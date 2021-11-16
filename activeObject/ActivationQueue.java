package activeObject;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

// Do zbudowania ActivationQueue wykorzystałem 2 kolejki dwukierunkowe. Na jednej znajdują się zapytania o wykonanie
// metody Get a na drugiej zapytania o Put.  Zawsze najpierw wykonywane jest zapytanie (w obrębie kolejki) najstarsze
// co zapobiega zagłodzeniu. Wzbogaciłem ActivactionQueue o metodę  refund która "zwraca" pobrany element z powrotem do
// kolejki. Zwracając zapamiętujemy do której kolejki zwracaliśmy by nie pobierać w kółko tego samego zapytania, tylko
// poczekać na zapytanie z kolejki przeciwnej.

public class ActivationQueue {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition dataEntered = lock.newCondition();

    private boolean wasGetRequestReturned = false;
    private boolean wasPutRequestReturned = false;
    private Integer iter = 0;

    private final Deque<RequestGet> getRequests = new LinkedList<>();
    private final Deque<RequestPut> putRequests = new LinkedList<>();

    void enqueue(IMethodRequest methodRequest){
        System.out.println("Try to get lock: " );
        lock.lock();
        try{

            if(methodRequest instanceof RequestGet) {
                System.out.println("Get");
                getRequests.add((RequestGet) methodRequest);
                if( !wasGetRequestReturned) dataEntered.signal();
            }

            if(methodRequest instanceof RequestPut) {
                System.out.println("Put");
                putRequests.add((RequestPut) methodRequest);
                if( !wasPutRequestReturned) dataEntered.signal();
            }
        }finally{
            lock.unlock();
        }
    }

    IMethodRequest dequeue() throws  InterruptedException{
        IMethodRequest result = null;
        lock.lock();
        try{
            while(true){
                for(int i = 0 ; i < 2 ; i++){
                    System.out.println("Dequeue Iteration");

                    iter+=1;
                    iter%=2;

                    if(iter == 0){
                        if(getRequests.size() > 0 && !wasGetRequestReturned ){
                            result = getRequests.poll();
                            break;
                        }

                    }else {
                        if(putRequests.size() > 0 && !wasPutRequestReturned ){
                            result = putRequests.poll();
                            break;
                        }
                    }
                }

                if(result == null) {
                    System.out.println("Sleep");
                    dataEntered.await();

                }else{
                    break;
                }

            }

            wasGetRequestReturned = false;
            wasPutRequestReturned = false;

        }finally {
            lock.unlock();
        }

        return result;
    }

    void refund( IMethodRequest methodRequest){
        lock.lock();
        try{
            if(methodRequest instanceof RequestGet) {
                System.out.println("Return Get");
                getRequests.addFirst((RequestGet) methodRequest);
                wasGetRequestReturned = true;
            }

            if(methodRequest instanceof RequestPut) {
                System.out.println("Return Put");
                putRequests.addFirst((RequestPut) methodRequest);
                wasPutRequestReturned = true;

            }
        }finally {
            lock.unlock();
        }
    }

}
