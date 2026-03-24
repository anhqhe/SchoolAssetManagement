package model.asset;

public class AssetIncreaseItem {

    private long increaseItemId;
    private long increaseId;
    private long assetId;
    private String assetCode;  // JOIN Assets
    private String assetName;  // JOIN Assets
    private String note;

    public AssetIncreaseItem() {
    }

    public long getIncreaseItemId() { return increaseItemId; }
    public void setIncreaseItemId(long increaseItemId) { this.increaseItemId = increaseItemId; }

    public long getIncreaseId() { return increaseId; }
    public void setIncreaseId(long increaseId) { this.increaseId = increaseId; }

    public long getAssetId() { return assetId; }
    public void setAssetId(long assetId) { this.assetId = assetId; }

    public String getAssetCode() { return assetCode; }
    public void setAssetCode(String assetCode) { this.assetCode = assetCode; }

    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
