/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.Allocation;

/**
 *
 * @author Leo
 */
public class Room {
    private long roomId;
    private String roomCode;
    private String roomName;
    private String location;
    private boolean isActive;

    public Room() {
    }

    public Room(long roomId, String roomCode, String roomName, String location, boolean isActive) {
        this.roomId = roomId;
        this.roomCode = roomCode;
        this.roomName = roomName;
        this.location = location;
        this.isActive = isActive;
    }
    
    //getters
    public long getRoomId() {
        return roomId;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public String getLocation() {
        return location;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public String getRoomName() {
        return roomName;
    }
    
    
    //setters
    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
    
            
}
