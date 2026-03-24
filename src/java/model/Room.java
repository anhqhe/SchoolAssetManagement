package model;


public class Room {
    private int roomId;          
    private String roomName;     
    private String description;
    private String location;
    private String headTeacherName;

    public Room() {
    }

    public Room(int roomId, String roomName, String description) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.description = description;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHeadTeacherName() {
        return headTeacherName;
    }

    public void setHeadTeacherName(String headTeacherName) {
        this.headTeacherName = headTeacherName;
    }

    @Override
    public String toString() {
        return "Room{" + "roomId=" + roomId + ", roomName=" + roomName + ", description=" + description + '}';
    }
}