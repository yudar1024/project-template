# 模块详细展开（Part 4/4）：工程化要求 & 验收标准

> 贯穿示例项目：**task-flow** — 面向小型团队的任务管理系统

---

## 模块七：工程化要求

### 1. 模块目标

工程化要求解决"**代码之外的一切**"——版本控制、配置管理、CI/CD、文档。没有工程化要求，Agent 产出的代码可能"能跑"但"不可维护"：敏感信息硬编码在源码里、没有 README、不同环境的配置混在一起。

### 2. 核心理念

1. **一条命令原则**：启动、测试、构建各只需一条命令
2. **环境无关原则**：代码不包含任何环境特定信息，全部通过配置注入
3. **文档即入口**：README 是新成员了解项目的唯一入口，必须完整可执行

### 3. 字段详解

#### 3.1 版本控制

**Commit 规范 — Conventional Commits**：

```
格式：<type>(<scope>): <subject>

type 必须是以下之一：
  feat     — 新功能
  fix      — 修复 BUG
  docs     — 文档更新
  style    — 格式调整（不影响逻辑）
  refactor — 重构（不新增功能、不修复BUG）
  test     — 添加/修改测试
  chore    — 构建、依赖、配置等维护工作

scope 为可选的影响范围：auth / task / project / common

subject 规则：
  - 动词开头（添加/修复/更新/移除）
  - 不超过 50 字符
  - 不加句号
```

**✅ 优秀的 Commit 消息**：
```
feat(auth): 添加用户注册功能，支持邮箱验证
fix(task): 修复拖拽任务后排序错乱的问题
refactor(common): 提取统一分页查询逻辑到 BaseService
test(project): 补充项目成员权限控制测试
docs: 更新 README 快速启动步骤
```

**❌ 不良的 Commit 消息**：
```
update
fix bug
修改了一些东西
WIP
。
```

**.gitignore 必须包含**：

```gitignore
# IDE
.idea/
.vscode/
*.iml

# 构建产物
build/
dist/
target/
node_modules/

# 环境配置（含敏感信息）
.env
.env.local
application-local.yml

# 操作系统
.DS_Store
Thumbs.db

# 日志
*.log
logs/
```

#### 3.2 配置管理

**环境分离的正确做法**：

```
application.yml          ← 公共配置（所有环境共享）
application-dev.yml      ← 开发环境（本地数据库、调试级别日志）
application-prod.yml     ← 生产环境（敏感信息从环境变量读取）
application-test.yml     ← 测试环境（H2内存数据库）
```

**公共配置示例**：
```yaml
# application.yml — 不含任何敏感信息
spring:
  application:
    name: task-flow
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: Asia/Shanghai
    default-property-inclusion: non_null

task-flow:
  jwt:
    expiration: 86400000  # 24小时（毫秒）
  security:
    max-login-attempts: 5
    lock-duration-minutes: 30
  task:
    max-tags-per-task: 5
```

**开发环境配置**：
```yaml
# application-dev.yml — 可含本地开发用的非敏感配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/taskflow_dev?useSSL=false
    username: root
    password: root123  # 本地开发数据库，非敏感
  jpa:
    show-sql: true
logging:
  level:
    com.taskflow: DEBUG
```

**生产环境配置**：
```yaml
# application-prod.yml — 所有敏感信息从环境变量读取
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

task-flow:
  jwt:
    secret: ${JWT_SECRET}

logging:
  level:
    com.taskflow: INFO
```

> **绝对禁止**在代码或非 dev 配置文件中硬编码：数据库生产密码、JWT Secret、API Key、第三方服务凭证、加密密钥

#### 3.3 文档要求

**README.md 必须包含的章节**：

| 章节 | 内容 | 验证标准 |
|------|------|---------|
| 项目简介 | 一段话说明项目用途 | 新人5秒内理解 |
| 技术栈 | 列出核心技术和版本 | 能判断环境兼容性 |
| 前置条件 | 需要安装的软件及版本 | 照做可以搭建环境 |
| 快速启动 | 从克隆到运行的完整步骤 | 照做可以跑起来 |
| 项目结构 | 目录树 + 说明 | 知道代码在哪里 |
| API 文档 | Swagger 地址或文档链接 | 前端可以对接 |
| 测试 | 如何运行测试 | 一条命令跑通 |

**API 文档**：

```
后端使用 SpringDoc OpenAPI 自动生成，启动后访问：
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

要求：
- 每个 Controller 方法有 @Operation(summary = "xxx") 注解
- 每个 DTO 字段有 @Schema(description = "xxx") 注解
- 按模块分组（@Tag）
```

#### 3.4 数据库初始化

```
方案选择：
- 小型项目：SQL 脚本（src/main/resources/db/migration/V1__init.sql）
- 中型项目：Flyway 或手动脚本
- 大型项目：Flyway / Liquibase

task-flow 选择手动 SQL 脚本（中型项目、简化 MVP）

V1__init.sql 应包含：
1. 建表语句（含索引和外键）
2. 初始数据（如默认管理员账号）
3. 字符集和排序规则声明
```

### 4. 完整示例（task-flow）

```markdown
## 七、工程化要求

### 7.1 版本控制

- Commit 规范：Conventional Commits
  - 格式：<type>(<scope>): <subject>
  - type: feat/fix/docs/style/refactor/test/chore
  - scope: auth/project/task/common
  - subject: 动词开头，≤50字符，不加句号
- .gitignore：.idea/, build/, dist/, node_modules/, .env, *.log, application-local.yml

### 7.2 配置管理

- 环境分离：
  - application.yml（公共）
  - application-dev.yml（开发，含本地数据库配置）
  - application-test.yml（测试，使用 H2 内存数据库）
  - application-prod.yml（生产，敏感信息从 ${ENV_VAR} 读取）
- 业务配置统一前缀 task-flow.*，使用 @ConfigurationProperties 绑定
- 禁止硬编码：数据库密码、JWT Secret、API Key

### 7.3 文档要求

- README.md 必须包含：项目简介、技术栈、前置条件、快速启动（后端+前端）、
  项目结构、API 文档地址、运行测试命令
- 后端 API 文档：SpringDoc OpenAPI（Swagger UI）
  - 启动后访问 http://localhost:8080/swagger-ui.html
  - 每个接口必须有 @Operation 注解
  - 按模块分组（auth / project / task）

### 7.4 数据库初始化

- 初始化脚本：src/main/resources/db/migration/V1__init.sql
- 包含：所有建表语句（含索引）、字符集 utf8mb4、初始管理员数据
- README 中说明初始化步骤

### 7.5 容器化（可选，MVP 不要求）

暂不实现，后续迭代考虑 Docker + docker-compose
```

---

## 模块八：验收标准

### 1. 模块目标

验收标准是 Agent 的"**完成定义**"（Definition of Done）。它告诉 Agent 什么时候算"做完了"，并要求 Agent 自己证明做完了。没有验收标准，Agent 倾向于"写完代码就停"，不检查是否能编译、是否测试通过。

### 2. 核心理念

1. **自证清白**：Agent 不仅要完成开发，还要自行运行验证并提供证据
2. **检查清单化**：每条验收标准必须可以打勾（是/否），不能模糊
3. **自检指令是关键**：在输入末尾放一段明确的自检指令，Agent 会严格执行

### 3. 字段详解

#### 3.1 三类验收

**功能验收** — Agent 对照需求逐一验证：
```
标准：逐个功能核对验收条件
证据：Agent 输出每个功能的验证结果表格
```

**质量验收** — Agent 运行工具并报告：
```
标准：lint零违规、测试全通过、覆盖率达标
证据：运行命令输出截图/日志
```

**工程验收** — Agent 模拟新人操作：
```
标准：按 README 步骤从零启动成功
证据：启动命令的输出日志
```

#### 3.2 Agent 自检指令

**这是整个输入中投入产出比最高的部分**。一段好的自检指令可以显著提升 Agent 产出的完成度：

```markdown
## 交付自检（Agent 必须在完成所有开发后执行）

请按以下步骤逐一执行，并以表格形式输出自检报告：

### 步骤1：编译构建
运行后端构建命令和前端构建命令，确认零错误：
- 后端：`./gradlew build -x test`
- 前端：`pnpm build`

### 步骤2：代码质量
- 后端：`./gradlew checkstyleMain`
- 前端：`pnpm lint`
确认零 error。列出 warning（如有）。

### 步骤3：测试
- 后端：`./gradlew test`
- 前端：`pnpm test`
报告：总用例数、通过数、失败数、覆盖率。

### 步骤4：功能核对
逐个核对 F-001 到 F-006 的每条验收条件，输出表格：

| 功能编号 | 验收条件 | 通过? | 备注 |
|---------|---------|------|------|
| F-001.1 | 邮箱格式校验 | ✅/❌ | ... |
| ... | ... | ... | ... |

### 步骤5：安全检查
在代码中搜索以下模式，确认无硬编码敏感信息：
- password = "（非测试文件）
- secret = "
- apiKey = "
- token = "（非 JWT 工具类）

### 步骤6：文档检查
- [ ] README.md 存在且包含所有必须章节
- [ ] API 接口都有 @Operation 注解
- [ ] 数据库初始化脚本存在且可执行

### 步骤7：输出自检报告
以以下格式输出汇总：

| 检查项 | 状态 | 详情 |
|--------|------|------|
| 后端构建 | ✅/❌ | ... |
| 前端构建 | ✅/❌ | ... |
| 后端 Lint | ✅/❌ | ... |
| 前端 Lint | ✅/❌ | ... |
| 后端测试 | ✅/❌ | xx/xx 通过，覆盖率 xx% |
| 前端测试 | ✅/❌ | xx/xx 通过 |
| 功能验收 | ✅/❌ | xx/xx 条通过 |
| 安全检查 | ✅/❌ | ... |
| 文档检查 | ✅/❌ | ... |
```

#### 3.3 交付物清单

明确列出 Agent 需要交付的所有文件/产物：

```
必须交付：
□ 后端源码（src/main/java/**）
□ 后端测试代码（src/test/java/**）
□ 后端配置文件（application*.yml）
□ 数据库初始化脚本（V1__init.sql）
□ 构建配置（build.gradle.kts、settings.gradle.kts）
□ 前端源码（src/**）
□ 前端配置（package.json、tsconfig.json、vite.config.ts、.eslintrc.*、.prettierrc）
□ README.md
□ .gitignore
□ 自检报告

可选交付：
□ Dockerfile
□ docker-compose.yml
□ CHANGELOG.md
□ API 文档导出文件
```

### 4. 完整示例（task-flow）

```markdown
## 八、验收标准

### 8.1 功能验收

- [ ] F-001 用户注册：所有 4 条验收条件通过
- [ ] F-002 用户登录：所有 4 条验收条件通过
- [ ] F-003 项目创建：所有 4 条验收条件通过
- [ ] F-004 任务看板：所有 4 条验收条件通过
- [ ] F-005 任务管理：所有 4 条验收条件通过
- [ ] F-006 任务删除：所有 3 条验收条件通过

### 8.2 质量验收

- [ ] 后端 `./gradlew checkstyleMain` 零违规
- [ ] 前端 `pnpm lint` 零 error
- [ ] 后端 `./gradlew test` 全部通过，Service 层覆盖率 ≥ 80%
- [ ] 前端 `pnpm test` 全部通过
- [ ] 代码中无硬编码密码/密钥（测试 fixture 除外）
- [ ] 无 TODO/FIXME/HACK 残留（或明确记录原因）

### 8.3 工程验收

- [ ] 后端可通过 `./gradlew bootRun` 启动，无报错
- [ ] 前端可通过 `pnpm dev` 启动，无报错
- [ ] 后端测试：`./gradlew test`
- [ ] 前端测试：`pnpm test`
- [ ] 前端构建：`pnpm build` 无报错
- [ ] README.md 包含：项目简介、技术栈、前置条件、快速启动、目录结构、API 文档
- [ ] .gitignore 正确配置
- [ ] 数据库初始化脚本（V1__init.sql）存在且语法正确

### 8.4 交付物清单

- [ ] 后端源码 + 测试代码
- [ ] 前端源码 + 测试代码
- [ ] 后端配置文件（application.yml / dev / test）
- [ ] 前端配置文件（package.json / tsconfig / vite.config / eslint / prettier）
- [ ] 数据库初始化脚本
- [ ] 构建文件（build.gradle.kts / settings.gradle.kts）
- [ ] README.md
- [ ] .gitignore
- [ ] 自检报告（表格形式）

### 8.5 交付自检（Agent 必须执行）

完成所有开发后，依次执行：
1. `./gradlew build -x test` → 确认编译通过
2. `./gradlew checkstyleMain` → 确认代码规范
3. `./gradlew test` → 报告通过率和覆盖率
4. `pnpm build` → 确认前端构建通过
5. `pnpm lint` → 确认前端代码规范
6. `pnpm test` → 报告通过率
7. 逐一核对 F-001~F-006 验收条件
8. 搜索代码中的硬编码敏感信息
9. 检查 README.md 完整性
10. 以表格形式输出完整自检报告
```
