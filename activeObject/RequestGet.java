package activeObject;

import basic.ThreadStatisticCollector;

public class RequestGet implements IMethodRequest{

    private final Integer parameter;
    private final Future<Integer> future;
    private final Servant servant;
    private final int pId;

    RequestGet(Future<Integer> future, Integer parameter, Servant servant, int pId){
        this.future = future;
        this.parameter = parameter;
        this.servant = servant;
        this.pId = pId;
    }

    @Override
    public void call() {
        ThreadStatisticCollector.instance.noteAction(this.pId);
        Integer result = this.servant.get(parameter);
        this.future.setData(result);
    }

    @Override
    public boolean guard() {
        //System.out.println(this.servant.getNumberOfOccupiedFields()  + ">=" +  this.parameter);
        if(this.servant.getNumberOfOccupiedFields() >= this.parameter)
            return true;
        return false;
    }

    @Override
    public boolean isConsumer() {
        return true;
    }

}
