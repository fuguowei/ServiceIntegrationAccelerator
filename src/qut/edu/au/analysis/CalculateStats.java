/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.analysis;

import java.io.IOException;
import static java.lang.Math.abs;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import static qut.edu.au.Utility.readFile;

/**
 *
 * @author fuguo
 */
public class CalculateStats {

    private static ArrayList<String> allindicies = new ArrayList<String>();
    
    private static double calculateMean(ArrayList<Double> allInstances) {
        if (!allInstances.isEmpty()) {
            DescriptiveStatistics ds = new DescriptiveStatistics();        
            for (Double difference : allInstances) {
                ds.addValue(difference);
            }
            return ds.getMean();
        } else
            return 0;
    }
    
    private static double calculateStandardDev(ArrayList<Double> allInstances) {
        if (!allInstances.isEmpty()) {
            DescriptiveStatistics ds = new DescriptiveStatistics();        
            for (Double difference : allInstances) {
                ds.addValue(difference);
            }
            return ds.getStandardDeviation();
        } else
            return 0;
    }
    
    
    public static void calcuateLFinalResult(int lenth, int maxLenth, String flag){
        try {
            ArrayList<Double> allLastSuccessRates = new ArrayList<Double>();
            ArrayList<Double> allLastTime = new ArrayList<Double>();
            ArrayList<Double> allLastHitRate = new ArrayList<Double>();
            
            double minSuccessRate = 0;
            double maxSuccessRate = 0;
            int whichMax = 0;
            int whichMin =0;

            for (int i = 1; i <= 40; i++) {
                //String fileName = "THIRDRUN"+lenth+"_"+maxLenth+"LOG_" + i + ".stat";
                
                //String fileName = "SECONDSEARCHGENERATED_LOG_" + i + ".stat";
                //String fileName = "REALSERVICE_LOG_" + i + ".stat";
                //String fileName = "SEARCHGENERATED_10_50LOG_" + i + ".stat";
                String fileName = "SEARCHGENERATED_"+lenth+"_"+maxLenth+"LOG_" + i + ".stat";
                
                if (flag!=null && flag.equals("BRUTEFORCE"))
                    fileName = "BRUTEFORCE_"+lenth+"_"+maxLenth+"LOG_" + i + ".stat";

                
                if (flag!=null && flag.equals("REALSERVICE"))
                    fileName = "REALSERVICE_LOG_" + i + ".stat";

                //String statContent = readFile("logsFromServer3/"+fileName);
                String statContent;
                if (lenth==8 && maxLenth==20 && flag== null) {
                    fileName = "BRUTEFORCE_"+lenth+"_"+maxLenth+"LOG_" + i + ".stat";
                    statContent = readFile("logsFromServerN8485267/"+fileName, null);
                } else {
                    statContent = readFile("ValubleLogsFromServer/"+fileName, null);
                }
                
                
                String patternSuccessRate = "the succeeful paths is: \\d+/\\d+=(\\d+\\.\\d+)%";
                String patternTimie = "the total time taken: (\\d+\\.\\d+)";
                String patternHitRate = "the percentage of the hits is: (-?\\d+)/(\\d+)=-?\\d*\\.\\d+%";                
                //String identifiedIndicies = "the identified indicies are: [(.*)]";
                String identifiedIndicies = "the identified indicies are: \\[(.*)\\]";
                // the identified indicies are: [1,2,5,12,]
                
                //the percentage of the hits is: 28799107/4600000000=.63%

                //the total time taken: 10424.0 seconds.
                //the succeeful paths is: \\d+/\d+=(\d+\.\d+)%
                //the succeeful paths is: 13/20=65.00%
                // Create a Pattern object
                Pattern r = Pattern.compile(patternSuccessRate);

                // Now create matcher object.
                Matcher m = r.matcher(statContent);
                double lastSuccessRate = 0;
                while (m.find()) {
                    lastSuccessRate = Double.parseDouble(m.group(1));
                }
                
                //System.out.println("Number: "+i+ "    The max success rate is: " + lastSuccessRate);
                if (i==1) {
                    minSuccessRate = lastSuccessRate;
                    maxSuccessRate = lastSuccessRate;
                    whichMin = i;
                    whichMax = i;
                }
                else {                   
                    if (minSuccessRate>lastSuccessRate) {
                        minSuccessRate = lastSuccessRate;
                        whichMin = i;                        
                    }
                    if (maxSuccessRate<lastSuccessRate) {
                        maxSuccessRate = lastSuccessRate;
                        whichMax = i;
                    }
                }
                allLastSuccessRates.add(lastSuccessRate);

                r = Pattern.compile(patternTimie);
                // Now create matcher object.
                m = r.matcher(statContent);

                double lastTime = 0;
                while (m.find()) {
                    lastTime = Double.parseDouble(m.group(1));
                }
                //System.out.println("The time for the maxt success rate is: " + lastTime);
                allLastTime.add(lastTime/60);

                r = Pattern.compile(patternHitRate);
                // Now create matcher object.
                m = r.matcher(statContent);

                double lastHitRate1 = 0;
                double lastHitRate2 = 0;
                double lastHitRate = 0;
                
                        
                while (m.find()) {                    
                    lastHitRate1 = abs(Double.parseDouble(m.group(1)));
                    lastHitRate2 = abs(Double.parseDouble(m.group(2)));
                }
                //System.out.println("The max hit rate is: " + lastHitRate);
                lastHitRate = lastHitRate1/lastHitRate2;
                allLastHitRate.add(lastHitRate);
                
                if (flag !=null && flag.equals("REALSERVICE")) {
                    r = Pattern.compile(identifiedIndicies);
                    // Now create matcher object.
                    m = r.matcher(statContent);

                    String Indicies = "";
                    while (m.find()) {
                        Indicies = m.group(1);
                    }
                    //System.out.println("The time for the maxt success rate is: " + lastTime);
                    allindicies.add(Indicies);
                }
            }
            System.out.println("the min is " + minSuccessRate + " i is　("+ whichMin + ") the max is : "+ maxSuccessRate + " i is ("+ whichMax+ ")");
            minSuccessRate = 0;
            maxSuccessRate = 0;            
            whichMax = 0;
            whichMin =0;
            
            DecimalFormat df = new DecimalFormat("####0.00");        
            
            //System.out.println("The mean of the successful rate for "+lenth+" out of "+ maxLenth+" is: "+ df.format(calculateMean(allLastSuccessRates)));
            System.out.println("The standard deviation of the successful rate for "+lenth+" out of "+ maxLenth+" is: "+ df.format(calculateStandardDev(allLastSuccessRates)));
            //System.out.println("The mean of the time for "+lenth+" out of "+ maxLenth+" is: "+ df.format(calculateMean(allLastTime)));
            //System.out.println("The standard deviation of the time for the successful rate for "+lenth+" out of "+ maxLenth+" is: "+ df.format(calculateStandardDev(allLastTime)));
            //System.out.println("The mean of the hit rate for "+lenth+" out of "+ maxLenth+" is: "+ calculateMean(allLastHitRate));
            //System.out.println("The standard deviation of the hit rate for the successful rate for "+lenth+" out of "+ maxLenth+" is: "+ calculateStandardDev(allLastHitRate));
            
            // --- to generate spread sheet
            
            //System.out.println(df.format(calculateMean(allLastSuccessRates))+","+df.format(calculateStandardDev(allLastSuccessRates))+","+calculateMean(allLastHitRate) + ","+
            //        calculateStandardDev(allLastHitRate) + ","+ df.format(calculateMean(allLastTime))+ ","+df.format(calculateStandardDev(allLastTime))
            //        );
            System.out.println(df.format(calculateMean(allLastSuccessRates)));

            // --- to generate latex table            
            //System.out.println(df.format(calculateMean(allLastSuccessRates))+" & "+df.format(calculateStandardDev(allLastSuccessRates))+" & "+calculateMean(allLastHitRate) + " & "+
            //        calculateStandardDev(allLastHitRate) + " & "+ df.format(calculateMean(allLastTime))+ " & "+df.format(calculateStandardDev(allLastTime))
            //        );
            
        } catch (IOException ex) {
            Logger.getLogger(CalculateStats.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void main(String arg[]) {
        
        
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~20~~~~Monte Carlo~~~~~~~~~~~~~~~~~~");
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~5 OUT OF 20~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(5,20, null);
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~5 OUT OF 20~~~~~~~~~~~~~~~~~~~~~~");
        
        calcuateLFinalResult(8,20, null);
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~11 OUT OF 20~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(11,20, null);
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~11 OUT OF 20~~~~~~~~~~~~~~~~~~~~~~");
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~14 OUT OF 20~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(14,20, null);
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~14 OUT OF 20~~~~~~~~~~~~~~~~~~~~~~");

        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~17 OUT OF 20~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(17,20, null);
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~17 OUT OF 20~~~~~~~~~~~~~~~~~~~~~~");
        

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~20~~~~BRUTE FORCE METHOD~~~~~~~~~~~~~~~~~~");
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~5 OUT OF 20~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(5,20, "BRUTEFORCE");  
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~5 OUT OF 20~~~~~~~~~~~~~~~~~~~~~~");
        

        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~8 OUT OF 20~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(8,20, "BRUTEFORCE");
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~8 OUT OF 20~~~~~~~~~~~~~~~~~~~~~~");

        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~11 OUT OF 20~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(11,20, "BRUTEFORCE");
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~11 OUT OF 20~~~~~~~~~~~~~~~~~~~~~~");

        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~14 OUT OF 20~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(14,20, "BRUTEFORCE");
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~14 OUT OF 20~~~~~~~~~~~~~~~~~~~~~~");

        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~17 OUT OF 20~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(17,20, "BRUTEFORCE");
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~17 OUT OF 20~~~~~~~~~~~~~~~~~~~~~~");

        
        
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~Monte Carlo 50~~~~~~~~~~~~~~~~~~~~~~");
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~10 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(10,50, null);
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~10 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~15 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(15,50, null);
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~15 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~20 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(20,50, null);
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~20 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~25 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(25,50, null);
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~25 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~30 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(30,50, null);
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~30 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~35 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(35,50, null);
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~35 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~40 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(40,50, null);
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~40 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        
        
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~Brute Force 50~~~~~~~~~~~~~~~~~~~~~~");
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~10 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(10,50, "BRUTEFORCE");
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~10 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~15 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(15,50, "BRUTEFORCE");
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~15 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~20 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(20,50, "BRUTEFORCE");
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~20 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~25 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(25,50, "BRUTEFORCE");
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~25 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~30 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(30,50, "BRUTEFORCE");
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~30 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~35 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(35,50, "BRUTEFORCE");
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~35 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~40 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(40,50, "BRUTEFORCE");
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~40 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~Monte Carlo 100~~~~~~~~~~~~~~~~~~~~~~");
        
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~10 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(10,100, null);
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~10 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        
        
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~20 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(20,100, null);
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~20 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~30 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(30,100, null);
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~30 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
                
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~40 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(40,100, null);
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~40 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~50 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(50,100, null);
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~50 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~60 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(60,100, null);
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~60 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~70 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(70,100, null);
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~70 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~80 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(80,100, null);
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~80 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        
        
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~BRUTE FORCE 100~~~~~~~~~~~~~~~~~~~~~~");
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~10 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(10,100, "BRUTEFORCE");
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~10 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        
        
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~20 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(20,100, "BRUTEFORCE");
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~20 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~30 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(30,100, "BRUTEFORCE");
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~30 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
                
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~40 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(40,100, "BRUTEFORCE");
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~40 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~50 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(50,100, "BRUTEFORCE");
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~50 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~60 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(60,100, "BRUTEFORCE");
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~60 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~70 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(70,100, "BRUTEFORCE");
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~70 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~80 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(80,100, "BRUTEFORCE");
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~80 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        
        /*
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~10 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(10,50, "BRUTEFORCE");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~10 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~15 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(15,50, "BRUTEFORCE");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~15 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~20 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(20,50, "BRUTEFORCE");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~20 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~25 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(25,50, "BRUTEFORCE");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~25 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~30 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(30,50, "BRUTEFORCE");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~30 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~35 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(35,50, "BRUTEFORCE");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~35 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~40 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(40,50, "BRUTEFORCE");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~40 OUT OF 50~~~~~~~~~~~~~~~~~~~~~~");
        
        
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~10 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(10,100, "BRUTEFORCE");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~10 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        
        
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~20 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(20,100, "BRUTEFORCE");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~20 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~30 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(30,100, "BRUTEFORCE");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~30 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
                
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~40 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(40,100, "BRUTEFORCE");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~40 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~50 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(50,100, "BRUTEFORCE");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~50 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~60 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(60,100, "BRUTEFORCE");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~60 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~70 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(70,100, "BRUTEFORCE");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~70 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~80 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(80,100, "BRUTEFORCE");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~80 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        
        */
        
        /*
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~8 OUT OF 20~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(8,20, "BRUTEFORCE");  //to logsFromServerN8485267
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~8 OUT OF 8~~~~~~~~~~~~~~~~~~~~~~");
        */
        

        
        
        
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~80 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        calcuateLFinalResult(80,100, "REALSERVICE");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~80 OUT OF 100~~~~~~~~~~~~~~~~~~~~~~");
        
        //derived 1, 4, 5, 8, 10
        
        int[] counter = new int[20];
        
        for (String arg1 : allindicies) {
            for (int i = 1; i < 21; i++) {
                if (counter[i-1] == 0) {
                    if (arg1.contains(String.valueOf(i))) {
                        counter[i-1] = counter[i-1]+1;
                        break;
                    }
                }
            }
        }
        
        for (int i = 0; i < 20; i++) {
            System.out.println(counter[i]);
        }
        
    }
}
