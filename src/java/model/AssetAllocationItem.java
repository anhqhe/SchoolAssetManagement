/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Leo
 */
public class AssetAllocationItem {
    private Long allocationItemId;
    private Long allocationId;
    private Long assetId;
    private String note;

    public AssetAllocationItem() {
    }

    public AssetAllocationItem(Long allocationItemId, Long allocationId, Long assetId, String note) {
        this.allocationItemId = allocationItemId;
        this.allocationId = allocationId;
        this.assetId = assetId;
        this.note = note;
    }
    
    //getters

    public Long getAllocationItemId() {
        return allocationItemId;
    }

    public Long getAllocationId() {
        return allocationId;
    }

    public Long getAssetId() {
        return assetId;
    }

    public String getNote() {
        return note;
    }
    
    //setters

    public void setAllocationItemId(Long allocationItemId) {
        this.allocationItemId = allocationItemId;
    }

    public void setAllocationId(Long allocationId) {
        this.allocationId = allocationId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public void setNote(String note) {
        this.note = note;
    }
    
}
