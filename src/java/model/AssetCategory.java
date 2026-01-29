/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Leo
 */
public class AssetCategory {
    private Long categoryId;
    private String categoryCode;
    private String categoryName;
    private Long parentCategoryId;
    private boolean isActive;

    public AssetCategory() {
    }

    public AssetCategory(Long categoryId, String categoryCode, String categoryName, Long parentCategoryId, boolean isActive) {
        this.categoryId = categoryId;
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.parentCategoryId = parentCategoryId;
        this.isActive = isActive;
    }
    
    //getters

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Long getParentCategoryId() {
        return parentCategoryId;
    }

    public boolean isIsActive() {
        return isActive;
    }
    
    //setters

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setParentCategoryId(Long parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
    
}
