package com.example.messengertest1.model.pojo;

public class Message { //этот pojo класс представляет собой сущность Message

    private String text; //текст сообщения
    private String senderId; //id отправителя
    private String receiverId; //id получателя

    public Message(String text, String senderId, String receiverId) {
        this.text = text;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public Message() {
    }

    public String getText() {
        return text;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverid() {
        return receiverId;
    }
}
