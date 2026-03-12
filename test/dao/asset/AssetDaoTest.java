/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package dao.asset;

import java.util.List;
import model.asset.Asset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author An
 */
public class AssetDaoTest {
    
    public AssetDaoTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of findAll method, of class AssetDao.
     */
    @Test
    public void testFindAll() throws Exception {
        System.out.println("findAll");
        AssetDao instance = new AssetDao();
        List<Asset> expResult = null;
        List<Asset> result = instance.findAll();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of insert method, of class AssetDao.
     */
    @Test
    public void testInsert() throws Exception {
        System.out.println("insert");
        Asset a = null;
        AssetDao instance = new AssetDao();
        instance.insert(a);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of update method, of class AssetDao.
     */
    @Test
    public void testUpdate() throws Exception {
        System.out.println("update");
        Asset a = null;
        AssetDao instance = new AssetDao();
        instance.update(a);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of delete method, of class AssetDao.
     */
    @Test
    public void testDelete() throws Exception {
        System.out.println("delete");
        int assetId = 0;
        AssetDao instance = new AssetDao();
        instance.delete(assetId);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateStatus method, of class AssetDao.
     */
    @Test
    public void testUpdateStatus() throws Exception {
        System.out.println("updateStatus");
        long assetId = 0L;
        String newStatus = "";
        String reason = "";
        long changedByUserId = 0L;
        AssetDao instance = new AssetDao();
        instance.updateStatus(assetId, newStatus, reason, changedByUserId);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findById method, of class AssetDao.
     */
    @Test
    public void testFindById() throws Exception {
        System.out.println("findById");
        int id = 0;
        AssetDao instance = new AssetDao();
        Asset expResult = null;
        Asset result = instance.findById(id);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of searchAssets method, of class AssetDao.
     */
    @Test
    public void testSearchAssets() throws Exception {
        System.out.println("searchAssets");
        String keyword = "";
        String status = "";
        Long categoryId = null;
        Boolean isActive = null;
        AssetDao instance = new AssetDao();
        List<Asset> expResult = null;
        List<Asset> result = instance.searchAssets(keyword, status, categoryId, isActive);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of generateAssetCodes method, of class AssetDao.
     */
    @Test
    public void testGenerateAssetCodes() throws Exception {
        System.out.println("generateAssetCodes");
        long categoryId = 0L;
        int count = 0;
        AssetDao instance = new AssetDao();
        List<String> expResult = null;
        List<String> result = instance.generateAssetCodes(categoryId, count);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
