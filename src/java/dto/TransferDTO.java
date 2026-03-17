/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import java.util.Date;

public class TransferDTO {
    private int transferId;
    private String transferCode;
    private String fromRoomName;
    private String toRoomName;
    private String requestedByName;
    private String reason;
    private String status;
    private Date createdAt;

    
    private String statusText;
    private String statusBadgeClass;
    
    private String assetNames;
    
    private int assetId;        
    private int fromRoomId;    
    private int toRoomId;       
    private int requestedById;  
}
