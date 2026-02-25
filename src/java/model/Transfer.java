/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author ASUS
 */
import java.util.Date;

public class Transfer {
    private int transferId;
    private String transferCode;
    private String fromRoomName;
    private String toRoomName;
    private String requestedByName;
    private String reason;
    private String status;
    private Date createdAt;

    
    private String statusText;
    private String statusBadgeClass;
    
    public int getTransferId() { return transferId; }
    public void setTransferId(int transferId) { this.transferId = transferId; }

    public String getTransferCode() { return transferCode; }
    public void setTransferCode(String transferCode) { this.transferCode = transferCode; }

    public String getFromRoomName() { return fromRoomName; }
    public void setFromRoomName(String fromRoomName) { this.fromRoomName = fromRoomName; }

    public String getToRoomName() { return toRoomName; }
    public void setToRoomName(String toRoomName) { this.toRoomName = toRoomName; }

    public String getRequestedByName() { return requestedByName; }
    public void setRequestedByName(String requestedByName) { this.requestedByName = requestedByName; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }


    public String getStatusText() { return statusText; }
    public void setStatusText(String statusText) { this.statusText = statusText; }

    public String getStatusBadgeClass() { return statusBadgeClass; }
    public void setStatusBadgeClass(String statusBadgeClass) { this.statusBadgeClass = statusBadgeClass; }
    
}