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
class Book {
    private ArrayList<Path> validPaths;

    public Book() {
        validPaths = new ArrayList<Path>();
    }
    
    public void addPath(Path path) {
        validPaths.add(path);
    }
    
}
