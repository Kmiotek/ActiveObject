import basic.CSVGenerator;
import basic.ThreadStatisticCollector;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Experiment {
    public static void main(String[] args) {
        CSVGenerator csv = new CSVGenerator();
        csv.setHeader(Arrays.asList("Buffer Type", "Consumers Number" , "Producers Number","Total threads number","offside operations number","Total Operation Number"));
        for(int k = 50 ; k <= 50 ; k+=150){
            for(int p = 50 ; p <= 50 ; p+=150){
                for(int c = 50000 ; c <= 2000000 ; c+= 500000)
                for(int i = 0 ; i < 5 ; i+=1){
                    System.out.println(k+"|" +p+"|" +c+"|" +i);
                    ExperimentStep step = new ExperimentStep(k,p,200,c, true);
                    step.start();
                    int totalOperationNumber = ThreadStatisticCollector.instance.getTotalActions();
                    csv.addRow(Arrays.asList("3 Lock Buffer",  String.valueOf( k),  String.valueOf( p),String.valueOf( k + p),String.valueOf( c),  String.valueOf( totalOperationNumber)));
                    ThreadStatisticCollector.instance.clear();

                }
            }
        }

        for(int k = 50 ; k <= 50 ; k+=150){
            for(int p = 50 ; p <= 50 ; p+=150){
                for(int c = 0 ; c <= 2000000 ; c+= 500000)
                    for(int i = 0 ; i < 5 ; i+=1){
                    System.out.println(k+"|" +p+"|" +c+"|" +i);
                    ExperimentStep step = new ExperimentStep(k,p,200,c, false);
                    step.start();
                    int totalOperationNumber = ThreadStatisticCollector.instance.getTotalActions();
                    csv.addRow(Arrays.asList("Active Objectr",  String.valueOf( k),  String.valueOf( p),String.valueOf( k + p),String.valueOf( c),  String.valueOf( totalOperationNumber)));
                    ThreadStatisticCollector.instance.clear();

                }
            }
        }

        try{
            csv.saveToFile("test.csv");
        }catch(Exception e){

        }
        System.out.println("Ende");
        return;
    }

}
