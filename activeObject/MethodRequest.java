package activeObject;

import java.util.function.Function;

public class MethodRequest<T,R> {

    private Function<T,R> function;
    private Function< Void ,Boolean> guardFunction;
    private T parameter;
    private Future future;

    MethodRequest( Function<T,R> function, Function<Void, Boolean> guardFunction,T parameter, Future future){
        this.function = function;
        this.guardFunction = guardFunction;
        this.parameter = parameter;
        this.future = future;
    }
    public void call(){
        future.value = this.function.apply(parameter);
        future.isAvailable = true;
    }
    public boolean guard(){return this.guardFunction.apply(null);}
}
