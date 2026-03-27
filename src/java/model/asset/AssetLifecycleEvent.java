/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.asset;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 *
 * @author An
 */


public class AssetLifecycleEvent {

    private long historyId;
    private long assetId;
    private String type;         // NEW, STATUS_CHANGE, ALLOCATION, TRANSFER, DELETED
    private String oldStatus;
    private String newStatus;
    private String reason;
    private long changedByUserId;
    private String changedByName; // JOIN từ Users
    private LocalDateTime changedAt;
    private Long oldRoomId;
    private Long newRoomId;
    private String oldRoomName;  // JOIN từ Rooms
    private String newRoomName;  // JOIN từ Rooms

    // --- Getters / Setters ---

    public long getHistoryId() { return historyId; }
    public void setHistoryId(long historyId) { this.historyId = historyId; }

    public long getAssetId() { return assetId; }
    public void setAssetId(long assetId) { this.assetId = assetId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getOldStatus() { return oldStatus; }
    public void setOldStatus(String oldStatus) { this.oldStatus = oldStatus; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public long getChangedByUserId() { return changedByUserId; }
    public void setChangedByUserId(long changedByUserId) { this.changedByUserId = changedByUserId; }

    public String getChangedByName() { return changedByName; }
    public void setChangedByName(String changedByName) { this.changedByName = changedByName; }

    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }

    public Long getOldRoomId() { return oldRoomId; }
    public void setOldRoomId(Long oldRoomId) { this.oldRoomId = oldRoomId; }

    public Long getNewRoomId() { return newRoomId; }
    public void setNewRoomId(Long newRoomId) { this.newRoomId = newRoomId; }

    public String getOldRoomName() { return oldRoomName; }
    public void setOldRoomName(String oldRoomName) { this.oldRoomName = oldRoomName; }

    public String getNewRoomName() { return newRoomName; }
    public void setNewRoomName(String newRoomName) { this.newRoomName = newRoomName; }

    /** Dùng cho fmt:formatDate trong JSP */
    public Date getChangedAtAsDate() {
        return changedAt == null ? null
                : Date.from(changedAt.atZone(ZoneId.systemDefault()).toInstant());
    }

    /** Text hiển thị loại sự kiện */
    public String getTypeText() {
        if (type == null) return "Không xác định";
        switch (type) {
            case "NEW":           return "Tạo mới";
            case "STATUS_CHANGE": return "Đổi trạng thái";
            case "ALLOCATION":    return "Cấp phát";
            case "TRANSFER":      return "Điều chuyển";
            case "DELETED":       return "Xóa";
            default: return type;
        }
    }

    /** CSS badge class theo type */
    public String getTypeBadgeClass() {
        if (type == null) return "badge-secondary";
        switch (type) {
            case "NEW":           return "badge-success";
            case "STATUS_CHANGE": return "badge-warning";
            case "ALLOCATION":    return "badge-info";
            case "TRANSFER":      return "badge-primary";
            case "DELETED":       return "badge-danger";
            default: return "badge-secondary";
        }
    }
}

