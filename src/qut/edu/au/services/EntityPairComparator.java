/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.services;

import java.util.Comparator;
import qut.edu.au.entities.EntityPair;

/**
 *
 * @author fuguo
 */
public class EntityPairComparator implements Comparator<EntityPair>{
    public int compare(EntityPair o1, EntityPair o2) {
        return o1.getStrMainEntity().compareTo(o2.getStrMainEntity());
    }
}

