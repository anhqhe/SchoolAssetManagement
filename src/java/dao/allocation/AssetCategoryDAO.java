/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.allocation;

import java.util.List;
import model.Allocation.AssetCategory;

/**
 *
 * @author Leo
 */
public class AssetCategoryDAO {

    public List<AssetCategory> getAllActiveCategories() {
        //demo
        List<AssetCategory> list = List.of(
                new AssetCategory(1L, "CAT-001", "Chung", null, true),
                new AssetCategory(2L, "CAT-002", "Văn phòng", 1L, true),
                new AssetCategory(3L, "CAT-003", "Thiết bị", 1L, true),
                new AssetCategory(4L, "CAT-004", "Văn phòng - Ghế", 2L, true),
                new AssetCategory(5L, "CAT-005", "Văn phòng - Bàn", 2L, true),
                new AssetCategory(6L, "CAT-006", "Thiết bị - Mạng", 3L, true),
                new AssetCategory(7L, "CAT-007", "Thiết bị - PC", 3L, false),
                new AssetCategory(8L, "CAT-008", "Phụ kiện", 1L, true),
                new AssetCategory(9L, "CAT-009", "Phụ kiện - Cáp", 8L, true),
                new AssetCategory(10L,"CAT-010", "Khác", null, true)
        );
        return  list;
    }
    
}
