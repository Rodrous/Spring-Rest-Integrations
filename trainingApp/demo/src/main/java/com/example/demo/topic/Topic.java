package com.example.demo.topic;

public class Topic {
    private String id;
    private String Name;
    private String Description;


    public Topic() {

    }
    public Topic(String id, String name, String description) {
        this.id = id;
        Name = name;
        Description = description;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
