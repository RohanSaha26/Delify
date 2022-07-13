package com.sahaprojects.drivechat.Models;


public class Message {
   private String messageId,message,senderId,receiverId,senderRoom,readReceipt;
   private long timestamp;

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderRoom() {
        return senderRoom;
    }

    public void setSenderRoom(String senderRoom) {
        this.senderRoom = senderRoom;
    }

    public Message() {
    }

    public String getReadReceipt() {
        return readReceipt;
    }

    public void setReadReceipt(String readReceipt) {
        this.readReceipt = readReceipt;
    }

    public Message(String message, String senderId, String receiverId, String senderRoom, long timestamp, String readReceipt) {
//        this.messageId = messageId;
        this.message = message;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.senderRoom = senderRoom;
        this.timestamp = timestamp;
        this.readReceipt =readReceipt;
    }

//    public Message(String message, String senderId, long timestamp) {
//        this.message = message;
//        this.senderId = senderId;
//        this.timestamp = timestamp;
//    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

//    public String getRsa() {
//        return rsa;
//    }
//
//    public void setRsa(String rsa) {
//        this.rsa = rsa;
//    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
