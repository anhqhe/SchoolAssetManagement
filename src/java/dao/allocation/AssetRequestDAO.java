/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.allocation;

import java.time.LocalDateTime;
import java.util.List;
import model.Allocation.AssetRequest;

/**
 *
 * @author Leo
 */
public class AssetRequestDAO {

    public List<AssetRequest> getRequestsByTeacher(long userId) {
        //demo
        List<AssetRequest> list = List.of(
                new AssetRequest(1L, "REQ-202601-001", 101L, 1L,
                        "Yêu cầu máy chiếu cho lớp học", "PENDING", LocalDateTime.of(2026, 1, 10, 9, 30)),
                new AssetRequest(2L, "REQ-202601-002", 102L, 2L,
                        "Sửa chữa máy tính trong phòng thực hành", "APPROVED", LocalDateTime.of(2026, 1, 11, 14, 45)),
                new AssetRequest(3L, "REQ-202601-003", 103L, 3L,
                        "Yêu cầu thêm bàn ghế cho hội thảo", "REJECTED", LocalDateTime.of(2026, 1, 12, 8, 15)),
                new AssetRequest(4L, "REQ-202601-004", 101L, 4L,
                        "Mượn micro và loa cho buổi thuyết trình", "IN_PROGRESS", LocalDateTime.of(2026, 1, 13, 10, 0)),
                new AssetRequest(5L, "REQ-202601-005", 104L, 1L,
                        "Yêu cầu cập nhật phần mềm cho phòng máy", "COMPLETED", LocalDateTime.of(2026, 1, 14, 16, 20)),
                new AssetRequest(6L, "REQ-202601-006", 105L, 5L,
                        "Lắp đặt camera cho phòng họp", "PENDING", LocalDateTime.of(2026, 1, 15, 11, 5)),
                new AssetRequest(7L, "REQ-202601-007", 106L, 6L,
                        "Chuẩn bị thiết bị cho buổi đào tạo", "APPROVED", LocalDateTime.of(2026, 1, 16, 9, 0)),
                new AssetRequest(8L, "REQ-202601-008", 107L, 7L,
                        "Mượn bàn ghế bổ sung", "REJECTED", LocalDateTime.of(2026, 1, 17, 13, 30)),
                new AssetRequest(9L, "REQ-202601-009", 108L, 8L,
                        "Thiết bị âm thanh cho sự kiện nội bộ", "IN_PROGRESS", LocalDateTime.of(2026, 1, 18, 15, 45)),
                new AssetRequest(10L, "REQ-202601-010", 109L, 9L,
                        "Yêu cầu phòng họp cho cuộc họp ban lãnh đạo", "COMPLETED", LocalDateTime.of(2026, 1, 19, 10, 10))
        );
        return list;
    }
    
}
