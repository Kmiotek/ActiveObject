package activeObject;

public class RequestPut implements IMethodRequest{

    private final Integer parameter;
    private final Future<Void> future;
    private final Servant servant;

    RequestPut(Future<Void> future, Integer parameter, Servant servant){
        this.future = future;
        this.parameter = parameter;
        this.servant = servant;
    }

    @Override
    public void call() {
        this.servant.put(parameter);
        this.future.isAvailable = true;
    }

    @Override
    public boolean guard() {
        if(this.servant.getNumberOfFreeFields() >= this.parameter)
            return true;
        return false;
    }
}
