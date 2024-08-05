package com.example.android2_lab7_ph50030.models;

import java.util.HashMap;

public class Todo {
    private String id;
    private String title;
    private String content;
    private String date;
    private String type;
    private int status;

    public HashMap<String,Object> convertHashMap(){
        HashMap<String,Object> work=new HashMap<>();
        work.put("id",id);
        work.put("title",title);
        work.put("content",content);
        work.put("date",date);
        work.put("type",type);
        work.put("status",status);
        return work;
    }

    public Todo() {};

    public Todo(String title, String content, String date, String type, int status) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.type = type;
        this.status = status;
    }

    public Todo(String id, String title, String content, String date, String type, int status) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.type = type;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
