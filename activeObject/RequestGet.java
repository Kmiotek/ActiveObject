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
        this.future.setData(result);
    }

    @Override
    public boolean guard() {
        if(this.servant.getNumberOfOccupiedFields() >= this.parameter)
            return true;
        return false;
    }

    @Override
    public boolean isConsumer() {
        return true;
    }
}
