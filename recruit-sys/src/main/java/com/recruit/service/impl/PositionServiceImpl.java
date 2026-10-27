package com.recruit.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.recruit.chain.dto.ChainVerifyResult;
import com.recruit.chain.model.ChainRecordDO;
import com.recruit.chain.service.ChainService;
import com.recruit.chain.util.PositionHashUtil;
import com.recruit.common.mybatis.Page;
import com.recruit.dto.position.CreateOrUpdatePositionDTO;
import com.recruit.model.PositionDO;
import com.recruit.mapper.PositionMapper;
import com.recruit.model.result.PositionResultDO;
import com.recruit.service.PositionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  职位服务实现类 — 集成区块链上链逻辑
 * </p>
 *
 * 核心设计：
 * 1. 职位创建时同步上链：先写MySQL → 再哈希上链 → 更新链上状态
 * 2. 一致性校验：从链上查询哈希与本地数据比对
 * 3. 幂等控制：基于数据哈希的幂等Key防止重复上链
 *
 * @author Min / SuoMingFei
 * @since 2022-04-02
 */
@Slf4j
@Service
public class PositionServiceImpl extends ServiceImpl<PositionMapper, PositionDO> implements PositionService {

    @Resource
    private PositionMapper positionMapper;

    @Autowired(required = false)
    private ChainService chainService;

    @Override
    public PositionResultDO getById(Integer id) {
        return positionMapper.getById(id);
    }

    @Override
    public boolean updateState(Integer id, Integer state) {
        return positionMapper.updateState(id, state);
    }

    @Override
    public boolean updateHits(Integer id) {
        return positionMapper.updateHits(id);
    }

    @Override
    public PositionDO findById(Integer id) {
        return positionMapper.selectById(id);
    }

    @Override
    public boolean createPosition(CreateOrUpdatePositionDTO validator) {
        PositionDO positionDO = buildPositionDO(validator);
        return positionMapper.insert(positionDO) > 0;
    }

    @Override
    public boolean updatePosition(PositionDO positionDO, CreateOrUpdatePositionDTO validator) {
        positionDO.setTitle(validator.getTitle());
        positionDO.setRequirement(validator.getRequirement());
        positionDO.setQuantity(validator.getQuantity());
        positionDO.setCity(validator.getCity());
        positionDO.setSalaryUp(validator.getSalaryUp());
        positionDO.setSalaryDown(validator.getSalaryDown());
        positionDO.setCategoryId(validator.getCategoryId());
        positionDO.setHrId(validator.getHrId());
        positionDO.setCompanyId(validator.getCompanyId());
        // 更新职位信息之后，需要管理员重新审核，把职位状态state改为0
        positionDO.setState(0);
        // 更新后链上数据需要重新校验，将链上状态标记为未同步
        positionDO.setChainStatus(0);
        return positionMapper.updateById(positionDO) > 0;
    }

    @Override
    public List<PositionResultDO> sort() {
        return positionMapper.sortByHits();
    }

    @Override
    public IPage<PositionDO> getPage(Page<PositionDO> pager, Integer state) {
        return positionMapper.getPage(pager, state);
    }

    @Override
    public IPage<PositionResultDO> getPageByKeyword(Page<PositionResultDO> pager, String keyword) {
        return positionMapper.getPageByKeyword(pager, keyword);
    }

    // ========== 区块链集成方法 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createPositionWithChain(CreateOrUpdatePositionDTO validator) {
        // 1. 先持久化到 MySQL（事务保证）
        PositionDO positionDO = buildPositionDO(validator);
        positionDO.setChainStatus(0); // 初始状态：未上链
        positionMapper.insert(positionDO);

        // 2. 计算职位核心数据的 SHA-256 哈希
        String dataHash = PositionHashUtil.computePositionHash(positionDO);
        String bizDataJson = PositionHashUtil.toJson(positionDO);

        // 3. 调用长安链 SDK 上链存储
        if (chainService != null) {
            try {
                ChainRecordDO record = chainService.uploadToChain(
                        positionDO.getId(), dataHash, bizDataJson);

                // 4. 回写链上信息到职位表
                positionDO.setChainHash(dataHash);
                positionDO.setChainTxId(record.getTxId());
                positionDO.setChainStatus(1); // 已上链
                positionMapper.updateById(positionDO);

                log.info("职位[{}]创建并上链成功, Hash={}, TxId={}",
                        positionDO.getId(), dataHash, record.getTxId());

            } catch (Exception e) {
                // 上链失败不影响职位创建，标记状态即可
                positionDO.setChainHash(dataHash);
                positionDO.setChainStatus(2); // 上链失败
                positionMapper.updateById(positionDO);
                log.error("职位[{}]上链失败，已标记状态，职位数据正常保存", positionDO.getId(), e);
                // 不抛出异常 — 上链失败不影响核心业务
            }
        } else {
            log.warn("链服务未启用，职位[{}]仅保存到本地数据库", positionDO.getId());
            positionDO.setChainHash(dataHash);
            positionDO.setChainStatus(0);
            positionMapper.updateById(positionDO);
        }

        return true;
    }

    @Override
    public ChainVerifyResult verifyChainConsistency(Integer positionId) {
        // 1. 查询本地职位数据
        PositionDO position = positionMapper.selectById(positionId);
        if (position == null) {
            return ChainVerifyResult.builder()
                    .positionId(positionId)
                    .consistent(false)
                    .message("职位数据不存在")
                    .build();
        }

        // 2. 计算本地哈希
        String localHash = PositionHashUtil.computePositionHash(position);

        // 3. 调用链上一致性校验
        if (chainService == null) {
            return ChainVerifyResult.builder()
                    .positionId(positionId)
                    .consistent(false)
                    .localHash(localHash)
                    .message("链服务未启用")
                    .build();
        }

        boolean consistent = chainService.verifyConsistency(positionId);

        return ChainVerifyResult.builder()
                .positionId(positionId)
                .consistent(consistent)
                .localHash(localHash)
                .chainHash(position.getChainHash())
                .txId(position.getChainTxId())
                .message(consistent ? "数据一致性校验通过" : "数据可能已被篡改")
                .build();
    }

    /**
     * 构建职位实体
     */
    private PositionDO buildPositionDO(CreateOrUpdatePositionDTO validator) {
        PositionDO positionDO = new PositionDO();
        positionDO.setTitle(validator.getTitle());
        positionDO.setRequirement(validator.getRequirement());
        positionDO.setQuantity(validator.getQuantity());
        positionDO.setCity(validator.getCity());
        positionDO.setSalaryUp(validator.getSalaryUp());
        positionDO.setSalaryDown(validator.getSalaryDown());
        positionDO.setCategoryId(validator.getCategoryId());
        positionDO.setHrId(validator.getHrId());
        positionDO.setCompanyId(validator.getCompanyId());
        return positionDO;
    }
}
