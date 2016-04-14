/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.petrinet.logic;
/**
 * Eine Kante geht von einer Stelle zu einer Transition oder umgekehrt.
 * Das wird ber die Konstruktoren abgebildet.
 * 
 * @author rmetzler
 */
public class Arc
extends PetrinetObject {

    Place place;
    Transition transition;
    Direction direction;
    int weight = 1;
    
    public enum Direction {
        
        /**
         * Die 2 Richtungen, die so eine Kante haben darf
         */
        
        PLACE_TO_TRANSITION {
            @Override
            public boolean canFire(Place p, int weight) {
                return p.hasAtLeastTokens(weight);
            }

            @Override
            public void fire(Place p, int weight) {
                p.removeTokens(weight);
            }

        },
        
        TRANSITION_TO_PLACE {
            @Override
            public boolean canFire(Place p, int weight) {
                return ! p.maxTokensReached(weight);
            }

            @Override
            public void fire(Place p, int weight) {
                p.addTokens(weight);
            }

        };

        public abstract boolean canFire(Place p, int weight);

        public abstract void fire(Place p, int weight);
    }
    
    private Arc(String name, Direction d, Place p, Transition t) {
        super(name);
        this.direction = d;
        this.place = p;
        this.transition = t;
    }

    protected Arc(String name, Place p, Transition t) {
        this(name, Direction.PLACE_TO_TRANSITION, p, t);
        t.addIncoming(this);
    }

    protected Arc(String name, Transition t, Place p) {
        this(name, Direction.TRANSITION_TO_PLACE, p, t);
        t.addOutgoing(this);
    }

    public boolean canFire() {
        return direction.canFire(place, weight);
    }
    
    public void fire() {
        this.direction.fire(place, this.weight);
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
    
    public int getWeight() {
        return weight;
    }
    
    public Place getPlace() {
        return place;
    }

    public Transition getTransition() {
        return transition;
    }

    public Direction getDirection() {
        return direction;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.direction == Direction.PLACE_TO_TRANSITION)
            sb.append(this.place.getName()).append(" -> ").append(this.transition.getName());
        else if (this.direction == Direction.TRANSITION_TO_PLACE)
            sb.append(this.transition.getName()).append(" -> ").append(this.place.getName());
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;

        if (object != null && object instanceof Arc)
        {
            //System.out.println("place--------"+this.place+"---------"+((Arc)object).place);
            //System.out.println("direction--------"+this.direction+"---------"+((Arc)object).direction);
            //System.out.println("transition--------"+this.transition+"---------"+((Arc)object).transition);
            if (this.direction !=null && ((Arc)object).direction != null && this.direction == ((Arc)object).direction 
                    && this.place !=null && ((Arc)object).place !=null && this.place.equals(((Arc)object).place)
                    && this.transition != null && ((Arc)object).transition != null && this.transition.equals(((Arc)object).transition))
                sameSame = true;
            //sameSame = this.direction == ((Arc)object).direction && this.place.equals(((Arc)object).place) && this.transition.equals(((Arc)object).transition);
        }
        return sameSame;
    }        
    
    
}