/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.allocation;

import java.time.LocalDateTime;

/**
 *
 * @author Leo
 */

public class Approval {
    private long approvalId;
    private String refType;
    private long refId;
    private long approverId;
    private String decision;
    private String decisionNote;
    private LocalDateTime decidedAt;

    public Approval() {
    }

    public Approval(long approvalId, String refType, long refId, long approverId, String decision, String decisionNote, LocalDateTime decidedAt) {
        this.approvalId = approvalId;
        this.refType = refType;
        this.refId = refId;
        this.approverId = approverId;
        this.decision = decision;
        this.decisionNote = decisionNote;
        this.decidedAt = decidedAt;
    }
    
    //getters

    public long getApprovalId() {
        return approvalId;
    }

    public String getRefType() {
        return refType;
    }

    public long getRefId() {
        return refId;
    }

    public long getApproverId() {
        return approverId;
    }

    public String getDecision() {
        return decision;
    }

    public String getDecisionNote() {
        return decisionNote;
    }

    public LocalDateTime getDecidedAt() {
        return decidedAt;
    }
    
    //setters

    public void setApprovalId(long approvalId) {
        this.approvalId = approvalId;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    public void setRefId(long refId) {
        this.refId = refId;
    }

    public void setApproverId(long approverId) {
        this.approverId = approverId;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public void setDecisionNote(String decisionNote) {
        this.decisionNote = decisionNote;
    }

    public void setDecidedAt(LocalDateTime decidedAt) {
        this.decidedAt = decidedAt;
    }
    
}
