/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.allocation;

import java.time.LocalDateTime;

/**
 *
 * @author Leo
 */
public class Notification {
    private long notificationId;
    private long receiverId;
    private String title;
    private String content;
    private String refType;
    private long refId;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public Notification() {
    }

    public Notification(long notificationId, long receiverId, String title, String content, String refType, long refId, boolean isRead, LocalDateTime createdAt, LocalDateTime readAt) {
        this.notificationId = notificationId;
        this.receiverId = receiverId;
        this.title = title;
        this.content = content;
        this.refType = refType;
        this.refId = refId;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.readAt = readAt;
    }
    
    //getters

    public long getNotificationId() {
        return notificationId;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getRefType() {
        return refType;
    }

    public long getRefId() {
        return refId;
    }

    public boolean isRead() {
        return isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }
    
    //setters

    public void setNotificationId(long notificationId) {
        this.notificationId = notificationId;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    public void setRefId(long refId) {
        this.refId = refId;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
    
    
    
}
