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
 * @author fuguo
 */
public class Combination {
    private ArrayList<Parameter> parameterSet;
    private String requestContent;
    private String response;

    public ArrayList<Parameter> getParameterSet() {
        return parameterSet;
    }

    public String getRequestContent() {
        return requestContent;
    }

    public String getResponse() {
        return response;
    }

    public Combination(ArrayList<Parameter> parameterSets, String requesContent, String response) {
        this.parameterSet = parameterSets;
        this.requestContent = requesContent;
        this.response = response;
    }

    public void setParameterSets(ArrayList<Parameter> parameterSets) {
        this.parameterSet = parameterSets;
    }

    public void setRequestContent(String requesContent) {
        this.requestContent = requesContent;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
