/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package qut.edu.au.treeSearch;

import java.util.ArrayList;

/**
 *
 * @author sih
 */
public class Node {
    private ArrayList<Double> distributions; //= new ArrayList<Double>();

    public ArrayList<Double> getDistributions() {
        return distributions;
    }

    public ArrayList<Node> getChildren() {
        return children;
    }
    private ArrayList<Node> children; //= new ArrayList<Node>();

    public void setDistributions(ArrayList<Double> distributions) {
        this.distributions = distributions;
    }

    public void setChildren(ArrayList<Node> children) {
        this.children = children;
    }
    public Node() {
        distributions = new ArrayList<Double>();
        children = new ArrayList<Node>();
    }
}
