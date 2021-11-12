package activeObject;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ActivationQueue {

    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition bothEmpty = lock.newCondition();
    private final Condition consumersEmpty = lock.newCondition();
    private final Condition producersEmpty = lock.newCondition();
    private final Condition someoneWaiting = lock.newCondition();

    private boolean isSomeoneWaiting = false;

    private final Queue<IMethodRequest> consumers = new LinkedBlockingQueue<>();
    private final Queue<IMethodRequest> producers = new LinkedBlockingQueue<>();

    public ActivationQueue() {

    }

    void enqueue(IMethodRequest methodRequest){
        if (methodRequest.isConsumer()){
            consumers.offer(methodRequest);
            bothEmpty.signal();
            consumersEmpty.signal();
        } else {
            producers.offer(methodRequest);
            bothEmpty.signal();
            producersEmpty.signal();
        }
    }

    IMethodRequest dequeue() throws  InterruptedException{
        lock.lock();

        IMethodRequest result = null;

        try {
            while (isSomeoneWaiting) {
                someoneWaiting.await();
            }

            isSomeoneWaiting = true;

            while (producers.isEmpty() && consumers.isEmpty()) {
                bothEmpty.await();
            }

            if (!consumers.isEmpty()) {
                result = dequeueWithFirstNotEmpty(consumers, producers, producersEmpty);
            } else {
                result = dequeueWithFirstNotEmpty(producers, consumers, consumersEmpty);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            isSomeoneWaiting = false;
            someoneWaiting.signal();
            lock.unlock();
        }
        return result;
    }

    private IMethodRequest dequeueWithFirstNotEmpty(Queue<IMethodRequest> queue1, Queue<IMethodRequest> queue2, Condition condition) throws InterruptedException {
        if (queue1.peek().guard()){
            return queue1.poll();
        } else {
            while (queue2.isEmpty()){
                condition.await();
            }
            return queue2.poll();
        }
    }

}
