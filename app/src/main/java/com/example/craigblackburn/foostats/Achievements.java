package com.example.craigblackburn.foostats;

import android.database.SQLException;

public class Achievements extends FModel {
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

    public void save() throws SQLException {
        throw new SQLException("Save method for Achievmenet is currently not implemented.");
    }

}
