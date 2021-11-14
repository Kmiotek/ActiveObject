package activeObject;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Future<T> {

    protected T value;
    protected boolean isAvailable;
    private Lock lock = new ReentrantLock();
    private Condition dataIsAvailable = lock.newCondition();


    protected void setData(T data){
        lock.lock();
        this.value = data;
        this.isAvailable = true;
        dataIsAvailable.signal();
        lock.unlock();
    }

    public boolean isAvailable(){
        boolean result  = this.isAvailable;
        return  result;
    }

    public T get() throws InterruptedException{
        lock.lock();
        try{
            if(!this.isAvailable)
                dataIsAvailable.await();

        }finally{
            lock.unlock();
        }
        return this.value;
    }

}
