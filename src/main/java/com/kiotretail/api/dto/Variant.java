package com.kiotretail.api.dto;

public class Variant {
    private int id;
    private String color;
    private String size;

    public Variant(int id, String color, String size) {
        this.id = id;
        this.color = color;
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
