/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.introspection;

import qut.edu.au.services.Parameter;

/**
 *
 * @author fuguo
 */
class Node {
    private Parameter parameter;

    public Parameter getParameter() {
        return parameter;
    }

    public Distribution getPosterior() {
        return posterior;
    }
    private Distribution posterior;
    
    public Node(Parameter parameter, Distribution posterior) {
        this.parameter = parameter;
        this.posterior = posterior;
    }    
}
