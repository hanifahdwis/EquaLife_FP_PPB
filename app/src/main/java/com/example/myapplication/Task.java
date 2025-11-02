package com.example.myapplication;

public class Task {
    private String taskName;
    private String date;
    private String startTime;
    private String endTime;

    public Task(String taskName, String date, String startTime, String endTime) {
        this.taskName = taskName;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters
    public String getTaskName() {
        return taskName;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    // Getter gabungan untuk waktu
    public String getTimeRange() {
        return startTime + " - " + endTime;
    }
}