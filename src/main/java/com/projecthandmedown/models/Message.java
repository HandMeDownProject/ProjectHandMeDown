package com.projecthandmedown.models;

public class Message {
    private String subject;
    private String body;
    private String sender;
    private String receiver;

    public Message() {
    }

    public Message(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    public Message(String subject, String body, String sender) {
        this.subject = subject;
        this.body = body;
        this.sender = sender;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
