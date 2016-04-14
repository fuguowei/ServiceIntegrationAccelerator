/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au;

import java.util.ArrayList;

 public class Combinations {
    private StringBuilder output = new StringBuilder();
    private final ArrayList<Integer> inputstring;
    private static int counter =0;
    public Combinations(ArrayList<Integer> str ){
        inputstring = str;
        System.out.println("The input string  is  : " + inputstring);
    }
    
    
    public static void main (String args[])
    {
        ArrayList<Integer> strings = new ArrayList<Integer>();
        for (int i = 0; i < 10; i++) {
            strings.add(i);
        }
        
        Combinations combobj= new Combinations(strings);
        System.out.println("");
        System.out.println("");
        System.out.println("All possible combinations are :  ");
        System.out.println("");
        System.out.println("");
        combobj.combine(0);
        System.out.println("TOTOAL NUBMER: "+ counter);
    }
    
    //public void combine() { combine( 0 ); }
    
    private void combine(int start){
        for( int i = start; i < inputstring.size(); ++i ){
            output.append( inputstring.get(i) );
            System.out.println();
            System.out.println(output);
            counter++;
            System.out.println();
            if ( i < inputstring.size() )
            combine( i + 1);
            output.setLength(output.length()-1);
        }
    }
} 