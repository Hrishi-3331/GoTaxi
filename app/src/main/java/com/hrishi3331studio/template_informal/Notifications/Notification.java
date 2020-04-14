package com.hrishi3331studio.template_informal.Notifications;

public class Notification {

    private String date;
    private String subject;
    private String content;

    public Notification() {
    }

    public Notification(String date, String subject, String content) {
        this.date = date;
        this.subject = subject;
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
