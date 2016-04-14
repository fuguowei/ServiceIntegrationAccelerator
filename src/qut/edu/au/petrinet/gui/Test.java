/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.petrinet.gui;

import qut.edu.au.petrinet.logic.Arc;
import qut.edu.au.petrinet.logic.Petrinet;
import qut.edu.au.petrinet.logic.Transition;
import qut.edu.au.petrinet.logic.Place;


public class Test {

    public static void main(String[] args) {
        Petrinet pn = new Petrinet("Wechsel");
        Transition t1 = pn.transition("t1");
        Transition t2 = pn.transition("t2");

        Place p1 = pn.place("p1", 1);
        Place p2 = pn.place("p2");
        
        Arc a1 = pn.arc("a1", p1, t1);
        Arc a2 = pn.arc("a2", t1, p2);
        Arc a3 = pn.arc("a3", p2, t2);
        Arc a4 = pn.arc("a4", t2, p1);

        PetrinetGUI.displayPetrinet(pn);
    }
    
}