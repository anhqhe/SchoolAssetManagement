package model;

import java.util.Date;

public class Asset {
    private long assetId;
    private String assetCode;
    private String assetName;
    private long categoryId;
    private String categoryName; // Join from AssetCategories
    private String serialNumber;
    private String model;
    private String brand;
    private String originNote;
    private Date purchaseDate;
    private Date receivedDate;
    private String conditionNote;
    private String status; // IN_STOCK, IN_USE, MAINTENANCE, DAMAGED, etc.
    private Long currentRoomId;
    private String roomName; // Join from Rooms
    private String roomLocation; // Join from Rooms
    private Long currentHolderId;
    private String holderName; // Join from Users
    private boolean isActive;
    private Date createdAt;
    private Date updatedAt;

    // Getters and Setters
    public long getAssetId() { return assetId; }
    public void setAssetId(long assetId) { this.assetId = assetId; }

    public String getAssetCode() { return assetCode; }
    public void setAssetCode(String assetCode) { this.assetCode = assetCode; }

    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }

    public long getCategoryId() { return categoryId; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getOriginNote() { return originNote; }
    public void setOriginNote(String originNote) { this.originNote = originNote; }

    public Date getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(Date purchaseDate) { this.purchaseDate = purchaseDate; }

    public Date getReceivedDate() { return receivedDate; }
    public void setReceivedDate(Date receivedDate) { this.receivedDate = receivedDate; }

    public String getConditionNote() { return conditionNote; }
    public void setConditionNote(String conditionNote) { this.conditionNote = conditionNote; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getCurrentRoomId() { return currentRoomId; }
    public void setCurrentRoomId(Long currentRoomId) { this.currentRoomId = currentRoomId; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public String getRoomLocation() { return roomLocation; }
    public void setRoomLocation(String roomLocation) { this.roomLocation = roomLocation; }

    public Long getCurrentHolderId() { return currentHolderId; }
    public void setCurrentHolderId(Long currentHolderId) { this.currentHolderId = currentHolderId; }

    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    
    /**
     * Helper method để lấy badge color theo status
     */
    public String getStatusBadgeClass() {
        if (status == null) return "badge-secondary";
        
        switch (status) {
            case "IN_STOCK":
                return "badge-info";
            case "IN_USE":
                return "badge-success";
            case "MAINTENANCE":
                return "badge-warning";
            case "DAMAGED":
                return "badge-danger";
            case "DISPOSED":
                return "badge-dark";
            default:
                return "badge-secondary";
        }
    }
    
    /**
     * Helper method để format status text
     */
    public String getStatusText() {
        if (status == null) return "N/A";
        
        switch (status) {
            case "IN_STOCK":
                return "Trong kho";
            case "IN_USE":
                return "Đang sử dụng";
            case "MAINTENANCE":
                return "Bảo trì";
            case "DAMAGED":
                return "Hỏng hóc";
            case "DISPOSED":
                return "Đã thanh lý";
            default:
                return status;
        }
    }
}
