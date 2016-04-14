/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package qut.edu.au.analysis;

import java.util.ArrayList;
import qut.edu.au.services.Parameter;

/**
 *
 * @author sih
 */
public class AnalysisStats {
    public static int Global_total_number_of_parameters =0;
    public static int parameterUniqueIDinTree =0;
    public static int Global_total_number_of_complex_parameters =0;
    public static int Global_total_number_of_simple_parameters =0;
    public static int Global_total_number_of_levels =0;
    public static ArrayList<String> listofPreliminary = new ArrayList<String>();

    public static ArrayList<Parameter> simpleParameterList = new ArrayList<Parameter>();
    public static ArrayList<Parameter> complexParameterList = new ArrayList<Parameter>();    
    public static ArrayList<Parameter> compulsoryInputParameterList = new ArrayList<Parameter>();
    public static ArrayList<Parameter> optionalInputParameterList = new ArrayList<Parameter>();
    
    public static void resetStats() {
        AnalysisStats.simpleParameterList = new ArrayList<Parameter>();
        AnalysisStats.complexParameterList = new ArrayList<Parameter>();        
        AnalysisStats.compulsoryInputParameterList = new ArrayList<Parameter>();
        AnalysisStats.optionalInputParameterList = new ArrayList<Parameter>();
        AnalysisStats.listofPreliminary = new ArrayList<String>();
        AnalysisStats.parameterUniqueIDinTree =0;
        AnalysisStats.Global_total_number_of_complex_parameters =0;
        AnalysisStats.Global_total_number_of_parameters =0;
        AnalysisStats.Global_total_number_of_levels =0;
        AnalysisStats.Global_total_number_of_simple_parameters =0;
    }
            
}
