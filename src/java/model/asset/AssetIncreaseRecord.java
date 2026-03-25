package model.asset;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class AssetIncreaseRecord {

    private long increaseId;
    private String increaseCode;
    private String sourceType;
    private String sourceDetail;
    private LocalDate receivedDate;
    private long createdByUserId;
    private String createdByName; // JOIN Users
    private String note;
    private LocalDateTime createdAt;
    private int itemCount; // COUNT from AssetIncreaseItems

    public AssetIncreaseRecord() {
    }

    public long getIncreaseId() { return increaseId; }
    public void setIncreaseId(long increaseId) { this.increaseId = increaseId; }

    public String getIncreaseCode() { return increaseCode; }
    public void setIncreaseCode(String increaseCode) { this.increaseCode = increaseCode; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public String getSourceDetail() { return sourceDetail; }
    public void setSourceDetail(String sourceDetail) { this.sourceDetail = sourceDetail; }

    public LocalDate getReceivedDate() { return receivedDate; }
    public void setReceivedDate(LocalDate receivedDate) { this.receivedDate = receivedDate; }

    public long getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(long createdByUserId) { this.createdByUserId = createdByUserId; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }

    /** Helper: format sourceType sang tiếng Việt */
    public String getSourceTypeText() {
        if (sourceType == null) return "N/A";
        switch (sourceType) {
            case "Mua mới": return "Mua mới";
            case "Tiếp nhận": return "Tiếp nhận";
            case "Tặng": return "Tặng / Biếu";
            case "Khác": return "Khác";
            // Hỗ trợ giá trị cũ tiếng Anh nếu có
            case "MUA_MOI": return "Mua mới";
            case "TIEP_NHAN": return "Tiếp nhận";
            case "TANG": return "Tặng / Biếu";
            case "KHAC": return "Khác";
            case "PURCHASE": return "Mua mới";
            case "DONATION": return "Tặng / Biếu";
            case "TRANSFER": return "Tiếp nhận";
            default: return sourceType;
        }
    }

    /** Chuyển LocalDate sang Date cho JSP fmt:formatDate */
    public Date getReceivedDateAsDate() {
        return receivedDate == null ? null
                : Date.from(receivedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public Date getCreatedAtAsDate() {
        return createdAt == null ? null
                : Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant());
    }
}
