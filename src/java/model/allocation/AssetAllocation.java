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

public class AssetAllocation {
    private Long allocationId;
    private String allocationCode;
    private Long requestId;
    private Long fromRoomId;
    private Long toRoomId;
    private Long receiverId;
    private Long allocatedById;
    private String status;
    private String note;
    private LocalDateTime allocatedAt;

    public AssetAllocation() {
    }

    public AssetAllocation(Long allocationId, String allocationCode, Long requestId, Long fromRoomId, Long toRoomId, Long receiverId, Long allocatedById, String status, String note, LocalDateTime allocatedAt) {
        this.allocationId = allocationId;
        this.allocationCode = allocationCode;
        this.requestId = requestId;
        this.fromRoomId = fromRoomId;
        this.toRoomId = toRoomId;
        this.receiverId = receiverId;
        this.allocatedById = allocatedById;
        this.status = status;
        this.note = note;
        this.allocatedAt = allocatedAt;
    }
    
    //getters

    public Long getAllocationId() {
        return allocationId;
    }

    public String getAllocationCode() {
        return allocationCode;
    }

    public Long getRequestId() {
        return requestId;
    }

    public Long getFromRoomId() {
        return fromRoomId;
    }

    public Long getToRoomId() {
        return toRoomId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public Long getAllocatedById() {
        return allocatedById;
    }

    public String getStatus() {
        return status;
    }

    public String getNote() {
        return note;
    }

    public LocalDateTime getAllocatedAt() {
        return allocatedAt;
    }
    
    //setters

    public void setAllocationId(Long allocationId) {
        this.allocationId = allocationId;
    }

    public void setAllocationCode(String allocationCode) {
        this.allocationCode = allocationCode;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public void setFromRoomId(Long fromRoomId) {
        this.fromRoomId = fromRoomId;
    }

    public void setToRoomId(Long toRoomId) {
        this.toRoomId = toRoomId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public void setAllocatedById(Long allocatedById) {
        this.allocatedById = allocatedById;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setAllocatedAt(LocalDateTime allocatedAt) {
        this.allocatedAt = allocatedAt;
    }
    
}
