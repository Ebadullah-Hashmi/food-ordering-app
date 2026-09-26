package com.example.semesterproject.domain;

public class Price {
    private int Id;
    private String Value;

    @Override
    public String toString() {
        return Value ;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public Price() {
    }
}
