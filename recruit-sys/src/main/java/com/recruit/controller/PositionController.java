package com.recruit.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.recruit.chain.dto.ChainVerifyResult;
import com.recruit.chain.service.ChainService;
import com.recruit.chain.util.PositionHashUtil;
import io.github.talelin.autoconfigure.exception.NotFoundException;
import io.github.talelin.core.annotation.GroupRequired;
import io.github.talelin.core.annotation.Logger;
import io.github.talelin.core.annotation.PermissionMeta;
import io.github.talelin.core.annotation.PermissionModule;
import com.recruit.common.mybatis.Page;
import com.recruit.common.util.PageUtil;
import com.recruit.common.util.SingleUtil;
import com.recruit.dto.position.CreateOrUpdatePositionDTO;
import com.recruit.model.CompanyDO;
import com.recruit.model.NotifyDO;
import com.recruit.model.result.PositionResultDO;
import com.recruit.service.CompanyService;
import com.recruit.service.NotifyService;
import com.recruit.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.recruit.model.PositionDO;
import com.recruit.vo.CreatedVO;
import com.recruit.vo.DeletedVO;
import com.recruit.vo.PageResponseVO;
import com.recruit.vo.UpdatedVO;

import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import java.util.*;

/**
* 职位管理控制器
* 集成区块链上链、一致性校验功能
* @author Min / SuoMingFei
* @since 2022-04-02
*/
@RestController
@RequestMapping("/recruit/position")
@PermissionModule(value = "职位")
public class PositionController {

    @Autowired
    private PositionService positionService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private NotifyService notifyService;

    @Autowired(required = false)
    private ChainService chainService;

    /**
     * 添加职位（含区块链上链）
     * 发布职位后自动将核心信息哈希上链到长安链
     * @return
     */
    @Logger(template = "发布职位")
    @PostMapping("")
    @GroupRequired
    @PermissionMeta(value = "发布职位")
    public CreatedVO create(@RequestBody @Validated CreateOrUpdatePositionDTO validator) {
        // 使用带区块链上链的创建方法
        positionService.createPositionWithChain(validator);

        // 职位发布成功后，通知对应的观察者
        CompanyDO company = companyService.getById(validator.getCompanyId());
        if (company != null) {
            String foreignName = company.getForeignName();
            if (SingleUtil.map.containsKey(foreignName)) {
                SingleUtil.map.get(foreignName).setState(1);
                SingleUtil.map.get(foreignName).setPositionName(validator.getTitle());
                SingleUtil.map.get(foreignName).notifyObservers();
                Set<String> set = SingleUtil.messageMap.keySet();
                Iterator<String> iterator = set.iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    NotifyDO notifyDO = new NotifyDO();
                    notifyDO.setUserName(key);
                    notifyDO.setContent(SingleUtil.messageMap.get(key));
                    notifyService.create(notifyDO);
                }
                SingleUtil.messageMap.clear();
            }
        }
        return new CreatedVO(2100);
    }

    /**
     * 根据id更新职位信息
     * 更新后链上状态重置，需要重新上链
     */
    @Logger(template = "更新职位信息")
    @PutMapping("/{id}")
    @GroupRequired
    @PermissionMeta(value = "更新职位")
    public UpdatedVO updatePosition(@PathVariable @Positive(message = "{id.positive}") Integer id, @RequestBody @Validated CreateOrUpdatePositionDTO validator) {
        QueryWrapper<PositionDO> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);
        PositionDO positionDO = positionService.getOne(wrapper);
        if (positionDO == null) {
            throw  new NotFoundException(41000);
        }
        positionService.updatePosition(positionDO, validator);
        return new UpdatedVO(2200);
    }

    /**
     * 根据id更新公司状态state
     */
    @PutMapping("/state/{id}")
    @GroupRequired
    @PermissionMeta(value = "更新职位状态")
    public UpdatedVO update(@PathVariable @Positive(message = "{id.positive}") Integer id, @RequestParam Integer state) {
        QueryWrapper<PositionDO> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);
        PositionDO positionDO = positionService.getOne(wrapper);
        if (positionDO == null) {
            throw new NotFoundException(41000);
        }
        positionService.updateState(id, state);
        return new UpdatedVO(4000);
    }

    /**
     * 更新职位浏览量
     */
    @PutMapping("/hits/{id}")
    public UpdatedVO updateHits(@PathVariable @Positive(message = "{id.positive}") Integer id) {
        QueryWrapper<PositionDO> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);
        PositionDO positionDO = positionService.getOne(wrapper);
        if (positionDO == null) {
            throw new NotFoundException(41000);
        }
        positionService.updateHits(id);
        return new UpdatedVO(4100);
    }

    /**
     * 根据id删除职位（下架）
     */
    @Logger(template = "删除职位")
    @DeleteMapping("/{id}")
    @GroupRequired
    @PermissionMeta(value = "下架职位")
    public DeletedVO delete(@PathVariable @Positive(message = "{id.positive}") Integer id) {
        PositionDO positionDO = positionService.findById(id);
        if (positionDO == null) {
            throw new NotFoundException(41000);
        }
        positionService.removeById(id);
        // 职位下架成功后，通知对应的观察者
        CompanyDO company = companyService.getById(positionDO.getCompanyId());
        if (company != null) {
            String foreignName = company.getForeignName();
            if (SingleUtil.map.containsKey(foreignName)) {
                SingleUtil.map.get(foreignName).setState(0);
                SingleUtil.map.get(foreignName).setPositionName(positionDO.getTitle());
                SingleUtil.map.get(foreignName).notifyObservers();
                Set<String> set = SingleUtil.messageMap.keySet();
                Iterator<String> iterator = set.iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    NotifyDO notifyDO = new NotifyDO();
                    notifyDO.setUserName(key);
                    notifyDO.setContent(SingleUtil.messageMap.get(key));
                    notifyService.create(notifyDO);
                }
                SingleUtil.messageMap.clear();
            }
        }
        return new DeletedVO(2000);
    }

    /**
     * 根据职位id查询职位详细信息，包括该职位所属公司的基本信息
     */
    @GetMapping("/{id}")
    public PositionResultDO get(@PathVariable(value = "id") @Positive(message = "{id.positive}") Integer id) {
        PositionResultDO positionResultDO = positionService.getById(id);
        if (positionResultDO == null) {
            throw new NotFoundException(41000);
        }
        return positionResultDO;
    }

    /**
     * 根据职位浏览量进行排序，获取前十条数据作为热门职位推荐
     */
    @GetMapping("/sort")
    public List<PositionResultDO> sort() {
        List<PositionResultDO> positionResultDOS = positionService.sort();
        if (positionResultDOS == null || positionResultDOS.size() == 0) {
            throw new NotFoundException(41000);
        }
        return positionResultDOS;
    }

    /**
     * 根据职位的状态state分页查询职位信息
     */
    @GetMapping("/page/{state}")
    public PageResponseVO<PositionDO> page(
            @RequestParam(name = "count", required = false, defaultValue = "10")
            @Min(value = 1, message = "{page.count.min}")
            @Max(value = 30, message = "{page.count.max}") Integer count,
            @RequestParam(name = "page", required = false, defaultValue = "0")
            @Min(value = 0, message = "{page.number.min}") Integer page,
            @PathVariable(value = "state") Integer state
    ) {
        Page<PositionDO> pager = new Page<>(page, count);
        IPage<PositionDO> paging = positionService.getPage(pager, state);
        return PageUtil.build(paging);
    }

    /**
     * 根据hrID分页查询某个HR发布的所有职位信息
     */
    @GetMapping("/page/hr/{hrID}")
    @GroupRequired
    @PermissionMeta(value = "查看已发布的职位")
    public PageResponseVO<PositionDO> pageHR(
            @RequestParam(name = "count", required = false, defaultValue = "10")
            @Min(value = 1, message = "{page.count.min}")
            @Max(value = 30, message = "{page.count.max}") Integer count,
            @RequestParam(name = "page", required = false, defaultValue = "0")
            @Min(value = 0, message = "{page.number.min}") Integer page,
            @PathVariable(value = "hrID") Integer hrID
    ) {
        Page<PositionDO> pager = new Page<>(page, count);
        QueryWrapper<PositionDO> wrapper = new QueryWrapper<>();
        wrapper.eq("hr_id", hrID);
        IPage<PositionDO> paging = positionService.getBaseMapper().selectPage(pager, wrapper);
        return PageUtil.build(paging);
    }

    /**
     * 根据职位名称模糊查询职位列表
     */
    @GetMapping("/page/keyword")
    public PageResponseVO<PositionResultDO> pageKeyword(
            @RequestParam(name = "count", required = false, defaultValue = "10")
            @Min(value = 1, message = "{page.count.min}")
            @Max(value = 30, message = "{page.count.max}") Integer count,
            @RequestParam(name = "page", required = false, defaultValue = "0")
            @Min(value = 0, message = "{page.number.min}") Integer page,
            @RequestParam String keyword
    ) {
        Page<PositionResultDO> pager = new Page<>(page, count);
        IPage<PositionResultDO> paging = positionService.getPageByKeyword(pager, keyword);
        return PageUtil.build(paging);
    }

    // ========== 区块链相关接口 ==========

    /**
     * 校验职位数据的链上一致性
     * 对比本地数据哈希与链上存储哈希，验证数据是否被篡改
     *
     * @param id 职位ID
     * @return 校验结果（包含本地哈希、链上哈希、是否一致）
     */
    @GetMapping("/chain/verify/{id}")
    public ChainVerifyResult verifyChainConsistency(
            @PathVariable @Positive(message = "{id.positive}") Integer id) {
        return positionService.verifyChainConsistency(id);
    }

    /**
     * 手动触发职位数据重新上链
     * 用于上链失败后的重试场景
     *
     * @param id 职位ID
     * @return 更新结果
     */
    @Logger(template = "重新上链")
    @PutMapping("/chain/retry/{id}")
    @GroupRequired
    @PermissionMeta(value = "重新上链")
    public UpdatedVO retryChainUpload(@PathVariable @Positive(message = "{id.positive}") Integer id) {
        PositionDO positionDO = positionService.findById(id);
        if (positionDO == null) {
            throw new NotFoundException(41000);
        }

        if (chainService == null) {
            throw new NotFoundException(60000);
        }

        // 计算哈希并上链
        String dataHash = PositionHashUtil.computePositionHash(positionDO);
        String bizDataJson = PositionHashUtil.toJson(positionDO);

        try {
            chainService.uploadToChain(id, dataHash, bizDataJson);
            // 更新本地状态
            positionDO.setChainHash(dataHash);
            positionDO.setChainStatus(1);
            positionService.updateById(positionDO);
        } catch (Exception e) {
            positionDO.setChainStatus(2);
            positionService.updateById(positionDO);
            throw new NotFoundException(60001);
        }

        return new UpdatedVO(6000);
    }
}
