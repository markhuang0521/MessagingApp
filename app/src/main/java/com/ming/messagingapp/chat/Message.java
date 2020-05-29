package com.ming.messagingapp.chat;

import java.util.ArrayList;

public class Message {
    private String messageId;
    private String message;
    private String senderId;
    private ArrayList<String> mediaUrlList;

    public Message(String messageId, String message, String senderId, ArrayList<String> mediaUrlList) {
        this.messageId = messageId;
        this.message = message;
        this.senderId = senderId;
        this.mediaUrlList = mediaUrlList;
    }

    public ArrayList<String> getMediaUrlList() {
        return mediaUrlList;
    }

    public void setMediaUrlList(ArrayList<String> mediaUrlList) {
        this.mediaUrlList = mediaUrlList;
    }

    public Message(String messageId, String text, String senderId) {
        this.messageId = messageId;
        this.message = text;
        this.senderId = senderId;
    }

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
}
