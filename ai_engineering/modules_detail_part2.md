# 模块详细展开（Part 2/4）：技术栈锁定 & 架构设计约束

> 贯穿示例项目：**task-flow** — 面向小型团队的任务管理系统

---

## 模块三：技术栈锁定

### 1. 模块目标

技术栈锁定解决一个核心问题：**防止 Agent 自行选择技术**。Agent 对各种技术都有"了解"，但它不知道你的团队熟悉什么、公司基础设施支持什么。不锁定技术栈，Agent 可能在同一个项目中混用 Axios 和 Fetch，或者选择团队从未接触过的框架。

### 2. 核心理念

1. **版本号精确到大版本**：写 `Spring Boot 3.2.x` 而不是 `Spring Boot`，因为 2.x 和 3.x 的 API 差异巨大
2. **配套生态一起锁定**：锁定 React 时必须一起锁定状态管理、路由、HTTP客户端等
3. **说"不用什么"比"用什么"更重要**：禁止清单防止 Agent 引入团队无法维护的技术

### 3. 字段详解

#### 3.1 语言与运行时

**为什么要锁定到大版本号？**

| 场景 | 如果不锁定会怎样 |
|------|----------------|
| Java | Agent 可能用 Java 8 的 Date 而非 Java 21 的 LocalDateTime |
| Python | Agent 可能不使用 match-case（3.10+）或类型注解的新语法 |
| TypeScript | Agent 可能不使用 satisfies 运算符（5.0+）或 const 类型参数 |
| Node.js | Agent 可能使用 CommonJS 而非 ES Modules |

**✅ 正确写法**：
```
- 编程语言：Java 21（使用虚拟线程、Record、Pattern Matching 等新特性）
- 运行时：JVM 21 (LTS)
- 包管理：Gradle 8.x (Kotlin DSL)
```

**❌ 错误写法**：
```
- 编程语言：Java
- 包管理：Maven 或 Gradle 都行
```

#### 3.2 框架与核心库

**选型表应包含四列**：

| 列 | 作用 | 示例 |
|----|------|------|
| 类别 | 技术用途分类 | Web 框架、ORM、测试框架 |
| 技术选型 | 具体技术名称 | Spring Boot、MyBatis-Plus |
| 版本约束 | 精确到大版本号 | 3.2.x、3.5.x |
| 选型理由 | 为什么选这个 | 团队熟悉 / 性能优势 / 生态丰富 |

**选型理由为什么重要？**

当 Agent 需要在子技术上做选择时（比如选择序列化库），它会参考选型理由来推断你的倾向：
- 理由是"轻量简洁" → Agent 倾向选择更轻量的方案
- 理由是"团队熟悉" → Agent 倾向选择更主流的方案
- 理由是"性能优先" → Agent 倾向选择高性能方案

#### 3.3 禁止使用清单

**最高性价比的输入字段**。以下是常见禁止清单条目：

**Java 项目常见禁止项**：
```
- 禁止：Lombok @Data，原因：隐式生成 equals/hashCode 可能导致 ORM 问题，用 @Getter @Setter 替代
- 禁止：System.out.println，原因：使用 SLF4J Logger
- 禁止：java.util.Date，原因：使用 java.time API
- 禁止：Apache Commons Lang（仅字符串工具），原因：Java 11+ 原生替代
- 禁止：手动拼接 SQL，原因：使用 ORM 或 prepared statement
- 禁止：Field 注入（@Autowired 字段），原因：使用构造函数注入
```

**TypeScript 项目常见禁止项**：
```
- 禁止：any 类型，原因：破坏类型安全，使用 unknown + 类型守卫
- 禁止：var 声明，原因：使用 const / let
- 禁止：enum（运行时枚举），原因：使用 as const + type 联合类型
- 禁止：Moment.js，原因：已停止维护、包体积大，使用 Day.js
- 禁止：直接操作 DOM，原因：通过 React 状态驱动 UI
- 禁止：index.ts 桶文件（barrel exports），原因：影响 tree-shaking
```

**Python 项目常见禁止项**：
```
- 禁止：requests（同步 HTTP），原因：使用 httpx 支持异步
- 禁止：print 调试，原因：使用 logging 模块
- 禁止：mutable 默认参数，原因：Python 经典陷阱
- 禁止：*（星号导入），原因：污染命名空间
- 禁止：裸 except，原因：必须指定异常类型
```

#### 3.4 开发工具链

工具链锁定确保生成的代码能通过你的质量检查门禁：

```
如果你指定了 Prettier 配置：
  → Agent 会按照配置生成代码
  → 生成的代码可以直接通过 CI 的格式检查

如果没有指定：
  → Agent 可能用 2 空格缩进，你团队用 4 空格
  → 提交后 CI 格式检查爆红一片
```

### 4. 完整示例（task-flow）

```markdown
## 三、技术栈锁定

### 3.1 语言与运行时

- **后端语言**：Java 21（启用虚拟线程、Record类、sealed class 等特性）
- **前端语言**：TypeScript 5.x（严格模式：strict: true、noUncheckedIndexedAccess: true）
- **后端运行时**：Spring Boot 3.2.x（内嵌 Tomcat）
- **前端运行时**：Node.js 20 LTS
- **后端包管理**：Gradle 8.x（Kotlin DSL，即 build.gradle.kts）
- **前端包管理**：pnpm 8.x

### 3.2 框架与核心库

| 类别 | 技术选型 | 版本约束 | 选型理由 |
|------|---------|---------|---------|
| 后端框架 | Spring Boot | 3.2.x | 团队技术栈、生态成熟 |
| 安全框架 | Spring Security | 6.2.x | Spring 原生集成 |
| JWT | jjwt (io.jsonwebtoken) | 0.12.x | 轻量、主流 |
| ORM | MyBatis-Plus | 3.5.x | 团队熟悉、代码生成便捷 |
| 数据库 | MySQL | 8.0 | 公司基础设施 |
| 数据库连接池 | HikariCP | 默认Spring Boot版 | 高性能、Spring Boot 默认 |
| 参数校验 | Jakarta Validation | 3.x | 声明式校验，减少样板代码 |
| API 文档 | SpringDoc OpenAPI | 2.3.x | 自动生成 Swagger UI |
| 日志 | SLF4J + Logback | Spring Boot 默认版 | 标准日志门面 |
| 前端框架 | React | 18.x | 团队技术栈 |
| 前端构建 | Vite | 5.x | 极速 HMR、开箱即用 |
| 路由 | React Router | 6.x | React 官方推荐 |
| 状态管理 | Zustand | 4.x | 轻量、无模板代码 |
| HTTP 客户端 | Axios | 1.x | 拦截器、请求取消、成熟生态 |
| UI 组件库 | Ant Design | 5.x | 企业级、组件丰富 |
| 拖拽 | @dnd-kit/core | 6.x | 现代拖拽库、React友好 |
| 日期处理 | Day.js | 1.x | 轻量、API兼容Moment |

### 3.3 开发工具链

- **后端格式化**：Spotless 插件（Google Java Format）
- **后端代码检查**：Checkstyle（Google 风格配置）
- **前端格式化**：Prettier（配置文件见下方）
- **前端 Lint**：ESLint + typescript-eslint（strict 配置）
- **Git Hooks**：无（简化 MVP，后续迭代补充）

**Prettier 配置（.prettierrc）**：
```json
{
  "semi": true,
  "singleQuote": true,
  "tabWidth": 2,
  "trailingComma": "all",
  "printWidth": 100,
  "arrowParens": "always"
}
```

### 3.4 禁止使用清单

**后端**：
- 禁止：Lombok @Data → 用 @Getter @Setter，原因：避免隐式equals/hashCode
- 禁止：Lombok @Builder → 用静态工厂方法或构造函数，原因：Builder隐藏必填字段
- 禁止：@Autowired 字段注入 → 用构造函数注入，原因：不可测试
- 禁止：System.out.println → 用 log.info/debug/error，原因：生产日志管理
- 禁止：java.util.Date → 用 java.time.LocalDateTime，原因：线程安全+API清晰
- 禁止：手动拼接SQL字符串，原因：SQL注入风险
- 禁止：在Controller中写业务逻辑，原因：职责分离

**前端**：
- 禁止：any 类型 → 用 unknown + 类型守卫，原因：类型安全
- 禁止：var → 用 const / let，原因：避免变量提升问题
- 禁止：enum → 用 as const 对象，原因：tree-shaking友好
- 禁止：直接操作DOM → 通过React状态驱动，原因：React范式
- 禁止：class组件 → 用函数组件+Hooks，原因：现代React标准
- 禁止：Moment.js → 用 Day.js，原因：包体积
- 禁止：console.log 调试信息残留，原因：生产环境干净
```

---

## 模块四：架构设计约束

### 1. 模块目标

架构约束回答"**代码往哪里放**"和"**谁能调用谁**"。没有架构约束，Agent 可能把业务逻辑写在 Controller 里，或者让 Repository 直接调用另一个 Service——这些在短期可以"跑起来"，但长期维护是灾难。

### 2. 核心理念

1. **目录结构即架构**：目录名和层级就是对开发者最直观的架构约束
2. **依赖方向必须单向**：上层→下层，禁止反向和跨层调用
3. **约束越明确，Agent 犯错越少**：写清楚"什么不能做"比"什么要做"更有效

### 3. 字段详解

#### 3.1 架构风格选型

| 风格 | 适用场景 | 核心约束 | 示例项目规模 |
|------|---------|---------|-----------|
| **简单分层** | 单一业务域、CRUD为主 | Controller→Service→Repository | 小型/中型 |
| **功能分包** | 多业务域、模块独立性强 | 按功能模块分包，模块内分层 | 中型/大型 |
| **六边形架构** | 外部依赖多、需要替换实现 | 端口+适配器，核心不依赖外部 | 中型/大型 |
| **DDD 分层** | 复杂业务逻辑、领域模型丰富 | Application→Domain→Infrastructure | 大型 |
| **微服务** | 独立部署、团队分治 | 服务间 API 通信、独立数据库 | 大型 |

**如何选择？** 

```
问自己三个问题：
1. 有几个独立的业务域？ → 1个用简单分层，2-5个用功能分包，5+考虑微服务
2. 业务逻辑复杂吗？ → CRUD 为主用简单分层，复杂业务用 DDD
3. 外部依赖多吗？ → 需要频繁替换外部服务用六边形架构
```

#### 3.2 目录结构

**描述规范**：不要只列目录名，要加注释说明用途

```
✅ 正确：
├── service/           # 业务逻辑层，编排核心逻辑和事务管理
│   ├── impl/          # Service接口的实现类
│   └── converter/     # Entity ↔ DTO 转换器

❌ 错误：
├── service/
│   ├── impl/
│   └── converter/
```

#### 3.3 分层职责矩阵

**最关键的约束字段**。明确每一层"做什么"和"不做什么"：

| 层级 | 必须做 | 禁止做 |
|------|-------|-------|
| Controller | 参数校验、DTO↔VO转换、调用Service | 写SQL、事务管理、业务逻辑判断 |
| Service | 业务逻辑编排、事务管理、调用Repository | 感知HTTP概念（HttpServletRequest等） |
| Repository | 数据库CRUD、自定义SQL查询 | 业务逻辑判断、调用其他Service |
| Model | 数据结构定义 | 包含任何业务方法 |

**常见违规示例**：

```java
// ❌ Controller 中写业务逻辑
@PostMapping("/tasks")
public ApiResponse<TaskVO> createTask(@RequestBody TaskCreateDTO dto) {
    // 不应该在 Controller 中做权限判断
    if (!projectMemberRepository.isMember(dto.getProjectId(), currentUserId)) {
        throw new ForbiddenException("无权限");
    }
    // 不应该在 Controller 中组装实体
    Task task = new Task();
    task.setTitle(dto.getTitle());
    task.setProjectId(dto.getProjectId());
    taskRepository.insert(task);
    return ApiResponse.success(convertToVO(task));
}

// ✅ Controller 只做参数校验和调用
@PostMapping("/tasks")
public ApiResponse<TaskVO> createTask(@Valid @RequestBody TaskCreateDTO dto) {
    TaskVO result = taskService.createTask(dto, currentUserId);
    return ApiResponse.success(result);
}
```

#### 3.4 设计模式约束

指定场景化的设计模式可以让 Agent 代码结构更一致：

```
- 日志/审计：使用 AOP（@Aspect），不要在每个方法中手动写日志
- 对象转换：使用 Converter/Mapper 类，不要在 Service 中内联转换
- 配置获取：使用 @ConfigurationProperties，不要散落的 @Value
- 异常处理：使用全局异常处理器（@RestControllerAdvice），不要 try-catch 每个方法
```

### 4. 完整示例（task-flow）

```markdown
## 四、架构设计约束

### 4.1 架构风格

采用**功能分包 + 包内分层**架构。选择理由：
- 有 4 个清晰的业务模块（auth、project、task、dashboard）
- 模块间耦合度低，适合按功能拆分
- 包内使用 controller→service→repository 三层结构

### 4.2 后端目录结构

```
src/main/java/com/taskflow/
├── common/                          # 公共基础模块
│   ├── config/                      # Spring 配置类
│   │   ├── SecurityConfig.java      # Spring Security 配置
│   │   ├── CorsConfig.java          # 跨域配置
│   │   └── MyBatisPlusConfig.java   # MyBatis-Plus 分页/自动填充
│   ├── exception/                   # 异常体系
│   │   ├── BusinessException.java   # 业务异常基类
│   │   ├── ErrorCode.java           # 错误码枚举
│   │   └── GlobalExceptionHandler.java  # 全局异常处理器
│   ├── model/                       # 公共模型
│   │   ├── ApiResponse.java         # 统一响应包装
│   │   └── PageResult.java          # 分页响应包装
│   ├── security/                    # 安全相关
│   │   ├── JwtTokenProvider.java    # JWT工具
│   │   └── JwtAuthFilter.java       # JWT过滤器
│   └── util/                        # 工具类
│
├── auth/                            # 认证模块
│   ├── controller/
│   │   └── AuthController.java      # POST /api/v1/auth/register, /login
│   ├── service/
│   │   └── AuthService.java
│   └── model/
│       ├── dto/                     # RegisterDTO, LoginDTO
│       └── vo/                      # LoginVO (含Token)
│
├── project/                         # 项目管理模块
│   ├── controller/
│   │   └── ProjectController.java   # /api/v1/projects/**
│   ├── service/
│   │   └── ProjectService.java
│   ├── repository/
│   │   ├── ProjectMapper.java
│   │   └── ProjectMemberMapper.java
│   └── model/
│       ├── entity/                  # Project, ProjectMember
│       ├── dto/                     # ProjectCreateDTO
│       └── vo/                      # ProjectVO, ProjectDetailVO
│
├── task/                            # 任务管理模块
│   ├── controller/
│   │   ├── TaskController.java      # /api/v1/projects/{pid}/tasks/**
│   │   └── BoardColumnController.java
│   ├── service/
│   │   ├── TaskService.java
│   │   └── BoardColumnService.java
│   ├── repository/
│   │   ├── TaskMapper.java
│   │   ├── TaskTagMapper.java
│   │   └── BoardColumnMapper.java
│   └── model/
│       ├── entity/                  # Task, TaskTag, BoardColumn
│       ├── dto/
│       └── vo/
│
├── activity/                        # 操作记录模块
│   ├── aspect/
│   │   └── ActivityLogAspect.java   # AOP 自动记录操作日志
│   ├── service/
│   │   └── ActivityLogService.java
│   ├── repository/
│   │   └── ActivityLogMapper.java
│   └── model/
│       └── entity/
│           └── ActivityLog.java
│
└── TaskFlowApplication.java         # Spring Boot 启动类

src/main/resources/
├── application.yml                  # 公共配置
├── application-dev.yml              # 开发环境
├── application-prod.yml             # 生产环境
├── mapper/                          # MyBatis XML（复杂查询）
│   ├── TaskMapper.xml
│   └── ActivityLogMapper.xml
└── db/migration/                    # 数据库初始化脚本
    └── V1__init.sql
```

### 4.3 前端目录结构

```
src/
├── api/                    # API 调用封装，每个模块一个文件
│   ├── auth.ts             # login(), register()
│   ├── project.ts          # getProjects(), createProject()
│   ├── task.ts             # getTasks(), createTask(), updateTask()
│   └── client.ts           # Axios 实例、拦截器、基础配置
├── components/             # 通用 UI 组件（跨功能复用）
│   ├── Layout/             # 页面布局框架
│   ├── Loading/            # 加载状态组件
│   └── ErrorBoundary/      # 错误边界
├── features/               # 功能模块（每个模块自包含）
│   ├── auth/
│   │   ├── LoginPage.tsx
│   │   ├── RegisterPage.tsx
│   │   └── useAuth.ts      # 认证相关 Hook
│   ├── project/
│   │   ├── ProjectListPage.tsx
│   │   ├── ProjectDetailPage.tsx
│   │   └── components/     # 模块内部组件
│   └── task/
│       ├── BoardPage.tsx    # 看板主页
│       ├── TaskDetailDrawer.tsx
│       └── components/
│           ├── BoardColumn.tsx
│           ├── TaskCard.tsx
│           └── TaskFilterBar.tsx
├── hooks/                  # 通用自定义 Hooks
│   └── useRequest.ts       # 封装请求状态（loading/error/data）
├── stores/                 # Zustand 全局状态
│   ├── authStore.ts        # 用户认证状态
│   └── taskStore.ts        # 任务/看板状态
├── types/                  # TypeScript 类型定义
│   ├── api.ts              # API 响应通用类型
│   ├── project.ts          # 项目相关类型
│   └── task.ts             # 任务相关类型
├── utils/                  # 工具函数
│   ├── date.ts             # 日期格式化
│   └── token.ts            # Token 存取（localStorage）
├── App.tsx                 # 根组件、路由配置
├── main.tsx                # 入口文件
└── vite-env.d.ts

public/
├── favicon.ico
└── index.html
```

### 4.4 分层职责矩阵

| 层级 | 职责 | 允许依赖 | 禁止 |
|------|------|---------|------|
| Controller | 接收请求、@Valid参数校验、调用Service、返回VO | Service, VO, DTO | 直接调Repository、写SQL、事务注解 |
| Service | 业务逻辑编排、权限校验、事务管理(@ Transactional) | Repository, 其他Service（通过接口）, Entity, DTO | 感知HTTP对象、直接返回Entity |
| Repository(Mapper) | 单表CRUD、自定义SQL查询 | Entity | 调Service、包含业务判断 |
| Entity | 数据库表映射，纯数据、无业务方法 | 无 | 依赖任何Spring组件 |
| DTO | 请求参数载体，携带校验注解 | 无 | 包含业务逻辑 |
| VO | 响应数据载体，面向前端 | 无 | 包含敏感字段（如password_hash）|

### 4.5 设计模式约束

| 场景 | 必须使用 | 禁止 |
|------|---------|------|
| 操作审计 | AOP（@Aspect + 自定义注解 @Auditable） | 在每个Service方法中手写日志记录 |
| 对象转换 | 手写静态转换方法（XxxConverter） | 在Controller/Service中内联转换 |
| 异常处理 | @RestControllerAdvice 全局处理 | try-catch下沉到每个方法 |
| 依赖注入 | 构造函数注入（final字段） | @Autowired字段注入 |
| 配置绑定 | @ConfigurationProperties | 散落的@Value注解 |
| 鉴权检查 | Spring Security + 自定义注解 | 在Service中手动if判断角色 |

### 4.6 跨模块通信规则

- auth 模块：其他模块通过 Spring Security Context 获取当前用户，不直接调用 AuthService
- project 模块：task 模块需要校验项目成员权限时，通过 ProjectService 的公共方法
- activity 模块：通过 AOP 切面自动拦截，其他模块无需显式调用
- 禁止循环依赖：如果 A→B 且 B→A，必须提取公共接口到 common 模块
```
