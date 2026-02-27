/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import java.time.LocalDateTime;

/**
 *
 * @author Leo
 */
public class AssetRequestDTO {
    private Long requestId;
    private String requestCode;
    private Long teacherId;
    private Long requestedRoomId;
    private String purpose;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private String teacherName;   // Users (FullName)
    private String roomName;      // Rooms (RoomName)
    
    //getters    

    public Long getRequestId() {
        return requestId;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public Long getRequestedRoomId() {
        return requestedRoomId;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getRoomName() {
        return roomName;
    }
    
    
    //setters

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public void setRequestedRoomId(Long requestedRoomId) {
        this.requestedRoomId = requestedRoomId;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
    
    
}
