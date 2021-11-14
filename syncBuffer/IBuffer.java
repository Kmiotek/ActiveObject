package syncBuffer;

import java.util.List;

public interface IBuffer {
    public void put(int pID, int size) throws InterruptedException;
    public List<Integer> get(int pID, int size) throws InterruptedException;
}
