/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.introspection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import qut.edu.au.services.Parameter;
import java.lang.Math;

/**
 *
 * @author fuguo
 */
class Distribution {
    private double sample_size;
    private double Effective_sample_size;
    private double [] pdf;
    private double [] cdf;
    private int arr_size;
    private Random randomGenerator;
    
    
    public void copy(Distribution distribution) {
        this.sample_size  = distribution.sample_size;        
        for (int i = 0; i < pdf.length; i++) {
            this.pdf[i] = distribution.pdf[i];
            this.cdf[i] = distribution.cdf[i];
        }
    }
    /**
     * Constructs an initial uniform distribution
     * @param size 
     */
    public Distribution(int size, Random rg) {
        // create a random number generator
        //randomGenerator.setSeed(System.currentTimeMillis());
        // set the size
        this.randomGenerator = rg;
        arr_size = size;
        // create the probability distribution
        pdf = new double[arr_size];
        // create the cumulative distribution
        cdf = new double[arr_size];
        // for every element
        for(int i = 0; i < arr_size; i++) {
            pdf[i] = 1.0/((double)arr_size);
            // if not zero
            if (i != 0) {
                cdf[i] = cdf[i-1] + pdf[i];
            } else {
                cdf[i] = pdf[i];
            }
        }
        // set the initial sample size
        sample_size = (double)size;
        Effective_sample_size = 1.0/sample_size;
    }
    
    public int sample() { // TODO test this
        // get a value between 0 and arr-size
        double z = randomGenerator.nextDouble();
        // upper and lower indices
        int lower = 0; int upper = arr_size-1;
        // the midpoint
        int mid = (lower+upper) >> 1;
        // while still searching
        while (lower <= upper) {
            // if less than mid
            if (z < cdf[mid]) {
                upper = mid-1;
            } else {
                lower = mid+1;
            }
            // set new mid
            mid = (lower+upper) >> 1;
        }
        
        return mid+1;
    }
    
    
    public int sampleWithTransition(double variance) { // TODO test this
        // sample from the proposal distribution
        int s = sample();
        // perturb the sample with the transition kernel
        int val = (int)Math.round((double)s+randomGenerator.nextGaussian()*variance);
        while(val < 0 || val >= arr_size || pdf[val] == 0.0) {
            val = (int)Math.round((double)s+randomGenerator.nextGaussian()*variance);        
        }
        return val;
    }
    
    /**
     * This method update the pdf with a weight at index
     * @param index
     * @param weight 
     */
    public double updatePDF(int index, double weight) {
        
        // multiply out the previous weight scaling
        for(int i = 0; i < arr_size; i++)
            pdf[i] = sample_size * pdf[i];  //don't know why we are doing this
        // add the weight
        pdf[index] += weight;
        //
        //Effective_sample_size *= sample_size;
        //Effective_sample_size += weight*weight;
        //zer sample size
        sample_size = 0.0;
        // get sampel size
        for(int i = 0; i < arr_size; i++) {
            sample_size +=  pdf[i];
        }
        //Effective_sample_size /= sample_size;
        // normalise
        for(int i = 0; i < arr_size; i++) {
            pdf[i] = pdf[i]/sample_size;
            // update cdf
            if (i != 0) {
                cdf[i] = cdf[i-1] + pdf[i];
            } else {
                cdf[i] = pdf[i];
            }
        }
        // resample
        if (cdf[arr_size-1] != 1.0) {
             // set the initial sample size
            sample_size = (double)arr_size;
            double updt = 1.0/sample_size;
            // make sure pdf gets the update
             pdf[index] += updt;
            //Effective_sample_size = 1.0/sample_size;
            // normalise
            for(int i = 0; i < arr_size; i++) {
                pdf[i] = pdf[i]/(cdf[arr_size-1]+updt);
                // update cdf
                if (i != 0) {
                    cdf[i] = cdf[i-1] + pdf[i];
                } else {
                    cdf[i] = pdf[i];
                }
            }
        }
        return pdf[index];
    }
    
    /**
     * Sets a probability value at index
     * Post: must call normalise
     * @param index
     * @param prob 
     */
    public void setProb(int index, double prob) {
        pdf[index] = prob;
    }
    
    public void normalise(){
        // sum variable
        // set the initial sample size
        sample_size = 0.0;
        // for every element
        for(int i = 0; i < arr_size; i++) {
            sample_size += pdf[i];
        }
        
        // for every element
        for(int i = 0; i < arr_size; i++) {
            pdf[i] = pdf[i]/sample_size;
            // if not zero
            if (i != 0) {
                cdf[i] = cdf[i-1] + pdf[i];
            } else {
                cdf[i] = pdf[i];
            }
        }
    }
    
    public void zero(){
       // for every element
        for(int i = 0; i < arr_size; i++) {
            pdf[i] = 0.0;
        } 
    }
    
    
    /**
     * This method update the pdf with a weight at index
     * @param index
     * @param weight 
     * @param deviation index-deviation -> index -> index+deviation
     */
    public void updatePDFwithNeighbours(int index, double weight, int deviation) {
        // multiply out the previous weight scaling
        for(int i = 0; i < arr_size; i++) {
            pdf[i] = sample_size * pdf[i];  //don't know why we are doing this
        }
        // add the weight
        pdf[index] += weight;
        
        for (int i = 0; i < deviation; i++) {
            int leftSide = index-i;
            int rightSide = index+i;            
            if (leftSide>=0)                
                pdf[leftSide] += weight/2;
            if (rightSide<arr_size)
                pdf[rightSide] += weight/2;
        }
        //zer sample size
        sample_size = 0.0;
        // get sampel size
        for(int i = 0; i < arr_size; i++) {
            sample_size +=  pdf[i];
        }
        // normalise
        for(int i = 0; i < arr_size; i++) {
            pdf[i] = pdf[i]/sample_size;
            // update cdf
            if (i != 0) {
                cdf[i] = cdf[i-1] + pdf[i];
            } else {
                cdf[i] = pdf[i];
            }
        }
    }
    
    
    /**
     * This methods sets the probability distribution
     * @param p
     * @pre p.length == getSize()
     */
    public void setPDF(double [] p, int lower, int upper) {
        
        for(int i = lower; i < upper; i++) {
            pdf[i-lower] = p[i];
        }
        // for every element
        for(int i = lower; i < upper; i++) {
            // if not zero
            if (i != lower) {
                cdf[i-lower] = cdf[i-1-lower] + pdf[i-lower];
            } else {
                cdf[i-lower] = pdf[i-lower];
            }
        }
        // set the initial sample size
        sample_size = (double)pdf.length;
    }
    
    /**
     * This method get an importance sampling weight for index and prior
     * distribution
     * @param index
     * @param prior - The prior distribution or null if uniform
     * @param min_tol - the minimum tolerance degeneracy safeguard
     * @return importance sampling weight
     */
    public double getImportanceWeight(int index, Distribution prior, double min_tol) {
        // if the prior is null assume uniform
        if (prior == null) {
            // if within tolerance
            if (pdf[index] >= min_tol) {
                return 1.0/pdf[index];
            }
            return 1.0/min_tol;
        } else {
            // if within tolerance
            if (pdf[index] >= min_tol) {
                return prior.probability(index)/pdf[index];
            }
            return prior.probability(index)/min_tol;
        }
    }
    
    /**
     * This method gets a probability value
     * @param index
     * @return probability value
     * @pre index < getSize()
     */ 
    public double probability(int index) {
        return pdf[index];
    }
    
    /**
     * 
     * @param arg 
     */
    public int getSize() {
        return arr_size;
    }
    
    public String toString() {
        String result= "pdf: ";
        for (int i = 0; i < pdf.length; i++) {
            result= result + i+ ":"+ pdf[i]+",";
        }
        result = result + "\ncdf: ";
        for (int i = 0; i < cdf.length; i++) {
            result= result + i + ":"+ cdf[i]+",";
        }
        result = result + "\n";
        return result;
    }    
    
    public static void main(String arg[]) {
        //Distribution distribution = new Distribution(10);
        //for (int i = 0; i<1000; i++) {
        //    System.out.println(distribution.sample());
        //}
        
        int arr1[] = {1, 2, 3};
        int arr2[] = {1, 2, 3};
        if (Arrays.equals(arr1, arr2)) // Same as arr1.equals(arr2)
            System.out.println("Same");
        else
            System.out.println("Not same");
        
    }

}
