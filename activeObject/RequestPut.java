package activeObject;

import basic.ThreadStatisticCollector;

public class RequestPut implements IMethodRequest{

    private final Integer parameter;
    private final Future<Void> future;
    private final Servant servant;
    private final int pId;

    RequestPut(Future<Void> future, Integer parameter, Servant servant, int pId){
        this.future = future;
        this.parameter = parameter;
        this.servant = servant;
        this.pId = pId;
    }

    @Override
    public void call() {
        ThreadStatisticCollector.instance.noteAction(this.pId);
        this.servant.put(parameter);
        this.future.setData(null);
    }

    @Override
    public boolean guard() {
        if(this.servant.getNumberOfFreeFields() >= this.parameter)
            return true;
        return false;
    }

}
