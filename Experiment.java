import basic.CSVGenerator;
import basic.ThreadStatisticCollector;

import java.util.Arrays;

public class Experiment {
    public static void main(String[] args) {
        CSVGenerator csv = new CSVGenerator();
        csv.setHeader(Arrays.asList("Buffer Type", "Consumers Number" , "Producers Number","Total threads number",
                "offside operations number","Total Operation Number","Total waits number"));
        for(int k = 25 ; k <= 40 ; k+=5){
            for(int c = 4000000 ; c <= 20000000 ; c+= 200000000) {
                for (int i = 0; i < 10; i += 1) {
                    System.out.println("3L"+ k + "|" + k + "|" + c + "|" + i);
                    ExperimentStep step = new ExperimentStep(k, k, 200, c, true);
                    step.start();
                    int totalOperationNumber = ThreadStatisticCollector.instance.getTotalActions();
                    int totalWaitsNumber = ThreadStatisticCollector.instance.getTotalWaits();
                    csv.addRow(Arrays.asList("3 Lock Buffer", String.valueOf(k), String.valueOf(k),
                            String.valueOf(k + k), String.valueOf(c), String.valueOf(totalOperationNumber),
                            String.valueOf(totalWaitsNumber)));
                    ThreadStatisticCollector.instance.clear();

                }
            }
        }

        for(int k = 5 ; k <= 40 ; k+=5){
            for(int c = 4000000 ; c <= 10000000 ; c+= 25000000){
                for(int i = 0 ; i < 10 ; i+=1){
                    System.out.println("A" +k+"|" +k+"|" +c+"|" +i);
                    ExperimentStep step = new ExperimentStep(k,k,200,c, false);
                    step.start();
                    int totalOperationNumber = ThreadStatisticCollector.instance.getTotalActions();
                    int totalWaitsNumber = ThreadStatisticCollector.instance.getTotalWaits();
                    csv.addRow(Arrays.asList("Active Object",  String.valueOf(k),  String.valueOf(k),
                            String.valueOf( k + k),String.valueOf( c),  String.valueOf( totalOperationNumber),
                            String.valueOf( totalWaitsNumber)));
                    ThreadStatisticCollector.instance.clear();

                }
            }
        }

        try{
            csv.saveToFile("testnumbers2.csv");

        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("Ende");
        return;
    }

}
