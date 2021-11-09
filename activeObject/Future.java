package activeObject;

public class Future<T> {

    protected T value;
    protected boolean isAvailable;

    public boolean isAvailable(){return  this.isAvailable;}
    public T get(){return this.value;}

}
