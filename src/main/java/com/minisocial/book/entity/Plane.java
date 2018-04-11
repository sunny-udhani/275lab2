package com.minisocial.book.entity;

import org.json.JSONObject;

import javax.persistence.Embeddable;

@Embeddable
public class Plane {
    private int capacity;
    private String model;
    private String manufacturer;
    private int year;

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Plane(int capacity, String model, String manufacturer, int year) {
        this.capacity = capacity;
        this.model = model;
        this.manufacturer = manufacturer;
        this.year = year;
    }
    public Plane() {
    }

    /**

     * Plane details as JSON
     *
     * @return JSONObject
     */
    public JSONObject getJSON() {
        JSONObject plane = new JSONObject();
        plane.put("capacity", this.getCapacity());
        plane.put("model", this.getModel());
        plane.put("manufacturer", this.getManufacturer());
        plane.put("year", this.getYear());
        return plane;
    }


}
