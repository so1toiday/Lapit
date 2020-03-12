package com.example.lapit.model;

public class Message {
    boolean seen;
    String text;
    long time;
    String type;
    String rq_type;

    public Message(boolean seen, String text, long time, String type, String rq_type) {
        this.seen = seen;
        this.text = text;
        this.time = time;
        this.type = type;
        this.rq_type = rq_type;
    }

    public Message() {
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRq_type() {
        return rq_type;
    }

    public void setRq_type(String rq_type) {
        this.rq_type = rq_type;
    }
}
