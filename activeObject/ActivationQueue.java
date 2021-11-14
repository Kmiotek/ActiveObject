package activeObject;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ActivationQueue {

    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition dataEntered = lock.newCondition();

    private boolean wasGetRequestReturned = false;
    private boolean wasPutRequestReturned = false;
    private Integer iter = 0;

    private final Deque<RequestGet> getRequests = new LinkedList<>();
    private final Deque<RequestPut> putRequests = new LinkedList<>();


    public ActivationQueue() {
    }

    void enqueue(IMethodRequest methodRequest){
        lock.lock();
        if(methodRequest instanceof RequestGet) {
            getRequests.add((RequestGet) methodRequest);
            if( !wasGetRequestReturned) dataEntered.signal();
        }

        if(methodRequest instanceof RequestPut) {
            putRequests.add((RequestPut) methodRequest);
            if( !wasPutRequestReturned) dataEntered.signal();
        }
        lock.unlock();
    }

    IMethodRequest dequeue() throws  InterruptedException{
        lock.lock();
        IMethodRequest result = null;

        while(true){
            for(int i = 0 ; i < 2 ; i++){
               // System.out.println("Iter:" + iter);
                if(iter == 0){
                    if(getRequests.size() > 0 && !wasGetRequestReturned ){
                        result = getRequests.poll();
                        //System.out.println("Get");
                        iter+=1;
                        iter%=2;
                        break;
                    }

                }else {
                    if(putRequests.size() > 0 && !wasPutRequestReturned ){
                        result = putRequests.poll();
                        //System.out.println("Put");
                        iter+=1;
                        iter%=2;
                        break;
                    }
                }

                iter+=1;
                iter%=2;
            }
            if(result == null) {
               // System.out.println("Im waiting");
                dataEntered.await();
            }else{
                break;
            }

        }


        wasGetRequestReturned = false;
        wasPutRequestReturned = false;

        lock.unlock();
        return result;
    }

    void refund( IMethodRequest methodRequest){
        lock.lock();
        //System.out.println("Refund");
        if(methodRequest instanceof RequestGet) {
            getRequests.addFirst((RequestGet) methodRequest);
            wasGetRequestReturned = true;
        }

        if(methodRequest instanceof RequestPut) {
            putRequests.addFirst((RequestPut) methodRequest);
            wasPutRequestReturned = true;

        }
        lock.unlock();
    }

}
