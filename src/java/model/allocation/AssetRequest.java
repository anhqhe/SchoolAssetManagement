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
public class AssetRequest {
    private Long requestId;
    private String requestCode;
    private Long teacherId;
    private Long requestedRoomId;
    private String purpose;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AssetRequest() {
    }

    public AssetRequest(Long requestId, String requestCode, Long teacherId, Long requestedRoomId, String purpose, String status, LocalDateTime createdAt) {
        this.requestId = requestId;
        this.requestCode = requestCode;
        this.teacherId = teacherId;
        this.requestedRoomId = requestedRoomId;
        this.purpose = purpose;
        this.status = status;
        this.createdAt = createdAt;
    }
    
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
    
    
}
