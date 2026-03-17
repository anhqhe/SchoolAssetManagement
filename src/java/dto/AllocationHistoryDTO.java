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
public class AllocationHistoryDTO {
    private Long allocationId;
    private String allocationCode;
    private String requestCode;
    private String fromRoomName;
    private String toRoomName;
    private String receiverName;
    private String allocatedByName;
    private String status;
    private LocalDateTime allocatedAt;
    private String assetNames;

    public AllocationHistoryDTO() {
    }

    public Long getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(Long allocationId) {
        this.allocationId = allocationId;
    }

    public String getAllocationCode() {
        return allocationCode;
    }

    public void setAllocationCode(String allocationCode) {
        this.allocationCode = allocationCode;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }

    public String getFromRoomName() {
        return fromRoomName;
    }

    public void setFromRoomName(String fromRoomName) {
        this.fromRoomName = fromRoomName;
    }

    public String getToRoomName() {
        return toRoomName;
    }

    public void setToRoomName(String toRoomName) {
        this.toRoomName = toRoomName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getAllocatedByName() {
        return allocatedByName;
    }

    public void setAllocatedByName(String allocatedByName) {
        this.allocatedByName = allocatedByName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getAllocatedAt() {
        return allocatedAt;
    }

    public void setAllocatedAt(LocalDateTime allocatedAt) {
        this.allocatedAt = allocatedAt;
    }

    public String getAssetNames() {
        return assetNames;
    }

    public void setAssetNames(String assetNames) {
        this.assetNames = assetNames;
    }
}
