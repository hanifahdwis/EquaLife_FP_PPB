package com.example.myapplication;

public class UserProfile {
    String name;
    String ageRange;
    String height;
    String weight;
    String dietPref;

    // Constructor
    public UserProfile(String name, String ageRange, String height, String weight, String dietPref) {
        this.name = name;
        this.ageRange = ageRange;
        this.height = height;
        this.weight = weight;
        this.dietPref = dietPref;
    }

    // Getters
    public String getName() { return name; }
    public String getAgeRange() { return ageRange; }
    public String getHeight() { return height; }
    public String getWeight() { return weight; }
    public String getDietPref() { return dietPref; }
}