-- H2 兼容 schema（开发模式用）
-- 使用 MODE=MySQL 兼容大部分语法

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(24) NOT NULL,
  `nickname` VARCHAR(24) DEFAULT NULL,
  `avatar` VARCHAR(500) DEFAULT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `tel` VARCHAR(20) DEFAULT NULL,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `delete_time` TIMESTAMP DEFAULT NULL
);

-- 分组表
CREATE TABLE IF NOT EXISTS `group` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(60) NOT NULL,
  `info` VARCHAR(100) DEFAULT NULL,
  `level` SMALLINT DEFAULT 3,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `delete_time` TIMESTAMP DEFAULT NULL
);

-- 用户分组关联
CREATE TABLE IF NOT EXISTS `user_group` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `group_id` INT NOT NULL,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `delete_time` TIMESTAMP DEFAULT NULL
);

-- 权限表
CREATE TABLE IF NOT EXISTS `permission` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(60) NOT NULL,
  `module` VARCHAR(50) NOT NULL,
  `mount` TINYINT DEFAULT 1,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `delete_time` TIMESTAMP DEFAULT NULL
);

-- 分组权限关联
CREATE TABLE IF NOT EXISTS `group_permission` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `group_id` INT NOT NULL,
  `permission_id` INT NOT NULL,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `delete_time` TIMESTAMP DEFAULT NULL
);

-- 用户身份（密码登录）
CREATE TABLE IF NOT EXISTS `user_identity` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `identity_type` VARCHAR(100) NOT NULL,
  `identifier` VARCHAR(100) DEFAULT NULL,
  `credential` VARCHAR(255) DEFAULT NULL,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `delete_time` TIMESTAMP DEFAULT NULL
);

-- 公司表
CREATE TABLE IF NOT EXISTS `company` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(100) DEFAULT NULL,
  `foreign_name` VARCHAR(100) DEFAULT NULL,
  `city` VARCHAR(50) DEFAULT NULL,
  `logo` VARCHAR(500) DEFAULT NULL,
  `slogan` VARCHAR(200) DEFAULT NULL,
  `description` TEXT DEFAULT NULL,
  `state` INT DEFAULT 0,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `delete_time` TIMESTAMP DEFAULT NULL
);

-- 职位分类
CREATE TABLE IF NOT EXISTS `category` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(50) DEFAULT NULL,
  `description` VARCHAR(200) DEFAULT NULL,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `delete_time` TIMESTAMP DEFAULT NULL
);

-- 职位表（含区块链字段）
CREATE TABLE IF NOT EXISTS `position` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(50) DEFAULT NULL,
  `requirement` TEXT DEFAULT NULL,
  `quantity` INT DEFAULT NULL,
  `city` VARCHAR(50) DEFAULT NULL,
  `salary_up` INT DEFAULT NULL,
  `salary_down` INT DEFAULT NULL,
  `release_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `state` INT DEFAULT 0,
  `hits` INT DEFAULT 0,
  `category_id` INT NOT NULL,
  `hr_id` INT NOT NULL,
  `company_id` INT NOT NULL,
  `chain_hash` VARCHAR(128) DEFAULT NULL,
  `chain_tx_id` VARCHAR(256) DEFAULT NULL,
  `chain_status` TINYINT DEFAULT 0,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `delete_time` TIMESTAMP DEFAULT NULL
);

-- 简历表
CREATE TABLE IF NOT EXISTS `resume` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `ability` TEXT DEFAULT NULL,
  `personal_sum` TEXT DEFAULT NULL,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `delete_time` TIMESTAMP DEFAULT NULL
);

-- 申请表
CREATE TABLE IF NOT EXISTS `application` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `state` INT DEFAULT 0,
  `apply_time` TIMESTAMP DEFAULT NULL,
  `resume_id` INT NOT NULL,
  `position_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `hr_id` INT NOT NULL,
  `company_id` INT DEFAULT NULL,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `delete_time` TIMESTAMP DEFAULT NULL
);

-- 面试表
CREATE TABLE IF NOT EXISTS `interview` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `resume_id` INT DEFAULT NULL,
  `user_id` INT DEFAULT NULL,
  `hr_id` INT DEFAULT NULL,
  `company_id` INT DEFAULT NULL,
  `position_id` INT DEFAULT NULL,
  `status` INT DEFAULT 0,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `delete_time` TIMESTAMP DEFAULT NULL
);

-- 教育经历
CREATE TABLE IF NOT EXISTS `education` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `resume_id` INT DEFAULT NULL,
  `school` VARCHAR(50) DEFAULT NULL,
  `degree` VARCHAR(20) DEFAULT NULL,
  `major` VARCHAR(50) DEFAULT NULL,
  `start_date` DATE DEFAULT NULL,
  `end_date` DATE DEFAULT NULL,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 工作经历
CREATE TABLE IF NOT EXISTS `experience` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `resume_id` INT DEFAULT NULL,
  `company_name` VARCHAR(100) DEFAULT NULL,
  `position_name` VARCHAR(50) DEFAULT NULL,
  `start_date` DATE DEFAULT NULL,
  `end_date` DATE DEFAULT NULL,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 项目经历
CREATE TABLE IF NOT EXISTS `project` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `resume_id` INT DEFAULT NULL,
  `project_name` VARCHAR(100) DEFAULT NULL,
  `role` VARCHAR(50) DEFAULT NULL,
  `start_date` DATE DEFAULT NULL,
  `end_date` DATE DEFAULT NULL,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- HR公司关联
CREATE TABLE IF NOT EXISTS `hr_company` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `hr_id` INT NOT NULL,
  `company_id` INT NOT NULL,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `delete_time` TIMESTAMP DEFAULT NULL
);

-- 收藏表
CREATE TABLE IF NOT EXISTS `favor` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `position_id` INT NOT NULL,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `delete_time` TIMESTAMP DEFAULT NULL
);

-- 关注表
CREATE TABLE IF NOT EXISTS `follow` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `company_id` INT NOT NULL,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `delete_time` TIMESTAMP DEFAULT NULL
);

-- 文件表
CREATE TABLE IF NOT EXISTS `file` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `path` VARCHAR(500) NOT NULL,
  `type` VARCHAR(50) DEFAULT NULL,
  `name` VARCHAR(100) DEFAULT NULL,
  `size` INT DEFAULT NULL,
  `extension` VARCHAR(20) DEFAULT NULL,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `delete_time` TIMESTAMP DEFAULT NULL
);

-- 日志表
CREATE TABLE IF NOT EXISTS `log` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `message` TEXT DEFAULT NULL,
  `user_id` INT DEFAULT NULL,
  `username` VARCHAR(24) DEFAULT NULL,
  `status_code` INT DEFAULT NULL,
  `method` VARCHAR(10) DEFAULT NULL,
  `path` VARCHAR(500) DEFAULT NULL,
  `permission` VARCHAR(100) DEFAULT NULL,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `delete_time` TIMESTAMP DEFAULT NULL
);

-- 通知表
CREATE TABLE IF NOT EXISTS `notify` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_name` VARCHAR(50) DEFAULT NULL,
  `content` TEXT DEFAULT NULL,
  `is_read` TINYINT DEFAULT 0,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `delete_time` TIMESTAMP DEFAULT NULL
);

-- 链上交易记录表
CREATE TABLE IF NOT EXISTS `chain_record` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `biz_id` INT NOT NULL,
  `biz_type` VARCHAR(32) NOT NULL,
  `data_hash` VARCHAR(128) NOT NULL,
  `tx_id` VARCHAR(256) DEFAULT NULL,
  `block_height` BIGINT DEFAULT NULL,
  `chain_status` TINYINT DEFAULT 0,
  `chain_time` TIMESTAMP DEFAULT NULL,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `idempotent_key` VARCHAR(128) NOT NULL,
  `chain_data` TEXT DEFAULT NULL
);

-- ============================================================
-- 初始数据（来自原始 recruit.sql）
-- ============================================================

-- 分组
INSERT INTO `group` VALUES (1, 'root', '超级管理组', 1, '2021-03-14', '2021-03-14', NULL);
INSERT INTO `group` VALUES (2, 'guest', '游客组', 3, '2021-03-14', '2021-03-14', NULL);
INSERT INTO `group` VALUES (3, 'hr', 'HR管理组', 2, '2021-03-14', '2021-03-14', NULL);
INSERT INTO `group` VALUES (4, 'user', '求职者组', 2, '2021-03-14', '2021-03-14', NULL);

-- 用户（来自原始数据，admin=8）
INSERT INTO `user` VALUES (1, 'root', '演示管理员', NULL, NULL, NULL, '2021-03-14', '2022-04-26', NULL);
INSERT INTO `user` VALUES (4, 'user', '演示求职者A', NULL, NULL, NULL, '2021-04-16', '2022-03-25', NULL);
INSERT INTO `user` VALUES (5, 'user1', '演示求职者B', NULL, NULL, NULL, '2021-04-16', '2022-03-25', NULL);
INSERT INTO `user` VALUES (6, 'hr', '演示招聘专员A', NULL, NULL, NULL, '2021-04-16', '2022-04-26', NULL);
INSERT INTO `user` VALUES (7, 'hr1', '演示招聘专员B', NULL, NULL, NULL, '2021-04-16', '2022-04-26', NULL);
INSERT INTO `user` VALUES (8, 'admin', '平台演示管理员', NULL, NULL, NULL, '2021-04-16', '2022-04-26', NULL);
INSERT INTO `user` VALUES (9, 'user2', '演示求职者C', NULL, NULL, NULL, '2021-05-02', '2022-03-25', NULL);

-- 用户密码（pbkdf2sha256 格式，密码都是 123456）
INSERT INTO `user_identity` VALUES (1, 1, 'USERNAME_PASSWORD', 'root', 'pbkdf2sha256:64000:18:24:n:yUnDokcNRbwILZllmUOItIyo9MnI00QW:6ZcPf+sfzyoygOU8h/GSoirF', '2021-03-14', '2021-03-14', NULL);
INSERT INTO `user_identity` VALUES (4, 4, 'USERNAME_PASSWORD', 'user', 'pbkdf2sha256:64000:18:24:n:LM9r8Qohq6Cr9ZE8ylO1vGi8dHXvtx1M:l+dKkEY/iYe2Uj9T1o/drG7n', '2021-04-16', '2021-04-16', NULL);
INSERT INTO `user_identity` VALUES (5, 5, 'USERNAME_PASSWORD', 'user1', 'pbkdf2sha256:64000:18:24:n:itdSrGVHL8V5NNcbe4SuJokpev5zWe3r:tLVd6/oJFjokN9gfRQVS6hs4', '2021-04-16', '2021-04-16', NULL);
INSERT INTO `user_identity` VALUES (6, 6, 'USERNAME_PASSWORD', 'hr', 'pbkdf2sha256:64000:18:24:n:Nmgyvn43dsxnwkvoLS4KToSPOy1VatEv:5nVP0vb3pzuo5eMi2cLKujyC', '2021-04-16', '2021-04-16', NULL);
INSERT INTO `user_identity` VALUES (7, 7, 'USERNAME_PASSWORD', 'hr1', 'pbkdf2sha256:64000:18:24:n:DDjeVDVJnlJ8eiQsTukrAwRerugVj3UE:RloPJhjQ9OOCGRBiD80cHOGW', '2021-04-16', '2021-04-16', NULL);
INSERT INTO `user_identity` VALUES (8, 8, 'USERNAME_PASSWORD', 'admin', 'pbkdf2sha256:64000:18:24:n:Wx1cy1IJb7HpPY21BWpkaIwqIOphaBZF:gjRhsWs3a0VD/nAOgXDJbSia', '2021-04-16', '2021-04-16', NULL);
INSERT INTO `user_identity` VALUES (9, 9, 'USERNAME_PASSWORD', 'user2', 'pbkdf2sha256:64000:18:24:n:lpJSHOqQVBrcsWRri8THNW1DH769ITfe:RjG6xiH7OEbzAAhZOJWDsoXw', '2021-05-02', '2021-05-02', NULL);

-- 用户分组
INSERT INTO `user_group` VALUES (1, 1, 1, '2021-03-14', '2021-03-14', NULL);
INSERT INTO `user_group` VALUES (4, 4, 4, '2021-04-16', '2021-04-16', NULL);
INSERT INTO `user_group` VALUES (5, 5, 4, '2021-04-16', '2021-04-16', NULL);
INSERT INTO `user_group` VALUES (6, 6, 3, '2021-04-16', '2021-04-16', NULL);
INSERT INTO `user_group` VALUES (7, 7, 3, '2021-04-16', '2021-04-16', NULL);
INSERT INTO `user_group` VALUES (8, 8, 1, '2021-04-16', '2021-04-16', NULL);
INSERT INTO `user_group` VALUES (9, 9, 4, '2021-05-02', '2021-05-02', NULL);

-- 权限
INSERT INTO `permission` VALUES (1, '用户管理', '用户', 1, '2021-03-14', '2021-03-14', NULL);
INSERT INTO `permission` VALUES (2, '分配权限', '分组', 1, '2021-03-14', '2021-03-14', NULL);
INSERT INTO `permission` VALUES (3, '删除职位', '职位', 1, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `permission` VALUES (4, '下架职位', '职位', 1, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `permission` VALUES (5, '更新职位', '职位', 1, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `permission` VALUES (6, '更新职位状态', '职位', 1, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `permission` VALUES (7, '发布职位', '职位', 1, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `permission` VALUES (8, '查看已发布的职位', '职位', 1, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `permission` VALUES (9, '投递简历', '申请', 1, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `permission` VALUES (10, '申请审核', '申请', 1, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `permission` VALUES (11, '重新上链', '职位', 1, '2024-09-01', '2024-09-01', NULL);
INSERT INTO `permission` VALUES (12, '更新公司信息', '公司', 1, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `permission` VALUES (13, '更新公司状态', '公司', 1, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `permission` VALUES (14, '查看简历', '简历', 1, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `permission` VALUES (15, '创建简历', '简历', 1, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `permission` VALUES (16, '更新简历', '简历', 1, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `permission` VALUES (17, '删除简历', '简历', 1, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `permission` VALUES (18, '收藏职位', '收藏', 1, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `permission` VALUES (19, '取消收藏', '收藏', 1, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `permission` VALUES (20, '新增关注', '关注', 1, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `permission` VALUES (21, '取消关注', '关注', 1, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `permission` VALUES (22, '添加观察者', '观察者', 1, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `permission` VALUES (23, '删除观察者', '观察者', 1, '2021-05-01', '2021-05-01', NULL);

-- root分组拥有所有权限
INSERT INTO `group_permission` VALUES (1, 1, 1, '2021-03-14', '2021-03-14', NULL);
INSERT INTO `group_permission` VALUES (2, 1, 2, '2021-03-14', '2021-03-14', NULL);
INSERT INTO `group_permission` VALUES (3, 1, 3, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (4, 1, 4, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (5, 1, 5, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (6, 1, 6, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (7, 1, 7, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (8, 1, 8, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (9, 1, 9, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (10, 1, 10, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (11, 1, 11, '2024-09-01', '2024-09-01', NULL);
INSERT INTO `group_permission` VALUES (12, 1, 12, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (13, 1, 13, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (14, 1, 14, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (15, 1, 15, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (16, 1, 16, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (17, 1, 17, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (18, 1, 18, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (19, 1, 19, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (20, 1, 20, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (21, 1, 21, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (22, 1, 22, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (23, 1, 23, '2021-05-01', '2021-05-01', NULL);
-- hr分组权限
INSERT INTO `group_permission` VALUES (30, 3, 5, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (31, 3, 6, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (32, 3, 7, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (33, 3, 8, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (34, 3, 10, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (35, 3, 4, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (36, 3, 14, '2021-05-01', '2021-05-01', NULL);
-- user分组权限
INSERT INTO `group_permission` VALUES (40, 4, 9, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (41, 4, 15, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (42, 4, 16, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (43, 4, 17, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (44, 4, 18, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (45, 4, 19, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (46, 4, 20, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (47, 4, 21, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `group_permission` VALUES (48, 4, 14, '2021-05-01', '2021-05-01', NULL);

-- 公司（已审核 state=1 的）
INSERT INTO `company` VALUES (1, '阿里巴巴', 'Alibaba', '中国杭州1', NULL, '让天下没有难做的生意', '阿里巴巴集团经营多项业务', 1, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `company` VALUES (2, '腾讯', 'Tencent', '广东深圳', NULL, '通过互联网服务提升人类生活品质', '深圳市腾讯计算机系统有限公司', 1, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `company` VALUES (3, '百度', 'BaiDu', '广东深圳', NULL, '用科技让复杂的世界更简单', '百度是拥有强大互联网基础的领先AI公司', 1, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `company` VALUES (4, '字节跳动', 'ByteDance', '中国北京', NULL, '建设全球创作与交流平台', '北京字节跳动科技有限公司', 1, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `company` VALUES (5, '京东', 'JD', '中国北京', NULL, '网购上京东，省钱又放心', '京东中国自营式电商企业', 1, '2021-05-01', '2021-05-01', NULL);

-- 职位分类
INSERT INTO `category` VALUES (1, 'Java开发', '这个分类是Java开发', '2021-05-02', '2021-05-02', NULL);
INSERT INTO `category` VALUES (2, 'C++开发', '这个分类是C++开发', '2021-05-02', '2021-05-02', NULL);
INSERT INTO `category` VALUES (3, '前端开发', '这个分类是前端开发', '2021-05-02', '2021-05-02', NULL);
INSERT INTO `category` VALUES (4, '后端开发', '这个分类是后台开发', '2021-05-02', '2021-05-02', NULL);
INSERT INTO `category` VALUES (5, '数据挖掘', '这个分类是数据挖掘', '2021-05-02', '2021-05-02', NULL);
INSERT INTO `category` VALUES (6, '大数据开发', '这个分类是大数据开发', '2021-05-02', '2021-05-02', NULL);
INSERT INTO `category` VALUES (7, '测试开发', '这个分类是测试开发', '2021-05-02', '2021-05-02', NULL);
INSERT INTO `category` VALUES (8, '区块链开发', '区块链开发工程师', '2024-09-01', '2024-09-01', NULL);

-- HR公司关联（hr=6对应阿里巴巴，hr1=7对应腾讯）
INSERT INTO `hr_company` VALUES (1, 6, 1, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `hr_company` VALUES (2, 7, 2, '2021-05-01', '2021-05-01', NULL);

-- 职位数据（含区块链状态）
INSERT INTO `position` VALUES (1, 'Java工程师', '熟练使用RPC框架，具备相关的分布式开发经验', 3, '北京市', 12000, 7000, '2021-04-01', 1, 335, 1, 6, 1, NULL, NULL, 0, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `position` VALUES (2, 'Java工程师', '接收应届实习生，实习期满应聘上岗', 5, '上海市', 16000, 11000, '2021-04-01', 1, 55, 1, 6, 1, NULL, NULL, 0, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `position` VALUES (3, 'Java工程师', '有扎实的java基础，熟悉分布式、缓存、异步消息等原理和应用', 15, '天津市', 13000, 10000, '2021-04-03', 1, 187, 4, 7, 2, NULL, NULL, 0, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `position` VALUES (4, 'Java工程师', 'JAVA WEB方向2年+经验', 2, '南京市', 16000, 12000, '2021-04-03', 1, 191, 4, 7, 2, NULL, NULL, 0, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `position` VALUES (5, 'C++工程师', '可接收计算机相关专业应届生，表现优秀者加薪转正', 30, '南京市', 8000, 5000, '2021-04-04', 1, 13, 2, 7, 2, NULL, NULL, 0, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `position` VALUES (6, '区块链开发工程师', '熟悉长安链/以太坊，Go或Java开发经验', 2, '上海市', 25000, 15000, '2024-09-01', 0, 0, 8, 6, 1, NULL, NULL, 0, '2024-09-01', '2024-09-01', NULL);
INSERT INTO `position` VALUES (7, '大数据工程师', '熟悉Hadoop/Spark生态，有数据仓库建设经验', 5, '杭州市', 20000, 12000, '2021-04-05', 1, 88, 6, 4, 4, NULL, NULL, 0, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `position` VALUES (8, '前端开发工程师', 'Vue3/React，有大型项目经验优先', 5, '深圳市', 18000, 10000, '2021-04-06', 1, 200, 3, 7, 2, NULL, NULL, 0, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `position` VALUES (9, '数据挖掘工程师', '机器学习/深度学习，有推荐系统经验优先', 3, '北京市', 30000, 18000, '2021-04-07', 1, 66, 5, 6, 1, NULL, NULL, 0, '2021-05-01', '2021-05-01', NULL);
INSERT INTO `position` VALUES (10, '测试工程师', '熟悉自动化测试框架，有CI/CD经验', 4, '广州市', 15000, 9000, '2021-04-08', 1, 45, 7, 7, 2, NULL, NULL, 0, '2021-05-01', '2021-05-01', NULL);

-- 简历
INSERT INTO `resume` VALUES (1, 4, 'Java/Spring/MyBatis', '热爱编程，有2年Java开发经验', '2021-04-16', '2021-04-16', NULL);
INSERT INTO `resume` VALUES (2, 5, 'Python/数据分析', '应届毕业生，学习能力强', '2021-04-16', '2021-04-16', NULL);
INSERT INTO `resume` VALUES (10, 9, '前端开发/Vue/React', '有1年前端开发经验', '2021-05-02', '2021-05-02', NULL);
