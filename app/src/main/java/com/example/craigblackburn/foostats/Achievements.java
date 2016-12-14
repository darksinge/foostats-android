package com.example.craigblackburn.foostats;

public class Achievements extends FModels {
    private String uuid;
    private String name;
    public Achievements(){}

    public Achievements(String id, String name) {
        this.uuid = id;
        this.name = name;
    }

    public String getId() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
