/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.allocation;

import java.util.List;
import model.Allocation.Room;

/**
 *
 * @author Leo
 */
public class RoomDAO {

    public List<Room> getAllActiveRooms() {
        //demo
        List<Room> list = List.of(
            new Room(1L, "RM-001", "Phòng họp A", "Tầng 1 - Tòa nhà A", true),
            new Room(2L, "RM-002", "Phòng họp B", "Tầng 1 - Tòa nhà A", true),
            new Room(3L, "RM-003", "Phòng họp C", "Tầng 2 - Tòa nhà A", true),
            new Room(4L, "RM-004", "Phòng họp Lớn", "Tầng 3 - Tòa nhà B", true),
            new Room(5L, "RM-005", "Phòng họp Nhỏ", "Tầng 3 - Tòa nhà B", true),
            new Room(6L, "RM-006", "Phòng đào tạo 1", "Tầng 4 - Tòa nhà C", true),
            new Room(7L, "RM-007", "Phòng đào tạo 2", "Tầng 4 - Tòa nhà C", false),
            new Room(8L, "RM-008", "Phòng thư giãn", "Tầng 1 - Tòa nhà C", true),
            new Room(9L, "RM-009", "Phòng Giám đốc", "Tầng 5 - Tòa nhà A", true),
            new Room(10L, "RM-010", "Phòng Khách", "Tầng trệt - Tòa nhà A", true)
        );
        return list;
    }
    
}
