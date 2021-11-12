package activeObject;

public class RequestGet implements IMethodRequest{

    private final Integer parameter;
    private final Future<Integer> future;
    private final Servant servant;

    RequestGet(Future<Integer> future, Integer parameter, Servant servant){
        this.future = future;
        this.parameter = parameter;
        this.servant = servant;
    }

    @Override
    public void call() {
        Integer result = this.servant.get(parameter);
        this.future.value = result;
        this.future.isAvailable = true;
    }

    @Override
    public boolean guard() {
        if(this.servant.getNumberOfFreeFields() >= this.parameter)
            return true;
        return false;
    }
}
