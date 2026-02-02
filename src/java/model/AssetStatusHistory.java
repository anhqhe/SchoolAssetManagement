/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;

/**
 *
 * @author Leo
 */
public class AssetStatusHistory {
    private long historyId;
    private long assetId;
    private String oldStatus;
    private String newStatus;
    private String reason;
    private long changedByUserId;
    private LocalDateTime changedAt;

    public AssetStatusHistory() {
    }

    public AssetStatusHistory(long historyId, long assetId, String oldStatus, String newStatus, String reason, long changedByUserId, LocalDateTime changedAt) {
        this.historyId = historyId;
        this.assetId = assetId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.reason = reason;
        this.changedByUserId = changedByUserId;
        this.changedAt = changedAt;
    }
    
    //getters

    public long getHistoryId() {
        return historyId;
    }

    public long getAssetId() {
        return assetId;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public String getReason() {
        return reason;
    }

    public long getChangedByUserId() {
        return changedByUserId;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }
    
    //setters

    public void setHistoryId(long historyId) {
        this.historyId = historyId;
    }

    public void setAssetId(long assetId) {
        this.assetId = assetId;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setChangedByUserId(long changedByUserId) {
        this.changedByUserId = changedByUserId;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
    
}
