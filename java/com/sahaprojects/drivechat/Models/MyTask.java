package com.sahaprojects.drivechat.Models;

public class MyTask {
    private String taskId,taskBody,taskSenderId,taskIsComplete;

    private long taskTime,taskUpdateTime;
    public MyTask(String taskBody, long taskTime, String taskSenderId, String taskIsComplete,long taskUpdateTime) {
//        this.taskId = taskId;
        this.taskBody = taskBody;
        this.taskTime = taskTime;
        this.taskSenderId = taskSenderId;
        this.taskIsComplete = taskIsComplete;
        this.taskUpdateTime = taskUpdateTime;
    }

    public MyTask() {
    }

    public long getTaskUpdateTime() {
        return taskUpdateTime;
    }

    public void setTaskUpdateTime(long taskUpdateTime) {
        this.taskUpdateTime = taskUpdateTime;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskBody() {
        return taskBody;
    }

    public void setTaskBody(String taskBody) {
        this.taskBody = taskBody;
    }

    public long getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(long taskTime) {
        this.taskTime = taskTime;
    }

    public String getTaskSenderId() {
        return taskSenderId;
    }

    public void setTaskSenderId(String taskSenderId) {
        this.taskSenderId = taskSenderId;
    }

    public String getTaskIsComplete() {
        return taskIsComplete;
    }


    public void setTaskIsComplete(String taskIsComplete) {
        this.taskIsComplete = taskIsComplete;
    }
}
