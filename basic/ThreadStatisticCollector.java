package basic;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ThreadStatisticCollector {

    Map<Integer,ThreadStat> threads = new HashMap<>();
    private Integer totalWaits = 0;
    private Integer totalActions = 0;

    public static  final ThreadStatisticCollector instance = new ThreadStatisticCollector();

    public void noteWait(int id){
        this.totalWaits++;
        if(!threads.containsKey(id)){
            threads.put(id,new ThreadStat());
        }
        threads.get(id).noteWait();
    }

    public Integer getTotalActions() {
        return totalActions;
    }
    public void clear(){
        totalActions =0;
        totalWaits = 0;
        this.threads = new HashMap<>();
    }

    public void noteAction(int id){

        this.totalActions++;

        if(!threads.containsKey(id) || threads.get(id) == null){
            threads.put(id,new ThreadStat());
        }
       // System.out.println( threads.get(id));
        threads.get(id).noteAction();
    }

    @Override
    public String toString() {
        final AtomicReference<String> output = new AtomicReference<>();
        output.set(" " + this.totalActions + "\t| " + this.totalWaits + "\t| "+ ((double)this.totalActions/(double)this.totalWaits) + "\tWaits per action \n");
        output.set( output.get() + "-----------------------------------------------------------------------------------------\n");
        this.threads.forEach( (key,value) -> {
            output.set( output.get() + key + "\t:" + value + "\n");
        });

        return output.get();
    }

    class ThreadStat{

        private int waitsCounter;
        private int actionCounter;

        @Override
        public String toString() {
            return " " + this.actionCounter + "\t| " + this.waitsCounter + "\t| "+ ((double)this.waitsCounter/(double)this.actionCounter) + "\tWaits per action";
        }

        public void noteWait(){
            this.waitsCounter++;
        }

        public void noteAction(){
            this.actionCounter++;
        }


    }


}
