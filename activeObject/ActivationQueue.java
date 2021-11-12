package activeObject;

public class ActivationQueue {
    void enqueue( IMethodRequest methodRequest){}
    IMethodRequest dequeue() throws  InterruptedException{
        wait(10);
        return null;
    }

}
