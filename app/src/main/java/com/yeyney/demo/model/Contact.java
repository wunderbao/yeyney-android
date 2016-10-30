package com.yeyney.demo.model;

import com.google.i18n.phonenumbers.Phonenumber;

import java.io.Serializable;

public class Contact implements Serializable {

    private static final long serialVersionUID = 8833549402782117225L;

    private String displayName;
    private Phonenumber.PhoneNumber number;
    private String plainNumber;
    private boolean selected;

    public Contact(String displayName) {
        this.displayName = displayName;
        this.selected = false;
    }

    public void setNumber(String plainNumber) {
        this.plainNumber = plainNumber;
    }

    public void setNumber(Phonenumber.PhoneNumber number) {
        this.number = number;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getNumber() {
        return number == null ? plainNumber : String.valueOf(number.getNationalNumber());
    }

    public void toggleSelect() {
        selected = !selected;
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public String toString() {
        return getNumber();
    }
}