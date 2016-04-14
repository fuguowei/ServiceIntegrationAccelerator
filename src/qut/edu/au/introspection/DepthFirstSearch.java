/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.introspection;

import java.util.ArrayList;
import qut.edu.au.services.Parameter;

/**
 *
 * @author fuguo
 */
public class DepthFirstSearch {
    
    
    public ArrayList<String> dfs(ArrayList<Parameter> parameters, Parameter parameter, int depth) {
        ArrayList<String> results = new ArrayList<String>();
        System.out.print(parameter.getName());
        for (int i = parameter.getSimpleIndex()+1; i < parameters.size(); i++) {
            dfs(parameters,parameters.get(i),0);
        }
        return results;
    }
  
    /**
     * Unit tests the <tt>DepthFirstSearch</tt> data type.
     */
    public static void main(String[] args) {
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add("a");
        parameters.add("b");
        parameters.add("c");
        parameters.add("d");
        parameters.add("e");
        parameters.add("f");
        
        DepthFirstSearch search = new DepthFirstSearch();
        
    }

}
