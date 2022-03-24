package activeObject;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ActivationQueue {

    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition bothEmpty = lock.newCondition();
    private final Condition consumersEmpty = lock.newCondition();
    private final Condition producersEmpty = lock.newCondition();

    private final LinkedQueue<IMethodRequest> consumers = new LinkedQueue<>();
    private final LinkedQueue<IMethodRequest> producers = new LinkedQueue<>();

    private boolean turn = true;

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
            consumers.enqueue(methodRequest);
            bothEmpty.signal();                     // Informuję odpowiednie condition o wstawieniu do kolejki.
            consumersEmpty.signal();
        } else {
            producers.enqueue(methodRequest);
            bothEmpty.signal();
            producersEmpty.signal();
        }
        lock.unlock();
    }

    IMethodRequest dequeue() throws  InterruptedException{
        turn = !turn;
        if (turn) {
            return dequeueWithQueue1Preferred(producers, consumers, producersEmpty, consumersEmpty);
        } else {
            return dequeueWithQueue1Preferred(consumers, producers, consumersEmpty, producersEmpty);
        }
    }


    private IMethodRequest dequeueWithQueue1Preferred(LinkedQueue<IMethodRequest> queue1, LinkedQueue<IMethodRequest> queue2,
                                                      Condition condition1, Condition condition2){
        lock.lock();

        IMethodRequest result = null;

        try {

            while (queue1.isEmpty() && queue2.isEmpty()) {        // Jeżeli w obu kolejkach nie ma nic to musimy czekać.
                bothEmpty.await();
            }

            if (!queue2.isEmpty()) {             // Znajdujemy niepustą kolejkę (druga kolejka także może być niepusta,
                // nie zmienia to algorytmu)
                result = dequeueWithFirstNotEmpty(queue2, queue1, condition1);
            } else {
                result = dequeueWithFirstNotEmpty(queue1, queue2, condition2);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return result;
    }

    private IMethodRequest dequeueWithFirstNotEmpty(LinkedQueue<IMethodRequest> queue1, LinkedQueue<IMethodRequest> queue2,
                                                    Condition condition) throws InterruptedException {
        if (queue1.peek().guard()){     // Wiemy że kolejka 1 nie jest pusta
            return queue1.dequeue();       // Jeżeli możemy zabrać element z pierwszej kolejki to robimy to
        } else {                        // Jeżeli nie to znaczy że będziemy musieli na pewno zabrać element z kolejki 2,
                                        // żeby kolejka 1 się odblokowała
            while (queue2.isEmpty()){   // Jeżeli nie ma nic w kolejce 2 to musimy czekać
                condition.await();
            }
            return queue2.dequeue();       // Nie musimy sprawdzać warunku bo wiemy że zawsze musimy móc albo dodać albo
                                        // zabrać z bufora.
        }
    }

}