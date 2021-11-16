package activeObject;

import java.util.ArrayList;
import java.util.List;

// Buffer
public class Servant {

    private int size;
    private List<Integer> data = new ArrayList();

    Servant(int M){
        this.size = M;
    }

    protected int getNumberOfFreeFields(){
        return this.size - data.size();
    }
    protected int getNumberOfOccupiedFields(){
        return data.size();
    }

    public void put( int value ) {
        for(int i  = 0 ; i < value;i++){
            data.add(1);
        }
    }

    public int get( int value ) {
        for(int i  = 0 ; i < value;i++){
            data.remove(0);
        }
        return value;
    };
}
