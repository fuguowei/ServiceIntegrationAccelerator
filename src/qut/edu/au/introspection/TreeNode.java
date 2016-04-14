/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.introspection;

import java.util.Random;

/**
 *
 * @author fuguo
 */
public class TreeNode {
    private Distribution distribution;

    private Distribution pastDistribution;
    private int referenceNumber;
    private int maxNumber;
    private Random randomGenerator;

    public int getMaxNumber() {
        return maxNumber;
    }
    protected TreeNode[] children;
    private TreeNode latestNode;


    /*
    public TreeNode(TreeNode another) {
        this.referenceNumber = another.getReferenceNumber();
        this.maxNumber = another.getMaxNumber();
        this.children = another.getChildren();
        this.distribution = another.getDistribution();
        this.pastDistribution = another.getPastDistribution();
    }    
    */
    public void setDistribution(Distribution distribution) {
        this.distribution = distribution;
    }

    public void setPastDistribution(Distribution pastDistribution) {
        this.pastDistribution = pastDistribution;
    }

    public void setReferenceNumber(int referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public void setMaxNumber(int maxNumber) {
        this.maxNumber = maxNumber;
    }
    
    public TreeNode getLatestNode() {
        return latestNode;
    }

    public int getReferenceNumber() {
        return referenceNumber;
    }
    
    public void setLatestNode(TreeNode latestNode) {
        this.latestNode = latestNode;
    }
    
    public void updatePastDistribution() {
        if (pastDistribution == null) 
            pastDistribution = new Distribution(this.maxNumber-this.referenceNumber, this.randomGenerator);
        pastDistribution.copy(distribution);
    }
    
    public Distribution getPastDistribution() {
        return this.pastDistribution;
    }

    public TreeNode(int reference, int maxNumber, Random rg) {
        this.randomGenerator = rg;
        this.distribution = new Distribution(maxNumber-reference, this.randomGenerator);;
        this.pastDistribution = null;
        this.referenceNumber = reference;
        this.maxNumber = maxNumber;        
        children = new TreeNode[maxNumber-reference];
    }
    
    public Distribution getDistribution() {
        return distribution;
    }
    
    
    public TreeNode[] getChildren() {
        return children;
    }

    public void setChildren(TreeNode[] children) {
        this.children = children;
    }
    
    public String print() {
        String stringsToPrint = "------Parameter Number: "+ this.getReferenceNumber()+"\n";
        stringsToPrint = stringsToPrint + this.getDistribution()+"\n";
        for (TreeNode child : this.children) {
            if (child != null)
                stringsToPrint = stringsToPrint + child.print();
        }
        return stringsToPrint;
    }
    /*
    public String toString() {
        String result = null;
        result = "parameter reference: "+ referenceNumber + " max number of parameters: "+ maxNumber +" number of children: "+ children.length+'\n';
        result = result + " Children are: ";
        for (int i = 0; i < this.children.length; i++) {
            if (children[i] != null)
                result = result + children[i].toString();
        }
        result = "\n"+ result + " Distribution is "+ distribution;
        return result;
    }
    */
    
    
   public static void main(String arg[]) {
       /*
        TreeNode tempTree = new TreeNode(0, 3);
        for (int i = 0; i < 10; i++) {
            int sample = tempTree.getDistribution().sampleWithTransition(0.0);
            System.out.print(sample);           
       }
               */
    }
    
}
