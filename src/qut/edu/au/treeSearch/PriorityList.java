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
public class PriorityList {
    private ArrayList<Node> upper_confidence_tree_nodes; //= new ArrayList<Node>();
    private Node initialNode;
    
    public PriorityList(int fromNumber, int toNumber) {
        //this.upper_confidence_tree_nodes = new ArrayList<Node>();        
        initialNode = new Node();
        //when we initialse the list, it has only initialNode, which is a special node with (toNumber-fromNumber) lists and (toNumber-fromNumber) children
        //the lists have an uniform distribution, all chidlren are nil        
        int totalNumber = toNumber - fromNumber;
        for (int i = fromNumber; i < toNumber; i++) {
            double uniformDis = 1/totalNumber;
            initialNode.getDistributions().add(uniformDis);
            initialNode.getChildren().add(null); //children are null for now
        }
    }
    public void add_Upper_confidence_tree_nodes(ArrayList<Node> upper_confidence_tree_nodes) {
        //for (Node node : upper_confidence_tree_nodes)
        //  this.upper_confidence_tree_nodes.add(node);
    }
}
