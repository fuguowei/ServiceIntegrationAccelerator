/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.introspection;

/**
 *
 * @author fuguo
 */
public class MarkovBlanket {
    private int parameterReference;
    private double probability;
    
    public MarkovBlanket(int parameterReference, double probability) {
        this.parameterReference = parameterReference;
        this.probability = probability;
    }
}