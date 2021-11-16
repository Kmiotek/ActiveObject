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

    private final Queue<IMethodRequest> consumers = new LinkedBlockingQueue<>();
    private final Queue<IMethodRequest> producers = new LinkedBlockingQueue<>();

    // Mój pomysł na kolejkę wykorzystuje dwie kolejki: jedną dla konsumentów i drugą dla producentów.
    // Jeżeli obie kolejki są pełne to możemy zawsze wykonać zadanie z jednej z nich, a jeżeli jedna z kolejek jest
    // pusta a w drugiej kolejce nie możemy zdjąć ostatniego elementu to czekamy na dodanie elementów do pierwszej
    // kolejki.

    public ActivationQueue() {

    }

    void enqueue(IMethodRequest methodRequest){
        lock.lock();                    // Używam locka ponieważ jeżeli wątek konsumenta lub producenta próbowałby
                                        // dodać element w trakcie kiedy Scheduler pobierałby element z kolejki
                                        // to mogłoby dojść do błędów.
        if (methodRequest.isConsumer()){
            consumers.offer(methodRequest);
            bothEmpty.signal();                     // Informuję odpowiednie condition o wstawieniu do kolejki.
            consumersEmpty.signal();
        } else {
            producers.offer(methodRequest);
            bothEmpty.signal();
            producersEmpty.signal();
        }
        lock.unlock();
    }

    IMethodRequest dequeue() throws  InterruptedException{
        lock.lock();

        IMethodRequest result = null;

        try {

            while (producers.isEmpty() && consumers.isEmpty()) {        // Jeżeli w obu kolejkach nie ma nic to musimy czekać.
                bothEmpty.await();
            }

            if (!consumers.isEmpty()) {             // Znajdujemy niepustą kolejkę (druga kolejka także może być niepusta,
                                                    // nie zmienia to algorytmu)
                result = dequeueWithFirstNotEmpty(consumers, producers, producersEmpty);
            } else {
                result = dequeueWithFirstNotEmpty(producers, consumers, consumersEmpty);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return result;
    }

    private IMethodRequest dequeueWithFirstNotEmpty(Queue<IMethodRequest> queue1, Queue<IMethodRequest> queue2, Condition condition) throws InterruptedException {
        if (queue1.peek().guard()){     // Wiemy że kolejka 1 nie jest pusta
            return queue1.poll();       // Jeżeli możemy zabrać element z pierwszej kolejki to robimy to
        } else {                        // Jeżeli nie to znaczy że będziemy musieli na pewno zabrać element z kolejki 2,
                                        // żeby kolejka 1 się odblokowała
            while (queue2.isEmpty()){   // Jeżeli nie ma nic w kolejce 2 to musimy czekać
                condition.await();
            }
            return queue2.poll();       // Nie musimy sprawdzać warunku bo wiemy że zawsze musimy móc albo dodać albo
                                        // zabrać z bufora.
        }
    }

}