/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.introspection;

import java.util.ArrayList;
import java.util.Random;
import qut.edu.au.services.Group;
import qut.edu.au.services.Parameter;

/**
 *
 * @author fuguo
 */
public class GroupTreeNode extends TreeNode{
    
    private TreeNode treeForParameters; // the tree for the parameters under one group
    //private GroupTreeNode[] children;
    private int counter =0; // this counter presents the number of times that the search method will stick to the current node

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }
            

    public TreeNode getTreeForParameters() {
        return treeForParameters;
    }

    public void setTreeForParameters(TreeNode treeForParameters) {
        this.treeForParameters = treeForParameters;
    }
    
    public GroupTreeNode(int reference, int maxNumber, Random rg) {
        super(reference, maxNumber, rg);
    }
    
    /*
    @Override
    public GroupTreeNode[] getChildren() {
        return children;
    }

    public void setChildren(GroupTreeNode[] children) {
        this.children = children;
    }
    */
    
    @Override
    public String print() {
        String thingsToPrint = null;
        thingsToPrint = "Group Number: "+ this.getReferenceNumber()+"\n";
        thingsToPrint = thingsToPrint + "Counter: "+ this.getCounter()+"\n";        
        thingsToPrint = thingsToPrint + this.getDistribution()+"\n";
        if (this.getTreeForParameters() != null) {
            thingsToPrint = thingsToPrint + "----Parameter Tree under the group: \n";
            thingsToPrint = thingsToPrint + this.getTreeForParameters().print();
            
        }
        for (int i = 0; i < this.children.length; i++) {
           GroupTreeNode child = (GroupTreeNode)this.children[i];
           if (child!=null)
               thingsToPrint = thingsToPrint + child.print();
        }
        return thingsToPrint;
    }
    
    private TreeNode initialiseParamPathUnderGroup(ArrayList<Parameter> allParams, ArrayList<Parameter> localPath, int totalSize, TreeNode tree, int i, Random rg) {
        TreeNode tempTree = tree;
        if (i < localPath.size()) {
            int index = localPath.get(i).getSimpleIndex();
            int indexForPrevious = 0;
            //if (i > 0) {// exclude the first element, which is null
            //indexForPrevious = index - knownSuccessPath.get(i - 1).getSimpleIndex();
            if (i > 0) //indexForPrevious = knownSuccessPath.get(i - 1).getSimpleIndex();
            {
                indexForPrevious = index - localPath.get(i - 1).getSimpleIndex() - 1;
            } else {
                indexForPrevious = index - allParams.get(0).getSimpleIndex();
            }
            
            tempTree = new TreeNode(index, totalSize, rg);
            tree.getChildren()[indexForPrevious] = tempTree;
            tree.getDistribution().updatePDF(indexForPrevious, MonteCarloIntrospection.initialParameterProbabilityFactor * tree.getDistribution().getSize()); //weight 1.5 will make the indexForPrevious 3 times of others
            //tree.getDistribution().updatePDFwithNeighbours(indexForPrevious, 1.5, 5); //weight 1.5 will make the indexForPrevious 3 times of others
            //maybe we should make the neighbours highly likely by updating the distribution?
            // }            
            i = i + 1;
            tempTree = initialiseParamPathUnderGroup(allParams, localPath, totalSize, tempTree, i, rg);
        }
        return tempTree;
    }
    
    public void initialise(ArrayList<Group> knownGroups, ArrayList<Parameter> knownSuccessPath, int groupSize, int i, Random rg) {
        if (i < knownGroups.size()) {
            Group currentGroup = knownGroups.get(i);
            int index = currentGroup.getGroupNumber();
            int indexForPrevious = 0;
            //if (i > 0) {// exclude the first element, which is null
            //indexForPrevious = index - knownSuccessPath.get(i - 1).getSimpleIndex();
            if (i > 0) //indexForPrevious = knownSuccessPath.get(i - 1).getSimpleIndex();
            {
                indexForPrevious = index - knownGroups.get(i - 1).getGroupNumber() - 1;
                if (indexForPrevious<0)
                    System.out.print("WRONG.............................impossible to get this");
            }
            GroupTreeNode tempTree = new GroupTreeNode(index, groupSize, rg);
            //set the parameter tree
            ArrayList<Parameter> localPath = new ArrayList<Parameter>();
            //ArrayList<Parameter> allparametersUnderGroup = new ArrayList<Parameter>();
            ArrayList<Parameter> parametersUnderGroup = currentGroup.getParameters();
            /*
            System.out.println("\n parameters under group: "+currentGroup.getGroupNumber()+"\n");
            for (Parameter parametersUnderGroup1 : parametersUnderGroup) {
                System.out.print(parametersUnderGroup1.getSimpleIndex()+",");
            }
            System.out.println("\n chosen ones: \n");
            */
            
            for (Parameter parameter : knownSuccessPath) {
                if (parametersUnderGroup.contains(parameter)) {
                    localPath.add(parameter);
                    //System.out.print(parameter.getSimpleIndex()+",");
                }
            }

            //int size = parametersUnderGroup.get(parametersUnderGroup.size()-1).getSimpleIndex()+1;
            int size = parametersUnderGroup.size()+1;            
            TreeNode localTreeUnderGroup = new TreeNode(0, size,rg);
            size = parametersUnderGroup.get(0).getSimpleIndex()+size-1;            
            initialiseParamPathUnderGroup(parametersUnderGroup, localPath, size, localTreeUnderGroup, 0, rg);
            //1.125  -- three found--- quite good rusults
            // 1.0625 -- two found
            //1.09375  -- only one?
            double probobility = this.getDistribution().updatePDF(indexForPrevious, MonteCarloIntrospection.initialGroupProbabilityFactor*this.getDistribution().getSize()); //weight 1.5 will make the indexForPrevious 3 times of others
            tempTree.setTreeForParameters(localTreeUnderGroup);
            tempTree.setCounter((int)(probobility*MonteCarloIntrospection.countingFactor));
            this.getChildren()[indexForPrevious] = tempTree;
            i = i + 1;
            GroupTreeNode node = (GroupTreeNode)this.getChildren()[indexForPrevious];            
            node.initialise(knownGroups, knownSuccessPath, groupSize, i, rg);
        }
    }
       
}
