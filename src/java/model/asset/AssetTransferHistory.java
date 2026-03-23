/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.asset;

import java.util.List;
import model.Transfer;

public class AssetTransferHistory {
    private int assetId;
    private String assetName;
    private String assetCode;
    private List<Transfer> transfers;

    public AssetTransferHistory(int assetId, String assetName, String assetCode, List<Transfer> transfers) {
        this.assetId   = assetId;
        this.assetName = assetName;
        this.assetCode = assetCode;
        this.transfers = transfers;
    }

    public String getLabel() {
        return assetName + " (" + assetCode + ")";
    }

    // Getters & Setters
    public int getAssetId()               { return assetId; }
    public void setAssetId(int assetId)   { this.assetId = assetId; }

    public String getAssetName()                    { return assetName; }
    public void setAssetName(String assetName)      { this.assetName = assetName; }

    public String getAssetCode()                    { return assetCode; }
    public void setAssetCode(String assetCode)      { this.assetCode = assetCode; }

    public List<Transfer> getTransfers()                   { return transfers; }
    public void setTransfers(List<Transfer> transfers)     { this.transfers = transfers; }
}