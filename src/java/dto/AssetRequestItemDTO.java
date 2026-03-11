/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

/**
 *
 * @author Leo
 */
public class AssetRequestItemDTO {

    private Long requestItemId;
    private Long requestId;
    private Long categoryId;
    private String assetNameHint;
    private int quantity;
    private String note;

    private String CategoryName;

    private int allocatedQuantity;
    private int remainingQuantity;

    public AssetRequestItemDTO() {
    }

    public AssetRequestItemDTO(Long requestItemId, Long requestId, Long categoryId, String assetNameHint, int quantity, String note, String CategoryName) {
        this.requestItemId = requestItemId;
        this.requestId = requestId;
        this.categoryId = categoryId;
        this.assetNameHint = assetNameHint;
        this.quantity = quantity;
        this.note = note;
        this.CategoryName = CategoryName;
    }

    //getters
    public Long getRequestItemId() {
        return requestItemId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getAssetNameHint() {
        return assetNameHint;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getNote() {
        return note;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public int getAllocatedQuantity() {
        return allocatedQuantity;
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }
    

    //setters
    public void setRequestItemId(Long requestItemId) {
        this.requestItemId = requestItemId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setAssetNameHint(String assetNameHint) {
        this.assetNameHint = assetNameHint;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setCategoryName(String CategoryName) {
        this.CategoryName = CategoryName;
    }

    public void setAllocatedQuantity(int allocatedQuantity) {
        this.allocatedQuantity = allocatedQuantity;
    }

    public void setRemainingQuantity(int remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    
}
