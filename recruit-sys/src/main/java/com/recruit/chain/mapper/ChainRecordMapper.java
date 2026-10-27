package com.recruit.chain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recruit.chain.model.ChainRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 链上交易记录 Mapper
 * @author SuoMingFei
 * @since 2024-09-01
 */
@Mapper
public interface ChainRecordMapper extends BaseMapper<ChainRecordDO> {
}
