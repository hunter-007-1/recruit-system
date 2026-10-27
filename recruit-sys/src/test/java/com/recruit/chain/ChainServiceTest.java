package com.recruit.chain;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.recruit.chain.mapper.ChainRecordMapper;
import com.recruit.chain.model.ChainRecordDO;
import com.recruit.chain.service.impl.ChainServiceImpl;
import com.recruit.chain.util.PositionHashUtil;
import com.recruit.mapper.PositionMapper;
import com.recruit.model.PositionDO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 区块链服务单元测试
 */
@RunWith(MockitoJUnitRunner.class)
public class ChainServiceTest {

    @InjectMocks
    private ChainServiceImpl chainService;

    @Mock
    private ChainRecordMapper chainRecordMapper;

    @Mock
    private PositionMapper positionMapper;

    @Test
    public void testIsAlreadyOnChainReturnsTrue() {
        String idempotentKey = "test_key_123";
        when(chainRecordMapper.selectCount(any(QueryWrapper.class))).thenReturn(1);
        boolean result = chainService.isAlreadyOnChain(idempotentKey);
        assertTrue(result);
    }

    @Test
    public void testIsAlreadyOnChainReturnsFalse() {
        String idempotentKey = "new_key_456";
        when(chainRecordMapper.selectCount(any(QueryWrapper.class))).thenReturn(0);
        boolean result = chainService.isAlreadyOnChain(idempotentKey);
        assertFalse(result);
    }

    @Test
    public void testVerifyConsistencyReturnsFalseWhenNoRecord() {
        when(chainRecordMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);
        boolean result = chainService.verifyConsistency(999);
        assertFalse(result);
    }

    @Test
    public void testChainRecordPersistence() {
        ChainRecordDO record = new ChainRecordDO();
        record.setBizId(1);
        record.setBizType("position");
        record.setDataHash("abc123");
        record.setChainStatus(0);
        record.setIdempotentKey("key123");
        when(chainRecordMapper.insert(any(ChainRecordDO.class))).thenReturn(1);
        int result = chainRecordMapper.insert(record);
        assertEquals(1, result);
        verify(chainRecordMapper).insert(record);
    }
}
