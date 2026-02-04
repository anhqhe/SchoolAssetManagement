/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author Leo
 */
public class AssetDTO {
    private Long assetId;
    private String assetCode;
    private String assetName;
    private Long categoryId;
    private String serialNumber;
    private String model;
    private String brand;
    private String originNote;
    private LocalDate purchaseDate;
    private LocalDate receivedDate;
    private String conditionNote;
    private String status;
    private Long currentRoomId;
    private Long currentHolderId;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private String categoryName;

    public AssetDTO() {
    }

    public AssetDTO(Long assetId, String assetCode, String assetName, Long categoryId, String serialNumber, String model, String brand, String originNote, LocalDate purchaseDate, LocalDate receivedDate, String conditionNote, String status, Long currentRoomId, Long currentHolderId, boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt, String categoryName) {
        this.assetId = assetId;
        this.assetCode = assetCode;
        this.assetName = assetName;
        this.categoryId = categoryId;
        this.serialNumber = serialNumber;
        this.model = model;
        this.brand = brand;
        this.originNote = originNote;
        this.purchaseDate = purchaseDate;
        this.receivedDate = receivedDate;
        this.conditionNote = conditionNote;
        this.status = status;
        this.currentRoomId = currentRoomId;
        this.currentHolderId = currentHolderId;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.categoryName = categoryName;
    }
    
    //getters

    public Long getAssetId() {
        return assetId;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public String getAssetName() {
        return assetName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getModel() {
        return model;
    }

    public String getBrand() {
        return brand;
    }

    public String getOriginNote() {
        return originNote;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public String getConditionNote() {
        return conditionNote;
    }

    public String getStatus() {
        return status;
    }

    public Long getCurrentRoomId() {
        return currentRoomId;
    }

    public Long getCurrentHolderId() {
        return currentHolderId;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getCategoryName() {
        return categoryName;
    }
    
    //setters

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setOriginNote(String originNote) {
        this.originNote = originNote;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    public void setConditionNote(String conditionNote) {
        this.conditionNote = conditionNote;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCurrentRoomId(Long currentRoomId) {
        this.currentRoomId = currentRoomId;
    }

    public void setCurrentHolderId(Long currentHolderId) {
        this.currentHolderId = currentHolderId;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
}
