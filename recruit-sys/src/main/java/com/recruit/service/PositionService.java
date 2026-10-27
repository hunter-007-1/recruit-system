package com.recruit.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.recruit.chain.dto.ChainVerifyResult;
import com.recruit.common.mybatis.Page;
import com.recruit.dto.position.CreateOrUpdatePositionDTO;
import com.recruit.model.PositionDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.recruit.model.result.PositionResultDO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Min
 * @since 2022-04-02
 */
public interface PositionService extends IService<PositionDO> {

    PositionResultDO getById(Integer id);

    boolean updateState(Integer id, Integer state);

    boolean updateHits(Integer id);

    PositionDO findById(Integer id);

    boolean createPosition(CreateOrUpdatePositionDTO validator);

    boolean updatePosition(PositionDO positionDO, CreateOrUpdatePositionDTO validator);

    List<PositionResultDO> sort();

    IPage<PositionDO> getPage(Page<PositionDO> pager, Integer state);

    IPage<PositionResultDO> getPageByKeyword(Page<PositionResultDO> pager, String keyword);

    // ========== 区块链相关方法 ==========

    /**
     * 创建职位并同步上链
     * 先持久化到 MySQL，再将核心数据哈希上链到长安链
     * 包含幂等校验，防止重复提交导致重复上链
     *
     * @param validator 职位参数
     * @return 创建是否成功
     */
    boolean createPositionWithChain(CreateOrUpdatePositionDTO validator);

    /**
     * 校验职位数据的链上一致性
     * 对比本地数据库数据哈希与链上存储哈希是否一致
     *
     * @param positionId 职位ID
     * @return 校验结果
     */
    ChainVerifyResult verifyChainConsistency(Integer positionId);
}
