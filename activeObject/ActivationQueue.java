package activeObject;

public class ActivationQueue {
    void enqueue( MethodRequest methodRequest){}
    MethodRequest dequeue() throws  InterruptedException{
        wait(10);
        return new MethodRequest();}

}
