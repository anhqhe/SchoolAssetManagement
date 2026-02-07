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
 * @author Leo
 */
public class Asset {

    private long assetId;
    private String assetCode;
    private String assetName;
    private long categoryId;
    private String serialNumber;
    private String model;
    private String brand;
    private String originNote;
    private LocalDateTime purchaseDate;
    private LocalDateTime receivedDate;
    private String conditionNote;
    private String status;
    private long currentRoomId;
    private long currentHolderId;
    private boolean isActive;
    private LocalDateTime createdAt;  // map với [CreatedAt]
    private LocalDateTime updatedAt;  // map với [UpdatedAt]
    private String categoryName;   // từ JOIN AssetCategories
    private String roomName;       // từ JOIN Rooms
    private String roomLocation;   // từ JOIN Rooms
    private String holderName;     // từ JOIN Users
    private int quantity;         // số lượng loại tài sản (cùng AssetName + CategoryId)

    public Asset() {
    }

    public long getAssetId() {
        return assetId;
    }

    public void setAssetId(long assetId) {
        this.assetId = assetId;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getOriginNote() {
        return originNote;
    }

    public void setOriginNote(String originNote) {
        this.originNote = originNote;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDateTime getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDateTime receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getConditionNote() {
        return conditionNote;
    }

    public void setConditionNote(String conditionNote) {
        this.conditionNote = conditionNote;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCurrentRoomId() {
        return currentRoomId;
    }

    public void setCurrentRoomId(long currentRoomId) {
        this.currentRoomId = currentRoomId;
    }

    public long getCurrentHolderId() {
        return currentHolderId;
    }

    public void setCurrentHolderId(long currentHolderId) {
        this.currentHolderId = currentHolderId;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomLocation() {
        return roomLocation;
    }

    public void setRoomLocation(String roomLocation) {
        this.roomLocation = roomLocation;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatusBadgeClass() {
        if (status == null) {
            return "badge-secondary";
        }
        switch (status) {
            case "IN_STOCK":
                return "badge-info";
            case "IN_USE":
                return "badge-success";
            case "RETIRED":
                return "badge-danger";
            default:
                return "badge-secondary";
        }
    }
    
    public String getStatusText() {
        if (status == null) {
            return "N/A";
        }
        switch (status) {
            case "IN_STOCK":
                return "Trong kho";
            case "IN_USE":
                return "Đang sử dụng";
            default:
                return status;
        }
    }

    public boolean getActive() {
        return isActive;
    }

    /** Chuyển LocalDateTime sang Date cho JSP fmt:formatDate */
    public Date getPurchaseDateAsDate() {
        return purchaseDate == null ? null : Date.from(purchaseDate.atZone(ZoneId.systemDefault()).toInstant());
    }

    public Date getReceivedDateAsDate() {
        return receivedDate == null ? null : Date.from(receivedDate.atZone(ZoneId.systemDefault()).toInstant());
    }

    public Date getCreatedAtAsDate() {
        return createdAt == null ? null : Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant());
    }
}
