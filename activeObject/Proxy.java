package activeObject;

public class Proxy {



    private final Servant servant;
    private final ActivationQueue queue;
    private final Scheduler scheduler;

    public Proxy(){

        this.servant = new Servant();
        this.queue = new ActivationQueue();
        this.scheduler = new Scheduler(servant, queue);

    }


    Future put( int value ){

        Future response = new Future<Integer>();
        MethodRequest newRequest
                = new MethodRequest<Integer , Void >(
                        ( Integer v ) -> {  this.servant.put(v); return null;},
                        (Void) -> { return  this.servant.getNumberOfFreeFields() >= value; },
                    value, response);

        this.queue.enqueue(newRequest);
        return response;

    }

    Future get( int value ){

        Future response = new Future<Integer>();
        MethodRequest newRequest
                = new MethodRequest<Integer , Integer >(
                ( Integer v ) -> {  return this.servant.get(v);},
                (Void) -> { return  this.servant.getNumberOfOccupiedFields() >= value; },
                value, response);

        this.queue.enqueue(newRequest);
        return response;}
}
