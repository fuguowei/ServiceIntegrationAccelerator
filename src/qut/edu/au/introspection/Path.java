/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.introspection;

import java.util.ArrayList;

/**
 *
 * @author fuguo
 */
class Path {
    private ArrayList<Node> nodes;

    public ArrayList<Node> getNodes() {
        return nodes;
    }
    public Path() {
        nodes = new ArrayList<Node>();
    }
    public void addNodes(Node node) {
        nodes.add(node);
    }
}
