package activeObject;

public interface IAsyncBuffer {
    Future<Void> put(int pId,int value);
    Future<Integer> get(int pId,int value);
}
