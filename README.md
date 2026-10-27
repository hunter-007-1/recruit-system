# 智链诚聘

基于 Spring Boot、Vue 2 和 MySQL 的招聘系统，包含职位、企业、简历、投递、面试与管理功能，并集成长安链职位数据存证、校验和重试。

## 项目结构

```text
recruit-system/
├── src/                         # Vue 前端源码
├── public/                      # 前端静态资源和 HTML 模板
├── package.json                 # 前端依赖与命令
├── package-lock.json            # npm 锁定文件
├── yarn.lock                    # Yarn 锁定文件
├── recruit-sys/                 # Spring Boot 后端
│   ├── pom.xml                  # Maven 依赖
│   ├── src/main/java/           # Java 源码及 RecruitApplication 入口
│   ├── src/main/resources/      # 配置、Mapper、开发库结构
│   └── sql/                     # MySQL 初始化和区块链迁移脚本
└── README.md
```

## 环境要求

- JDK 8 或更高版本（项目目标字节码为 Java 8；当前已用 JDK 17 验证后端编译）
- Maven 3.6 或更高版本
- Node.js 18.12 或更高版本及 npm 8 或更高版本（锁文件为 npm lockfile v3）
- 开发模式使用项目内 H2 内存数据库；生产模式使用 MySQL 8
- 区块链功能需要另行部署长安链节点和合约；开发配置默认关闭链连接

## 本地运行

### 启动后端

在项目根目录执行：

```powershell
cd recruit-sys
mvn spring-boot:run "-Dspring-boot.run.profiles=dev" "-Dspring-boot.run.jvmArguments=-Dfile.encoding=UTF-8"
```

后端默认监听 `http://127.0.0.1:5000`。开发配置使用 H2 内存数据库，首次启动时从 `src/main/resources/db/schema.sql` 初始化，不要求先安装 MySQL。

开发环境演示账号：`root` / `123456`。该账号只供本地开发数据库使用；部署生产环境前应移除演示账户并设置独立密钥。

### 启动前端

另开终端，在项目根目录执行：

```powershell
npm ci --legacy-peer-deps
npm run serve
```

前端默认监听 `http://127.0.0.1:8080`，开发 API 地址配置在 `.env.development`，指向本机后端 `http://127.0.0.1:5000/`。

项目使用 Vue CLI 3 的旧依赖树，安装时需要 `--legacy-peer-deps` 兼容其 ESLint peer dependency 版本范围。

## 数据库与生产配置

- MySQL 初始化脚本：`recruit-sys/sql/recruit.sql`
- 已有数据库升级区块链字段脚本：`recruit-sys/sql/migrate_chain.sql`
- 后端默认激活 `dev` 配置；MySQL 配置位于 `application-test.yml` 和 `application-prod.yml`，数据库连接参数通过 `DB_HOST`、`DB_USERNAME`、`DB_PASSWORD` 提供。
- 长安链配置通过 `CHAIN_NODE_ADDR`、`CHAIN_ID`、`CHAIN_ORG_ID`、`CHAIN_USER_CERT`、`CHAIN_USER_KEY`、`CHAIN_TLS_CERT` 提供。不要把真实密码、证书或私钥提交到代码仓库。
- `application.yml` 中令牌密钥可通过 `LIN_TOKEN_SECRET` 设置；生产部署时应提供独立的随机值。
- 前端生产 API 地址在 `.env.production` 中配置；当前为占位地址，部署前替换成实际后端地址。

## 上传到 GitHub 前

提交前检查配置和历史文件，确保没有真实数据库密码、令牌密钥、链节点证书、私钥、个人数据或生产日志。`.gitignore` 已排除依赖目录、IDE 文件、日志、Maven/npm 构建输出和本地运行临时文件。

该仓库当前包含源码和依赖锁文件，不包含 `node_modules` 或 Maven 构建输出；在新电脑上按上述步骤安装依赖即可开始运行。
