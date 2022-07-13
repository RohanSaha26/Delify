package com.sahaprojects.drivechat.Models;

public class CollabTasks {
    private String ctaskId,ctaskBody,ctaskSenderId,ctaskReceiverId,ctaskIsCompletebySender,ctaskIsCompletebyReceiver;
    long ctaskTime;

    public CollabTasks(String ctaskBody, long ctaskTime, String ctaskSenderId, String ctaskReceiverId, String ctaskIsCompletebySender, String ctaskIsCompletebyReceiver) {
        this.ctaskBody = ctaskBody;
        this.ctaskTime = ctaskTime;
        this.ctaskSenderId = ctaskSenderId;
        this.ctaskReceiverId = ctaskReceiverId;
        this.ctaskIsCompletebySender = ctaskIsCompletebySender;
        this.ctaskIsCompletebyReceiver = ctaskIsCompletebyReceiver;
    }

    public String getCtaskId() {
        return ctaskId;
    }

    public void setCtaskId(String ctaskId) {
        this.ctaskId = ctaskId;
    }

    public String getCtaskBody() {
        return ctaskBody;
    }

    public void setCtaskBody(String ctaskBody) {
        this.ctaskBody = ctaskBody;
    }

    public long getCtaskTime() {
        return ctaskTime;
    }

    public void setCtaskTime(long ctaskTime) {
        this.ctaskTime = ctaskTime;
    }

    public String getCtaskSenderId() {
        return ctaskSenderId;
    }

    public void setCtaskSenderId(String ctaskSenderId) {
        this.ctaskSenderId = ctaskSenderId;
    }

    public String getCtaskReceiverId() {
        return ctaskReceiverId;
    }

    public void setCtaskReceiverId(String ctaskReceiverId) {
        this.ctaskReceiverId = ctaskReceiverId;
    }

    public String getCtaskIsCompletebySender() {
        return ctaskIsCompletebySender;
    }

    public void setCtaskIsCompletebySender(String ctaskIsCompletebySender) {
        this.ctaskIsCompletebySender = ctaskIsCompletebySender;
    }

    public String getCtaskIsCompletebyReceiver() {
        return ctaskIsCompletebyReceiver;
    }

    public void setCtaskIsCompletebyReceiver(String ctaskIsCompletebyReceiver) {
        this.ctaskIsCompletebyReceiver = ctaskIsCompletebyReceiver;
    }
}
