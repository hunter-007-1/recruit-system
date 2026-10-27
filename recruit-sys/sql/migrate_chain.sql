-- =============================================================
-- 智链诚聘 - 区块链招聘系统 数据库迁移脚本
-- 新增：链上交易记录表、职位表区块链字段、联合索引优化
-- 作者：SuoMingFei
-- 日期：2024-09-01
-- =============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- -----------------------------------------------------------
-- 1. 职位表新增区块链相关字段
-- -----------------------------------------------------------
ALTER TABLE `position` ADD COLUMN `chain_hash` varchar(128) DEFAULT NULL COMMENT '链上数据哈希（SHA-256）';
ALTER TABLE `position` ADD COLUMN `chain_tx_id` varchar(256) DEFAULT NULL COMMENT '链上交易ID（TxId）';
ALTER TABLE `position` ADD COLUMN `chain_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '上链状态：0-未上链 1-已上链 2-上链失败';

-- -----------------------------------------------------------
-- 2. 链上交易记录表 — 记录每次上链操作的完整信息
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `chain_record`;
CREATE TABLE `chain_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `biz_id` int(11) NOT NULL COMMENT '关联业务ID（职位ID）',
  `biz_type` varchar(32) NOT NULL COMMENT '业务类型：position/resume/application',
  `data_hash` varchar(128) NOT NULL COMMENT '业务数据SHA-256哈希值',
  `tx_id` varchar(256) DEFAULT NULL COMMENT '长安链交易ID',
  `block_height` bigint(20) DEFAULT NULL COMMENT '区块高度',
  `chain_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '上链状态：0-待上链 1-已上链 2-上链失败 3-校验不一致',
  `chain_time` datetime DEFAULT NULL COMMENT '上链确认时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `idempotent_key` varchar(128) NOT NULL COMMENT '幂等校验Key（防重复上链）',
  `chain_data` text COMMENT '链上原始数据JSON（用于一致性比对）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_biz_id_type` (`biz_id`, `biz_type`) USING BTREE COMMENT '业务ID+类型联合索引',
  KEY `idx_tx_id` (`tx_id`(191)) USING BTREE COMMENT '交易ID索引',
  KEY `idx_idempotent_key` (`idempotent_key`) USING BTREE COMMENT '幂等Key唯一索引',
  KEY `idx_chain_status` (`chain_status`) USING BTREE COMMENT '上链状态索引'
) ENGINE=InnoDB AUTO_INCREMENT=1 CHARACTER SET=utf8 COLLATE=utf8_general_ci COMMENT='链上交易记录表' ROW_FORMAT=Dynamic;

-- -----------------------------------------------------------
-- 3. 职位表联合索引优化 — 提升多维度查询性能
-- -----------------------------------------------------------

-- 城市+状态+删除时间：按城市筛选在招职位
CREATE INDEX idx_position_city_state_delete ON `position` (`city`, `state`, `delete_time`);

-- 分类+状态+删除时间：按分类筛选在招职位
CREATE INDEX idx_position_category_state ON `position` (`category_id`, `state`, `delete_time`);

-- HR+状态：HR查看自己发布的职位
CREATE INDEX idx_position_hr_state ON `position` (`hr_id`, `state`);

-- 公司+状态：按公司筛选在招职位
CREATE INDEX idx_position_company_state ON `position` (`company_id`, `state`);

-- 标题模糊查询索引（前缀索引）
CREATE INDEX idx_position_title ON `position` (`title`(50));

-- 浏览量排序索引（热门职位排序优化）
CREATE INDEX idx_position_state_hits ON `position` (`state`, `hits` DESC);

-- 上链状态索引：快速筛选未上链的职位
CREATE INDEX idx_position_chain_status ON `position` (`chain_status`);

-- -----------------------------------------------------------
-- 4. 申请表联合索引优化
-- -----------------------------------------------------------
CREATE INDEX idx_application_user_state ON `application` (`user_id`, `state`);
CREATE INDEX idx_application_hr_state ON `application` (`hr_id`, `state`);
CREATE INDEX idx_application_position ON `application` (`position_id`, `state`);

-- -----------------------------------------------------------
-- 5. 面试表索引
-- -----------------------------------------------------------
CREATE INDEX idx_interview_user ON `interview` (`user_id`, `status`);
CREATE INDEX idx_interview_hr ON `interview` (`hr_id`, `status`);

SET FOREIGN_KEY_CHECKS = 1;
