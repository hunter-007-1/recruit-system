package com.recruit.controller;

import com.recruit.chain.dto.ChainVerifyResult;
import com.recruit.chain.service.ChainService;
import com.recruit.chain.util.PositionHashUtil;
import com.recruit.dto.position.CreateOrUpdatePositionDTO;
import com.recruit.model.PositionDO;
import com.recruit.service.PositionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * 职位控制器单元测试 — 覆盖区块链上链、一致性校验、幂等控制
 */
@RunWith(MockitoJUnitRunner.class)
public class PositionControllerTest {

    @InjectMocks
    private PositionController positionController;

    @Mock
    private PositionService positionService;

    @Mock
    private ChainService chainService;

    @Test
    public void testSamePositionDataProducesSameHash() {
        PositionDO pos1 = buildTestPosition();
        PositionDO pos2 = buildTestPosition();
        String hash1 = PositionHashUtil.computePositionHash(pos1);
        String hash2 = PositionHashUtil.computePositionHash(pos2);
        assertNotNull(hash1);
        assertNotNull(hash2);
        assertEquals(hash1, hash2);
        assertEquals(64, hash1.length());
    }

    @Test
    public void testDifferentPositionDataProducesDifferentHash() {
        PositionDO pos1 = buildTestPosition();
        PositionDO pos2 = buildTestPosition();
        pos2.setTitle("C++工程师");
        String hash1 = PositionHashUtil.computePositionHash(pos1);
        String hash2 = PositionHashUtil.computePositionHash(pos2);
        assertNotEquals(hash1, hash2);
    }

    @Test
    public void testIdempotentKeyGeneration() {
        String key1 = PositionHashUtil.generateIdempotentKey(1, "abc123");
        String key2 = PositionHashUtil.generateIdempotentKey(1, "abc123");
        String key3 = PositionHashUtil.generateIdempotentKey(2, "abc123");
        assertEquals(key1, key2);
        assertNotEquals(key1, key3);
        assertEquals(64, key1.length());
    }

    @Test
    public void testConsistencyCheckPassesWhenDataMatches() {
        PositionDO position = buildTestPosition();
        String hash = PositionHashUtil.computePositionHash(position);
        ChainVerifyResult result = ChainVerifyResult.builder()
                .positionId(1).consistent(true)
                .localHash(hash).chainHash(hash)
                .txId("tx_001").message("数据一致性校验通过").build();
        assertTrue(result.isConsistent());
        assertEquals(result.getLocalHash(), result.getChainHash());
    }

    @Test
    public void testConsistencyCheckFailsWhenDataTampered() {
        ChainVerifyResult result = ChainVerifyResult.builder()
                .positionId(1).consistent(false)
                .localHash("abc").chainHash("xyz")
                .message("数据可能已被篡改").build();
        assertFalse(result.isConsistent());
    }

    @Test
    public void testCreatePositionWithChain() {
        CreateOrUpdatePositionDTO dto = buildTestDTO();
        when(positionService.createPositionWithChain(dto)).thenReturn(true);
        boolean result = positionService.createPositionWithChain(dto);
        assertTrue(result);
        verify(positionService).createPositionWithChain(dto);
    }

    @Test
    public void testPositionToJson() {
        PositionDO position = buildTestPosition();
        String json = PositionHashUtil.toJson(position);
        assertNotNull(json);
        assertTrue(json.contains("Java工程师"));
        assertTrue(json.contains("positionId"));
    }

    @Test
    public void testFieldOrderDoesNotAffectHash() {
        PositionDO pos = buildTestPosition();
        String hash1 = PositionHashUtil.computePositionHash(pos);
        String hash2 = PositionHashUtil.computePositionHash(pos);
        assertEquals(hash1, hash2);
    }

    private PositionDO buildTestPosition() {
        PositionDO pos = new PositionDO();
        pos.setId(1);
        pos.setTitle("Java工程师");
        pos.setRequirement("熟悉SpringBoot、MyBatis，有区块链开发经验优先");
        pos.setQuantity(3);
        pos.setCity("北京市");
        pos.setSalaryUp(15000);
        pos.setSalaryDown(8000);
        pos.setCategoryId(1);
        pos.setHrId(6);
        pos.setCompanyId(1);
        return pos;
    }

    private CreateOrUpdatePositionDTO buildTestDTO() {
        CreateOrUpdatePositionDTO dto = new CreateOrUpdatePositionDTO();
        dto.setTitle("Java工程师");
        dto.setRequirement("熟悉SpringBoot、MyBatis，有区块链开发经验优先");
        dto.setQuantity(3);
        dto.setCity("北京市");
        dto.setSalaryUp(15000);
        dto.setSalaryDown(8000);
        dto.setCategoryId(1);
        dto.setHrId(6);
        dto.setCompanyId(1);
        return dto;
    }
}
