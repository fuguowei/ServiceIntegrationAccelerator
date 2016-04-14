/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author fuguo
 */
public class Attribute {

    public Attribute(String name) {
        this.name = name;
    }
    private String name;
    private String type;
    @JsonIgnore private String value;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
