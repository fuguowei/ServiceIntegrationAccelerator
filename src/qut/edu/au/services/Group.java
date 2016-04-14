/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.services;

import java.util.ArrayList;
import java.util.HashMap;
import qut.edu.au.introspection.TreeNode;

/**
 *
 * @author fuguo
 */
public class Group {
    private ArrayList<Parameter> parameters = null;
    private ArrayList<Parameter> parametersChosen = null;
    private TreeNode treeForParameters; // the tree for the parameters under one group
    private String name;
    private int groupNumber;
    private int counter =0; //how many times to excute this group

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    

    public TreeNode getTreeForParameters() {
        return treeForParameters;
    }

    public void setTreeForParameters(TreeNode treeForParameters) {
        this.treeForParameters = treeForParameters;
    }
    

    public void setParametersChosen(ArrayList<Parameter> parametersChosen) {
        this.parametersChosen = parametersChosen;
    }

    public ArrayList<Parameter> getParametersChosen() {
        return parametersChosen;
    }

    public ArrayList<Parameter> getParameters() {
        return parameters;
    }
    
    private HashMap<Integer, Integer> markovBlanket;

    public Group(int groupNumber) {
        this.groupNumber = groupNumber;
    }
    
    public void addParameter(Parameter param) {
        if (parameters== null)
            parameters = new ArrayList<Parameter>();
        parameters.add(param);
    }
    
    public int getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }
    
    public HashMap getMarkovBlanket() {
        if (markovBlanket == null)
            markovBlanket = new HashMap();
        return markovBlanket;
    }
    
    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;
        if (object != null && object instanceof Group)
            sameSame = this.groupNumber == ((Group) object).groupNumber;
        return sameSame;
    }        
    
    
}
