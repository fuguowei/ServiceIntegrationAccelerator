/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.petrinet.logic;

public class InhibitorArc
extends Arc {

    
    protected InhibitorArc(String name, Place p, Transition t) {
        super(name, p, t);
    }

    /**
     * Es darf nur gefeuert werden,
     * wenn weniger Token als dsa Inhibitor-Gewicht auf der Stelle liegen
     */
    
    @Override
    public boolean canFire() {
        return (place.getTokens() < this.getWeight());
    }
    
    /**
     * beim feuern einer Inhibitor-Kante gehen keine Tokens die Kante entlang
     */
    @Override
    public void fire() {
        // do nothing
    }

    
}