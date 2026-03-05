# 模块详细展开（Part 3/4）：代码规范 & 测试策略

> 贯穿示例项目：**task-flow** — 面向小型团队的任务管理系统

---

## 模块五：代码规范

### 1. 模块目标

代码规范解决**一致性**问题。Agent 在长对话中容易"风格漂移"——前面的文件用 camelCase，后面就变成了 snake_case；前面用自定义异常，后面开始 try-catch。明确的规范是防止这种漂移的唯一手段。

### 2. 核心理念

1. **配置文件优于文字描述**：提供 `.eslintrc`、`.prettierrc` 比文字规则更精确
2. **示例优于规则**：给一段标准代码胜过写十条命名规则
3. **错误处理是规范的重中之重**：错误处理体系决定了代码的健壮性

### 3. 字段详解

#### 3.1 命名规范

**核心原则**：不同"种类"的元素使用不同命名风格，让开发者一看名字就知道它是什么

| 种类 | Java 风格 | TypeScript 风格 | 为什么 |
|------|----------|----------------|--------|
| 类/组件 | `PascalCase` | `PascalCase` | 大写开头=可实例化的东西 |
| 方法/函数 | `camelCase` | `camelCase` | 小写开头=可调用的动作 |
| 常量 | `UPPER_SNAKE` | `UPPER_SNAKE` | 全大写=不可变 |
| 布尔变量 | `isActive`, `hasPermission` | `isActive`, `hasPermission` | is/has前缀=布尔值 |
| 集合变量 | `tasks` (复数) | `tasks` (复数) | 复数=多个元素 |
| 接口 | `TaskService` | `TaskService` | 不加I前缀（非匈牙利命名）|

**方法命名语义约定**：

| 前缀 | 含义 | 返回类型 | 示例 |
|------|------|---------|------|
| `get` | 获取单个，不存在抛异常 | `T` | `getTaskById(Long id)` |
| `find` | 查找，不存在返回null/Optional | `T?` | `findByEmail(String email)` |
| `list` | 列表查询 | `List<T>` | `listByProjectId(Long pid)` |
| `page` | 分页查询 | `Page<T>` | `pageByFilter(TaskFilter f)` |
| `create` | 创建新资源 | `T` | `createTask(TaskCreateDTO dto)` |
| `update` | 更新已有资源 | `T` | `updateTask(Long id, TaskUpdateDTO dto)` |
| `delete` | 删除资源 | `void` | `deleteTask(Long id)` |
| `is/has/can` | 判断型 | `boolean` | `isMember(Long pid, Long uid)` |
| `check` | 校验，不通过抛异常 | `void` | `checkPermission(Long pid, Long uid)` |

#### 3.2 错误处理

**三层错误处理体系**：

```
第1层：参数校验层（Controller 入口）
  → 使用 @Valid + Jakarta Validation 注解（@NotNull, @Size, @Email 等）
  → 校验失败自动返回 400 + 字段级错误信息

第2层：业务异常层（Service 内部）
  → 使用自定义 BusinessException + ErrorCode 枚举
  → 业务规则违反时主动抛出

第3层：系统异常层（全局兜底）
  → @RestControllerAdvice 捕获未处理异常
  → 记录完整堆栈日志，返回 500 通用错误信息（不泄露内部细节）
```

**ErrorCode 枚举设计**：

```java
public enum ErrorCode {
    // 通用 (1xxxx)
    PARAM_INVALID(10001, 400, "参数无效"),
    UNAUTHORIZED(10002, 401, "未认证"),
    FORBIDDEN(10003, 403, "无权限"),
    NOT_FOUND(10004, 404, "资源不存在"),
    
    // 认证模块 (2xxxx)
    USER_NOT_FOUND(20001, 401, "邮箱或密码错误"),
    ACCOUNT_LOCKED(20002, 423, "账户已锁定，请稍后重试"),
    EMAIL_ALREADY_EXISTS(20003, 409, "邮箱已注册"),
    
    // 项目模块 (3xxxx)
    PROJECT_NOT_FOUND(30001, 404, "项目不存在"),
    NOT_PROJECT_MEMBER(30002, 403, "非项目成员"),
    CANNOT_REMOVE_OWNER(30003, 422, "不能移除项目创建者"),
    
    // 任务模块 (4xxxx)
    TASK_NOT_FOUND(40001, 404, "任务不存在"),
    COLUMN_NOT_EMPTY(40002, 422, "看板列下还有任务，无法删除"),
    TAG_LIMIT_EXCEEDED(40003, 422, "标签数量已达上限");
    
    private final int code;
    private final int httpStatus;
    private final String message;
}
```

#### 3.3 API 响应格式

**三种标准响应模板**（Agent 必须一致遵循）：

```json
// 1. 成功响应（单对象）
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "title": "完成登录页原型"
  },
  "timestamp": 1709625600000
}

// 2. 成功响应（分页）
{
  "code": 0,
  "message": "success",
  "data": {
    "items": [...],
    "total": 56,
    "page": 1,
    "pageSize": 20,
    "totalPages": 3
  },
  "timestamp": 1709625600000
}

// 3. 错误响应
{
  "code": 40001,
  "message": "任务不存在",
  "data": null,
  "timestamp": 1709625600000
}

// 4. 参数校验错误响应
{
  "code": 10001,
  "message": "参数无效",
  "data": null,
  "errors": [
    {"field": "title", "message": "长度必须在2-200之间"},
    {"field": "priority", "message": "不能为空"}
  ],
  "timestamp": 1709625600000
}
```

#### 3.4 日志规范

| 级别 | 使用场景 | 必须包含的信息 |
|------|---------|--------------|
| ERROR | 系统异常、不可恢复错误 | 异常堆栈、请求上下文、影响范围 |
| WARN | 业务异常、可恢复问题 | 业务上下文、用户ID、触发条件 |
| INFO | 关键操作（登录、创建、删除） | 操作人、操作对象、结果 |
| DEBUG | 调试信息 | 方法参数、中间结果（仅开发环境） |

```java
// ✅ 正确
log.error("创建任务失败: projectId={}, userId={}, title={}", 
          projectId, userId, dto.getTitle(), exception);
log.info("用户登录成功: userId={}, email={}", user.getId(), user.getEmail());

// ❌ 错误
log.error("出错了");           // 缺少上下文
log.info(user.toString());     // 可能泄露敏感信息
log.error("密码错误: password=" + password);  // 泄露密码！
```

### 4. 完整示例（task-flow）

```markdown
## 五、代码规范

### 5.1 命名规范

| 元素 | 后端(Java) | 前端(TypeScript) | 示例 |
|------|-----------|-----------------|------|
| 类/组件 | PascalCase | PascalCase | TaskService / TaskCard |
| 方法/函数 | camelCase | camelCase | getTaskById / fetchTasks |
| 常量 | UPPER_SNAKE | UPPER_SNAKE | MAX_TAG_COUNT / API_BASE_URL |
| 变量 | camelCase | camelCase | currentUser / taskList |
| 布尔值 | is/has/can前缀 | is/has/can前缀 | isAdmin / hasPermission |
| 数据库表 | snake_case | — | board_column |
| 数据库字段 | snake_case | — | created_at |
| API路径 | kebab-case 复数 | — | /api/v1/board-columns |
| 文件名(后端) | PascalCase同类名 | — | TaskService.java |
| 文件名(前端组件) | PascalCase | — | TaskCard.tsx |
| 文件名(前端工具) | camelCase | — | dateUtils.ts |

**方法命名**：get(单个必存在) / find(可空) / list(列表) / page(分页) / 
create / update / delete / is|has(布尔判断) / check(校验+抛异常)

### 5.2 代码风格

**后端**：
- 缩进：4 空格
- 行宽：120 字符
- import 排序：java.* → javax.* → org.* → com.*（空行分隔）
- 大括号：K&R 风格（左括号不换行）
- 注入方式：构造函数注入（final 字段 + @RequiredArgsConstructor）

**前端**：
- 缩进：2 空格
- 行宽：100 字符
- 引号：单引号
- 分号：是
- 尾逗号：always
- import 排序：react → 第三方库 → @/ 别名 → ./ 相对路径

### 5.3 注释规范

- 所有 public 方法必须有 JavaDoc（后端）/ JSDoc（前端）
- 行内注释用中文
- 复杂业务逻辑前必须有注释块说明逻辑原因（不是说明代码做了什么）
- TODO 格式：`// TODO: 描述`
- 禁止注释掉的代码（应直接删除，依赖 Git 历史）

### 5.4 错误处理

- 错误码体系：通用 1xxxx、认证 2xxxx、项目 3xxxx、任务 4xxxx
- BusinessException + ErrorCode 枚举
- @RestControllerAdvice 全局异常处理
- 参数校验用 Jakarta Validation 注解
- 前端 Axios 拦截器：401→跳转登录、403→提示无权限、500→通用错误提示

### 5.5 API 响应格式

统一格式 ApiResponse<T>：
- 成功：{"code": 0, "message": "success", "data": T, "timestamp": long}
- 失败：{"code": xxxxx, "message": "错误描述", "data": null, "timestamp": long}
- 校验失败额外包含："errors": [{"field": "xx", "message": "xx"}]
- 分页 data 结构：{"items": [], "total": n, "page": n, "pageSize": n, "totalPages": n}

### 5.6 日志规范

- 框架：SLF4J + Logback
- ERROR：系统异常（含堆栈）  
- WARN：业务异常
- INFO：关键操作（登录/登出/增删改）
- DEBUG：调试信息（仅 dev 环境）
- 日志必须包含上下文（userId, projectId 等），禁止裸 log.error("出错了")
- 禁止日志中出现密码、Token 等敏感信息
```

---

## 模块六：测试策略

### 1. 模块目标

测试策略告诉 Agent"**测试不是可选项**"以及"**怎么写才算有效的测试**"。没有测试策略，Agent 的典型行为是：要么完全不写测试，要么写出 `testMethod1()` 这种形式主义测试。

### 2. 核心理念

1. **测试和生产代码同等重要**：必须在输入中明确这一点
2. **场景驱动 > 覆盖率数字**：80% 覆盖率但漏掉关键边界 < 60% 覆盖率但覆盖所有核心场景
3. **测试应是可执行的文档**：好的测试名+Given-When-Then 结构 = 无需注释的业务文档

### 3. 字段详解

#### 3.1 测试金字塔规划

```
项目规模 → 推荐测试策略

小型（CLI/脚本）：
  └── 单元测试覆盖核心逻辑即可

中型（Web应用）：
  ├── 单元测试：Service 层核心业务逻辑（≥80%）
  ├── 集成测试：API 接口全覆盖（发HTTP请求→验证响应）
  └── 可选：关键前端组件测试

大型（复杂系统）：
  ├── 单元测试：Domain 层 100% 覆盖
  ├── 集成测试：API + 跨服务调用
  ├── E2E 测试：关键用户流程
  └── 性能测试：核心接口压测
```

#### 3.2 测试命名规范

**推荐格式**：`should_<预期行为>_when_<前提条件>`

```java
// ✅ 一看就懂的测试名（即使不看代码也知道在测什么）
should_returnTask_when_validIdProvided()
should_throwNotFound_when_taskNotExist()
should_lockAccount_when_fiveConsecutiveFailedLogins()
should_rejectDelete_when_userIsNotAdmin()
should_deductTagCount_when_removeTag()

// ❌ 看不懂的测试名
testGetTask()
testLogin1()
testLogin2()
test_error()
```

#### 3.3 Given-When-Then 结构

每个测试必须包含三个明确的区块：

```java
@Test
void should_lockAccount_when_fiveConsecutiveFailedLogins() {
    // Given — 准备前置条件
    User user = UserFixture.activeUser("test@example.com");
    userRepository.save(user);
    LoginDTO wrongLogin = new LoginDTO("test@example.com", "wrong_password");
    
    // When — 执行被测行为
    for (int i = 0; i < 5; i++) {
        try { authService.login(wrongLogin); } catch (BusinessException e) { /* expected */ }
    }
    
    // Then — 验证预期结果
    User lockedUser = userRepository.findByEmail("test@example.com");
    assertThat(lockedUser.getStatus()).isEqualTo(UserStatus.LOCKED);
    assertThat(lockedUser.getLockedUntil()).isAfter(LocalDateTime.now());
    assertThat(lockedUser.getLoginFailCount()).isEqualTo(5);
    
    // 再次登录（即使密码正确）也应失败
    LoginDTO correctLogin = new LoginDTO("test@example.com", "correct_password");
    assertThatThrownBy(() -> authService.login(correctLogin))
        .isInstanceOf(BusinessException.class)
        .extracting("errorCode").isEqualTo(ErrorCode.ACCOUNT_LOCKED);
}
```

#### 3.4 必测场景清单

**Agent 最容易遗漏的测试场景**：

| 场景类型 | 通常会写 | 通常会漏 |
|---------|---------|---------|
| 正常流程 | ✅ 基本CRUD | 批量操作、分页边界 |
| 参数校验 | ✅ 非空校验 | 长度边界、格式校验、类型错误 |
| 权限控制 | ❌ 经常跳过 | 非成员访问、角色越权、Token过期 |
| 业务规则 | ❌ 经常跳过 | 状态流转、数量限制、级联影响 |
| 并发安全 | ❌ 基本不写 | 同时修改同一任务、重复创建 |
| 边界值 | ❌ 经常跳过 | 第0页、空列表、极长字符串 |

**提供具体场景清单给 Agent 可以大幅减少遗漏**。

#### 3.5 Mock 策略

```
原则：Mock 外部依赖，不 Mock 内部逻辑

Service 测试：
  ✅ Mock Repository（数据库是外部依赖）
  ✅ Mock 第三方 API 调用
  ❌ 不要 Mock 另一个 Service（除非有循环依赖）
  ❌ 不要 Mock 被测方法的内部方法

集成测试：
  ✅ 使用内存数据库（H2 / SQLite）
  ✅ 使用 MockMvc 发真实 HTTP 请求
  ❌ 不要 Mock Service 层
```

### 4. 完整示例（task-flow）

```markdown
## 六、测试策略

### 6.1 测试要求

| 测试类型 | 是否必须 | 覆盖率 | 工具 | 范围 |
|---------|---------|--------|------|------|
| 后端单元测试 | 是 | Service ≥ 80% | JUnit 5 + Mockito + AssertJ | Service 层所有方法 |
| 后端集成测试 | 是 | 核心API 100% | SpringBootTest + MockMvc + H2 | 所有 Controller 端点 |
| 前端组件测试 | 是 | 核心组件 | Vitest + React Testing Library | 看板、任务卡片、筛选 |
| 前端API层测试 | 推荐 | 拦截器逻辑 | Vitest + msw | Axios拦截器和错误处理 |

### 6.2 测试规范

- 命名：`should_<预期行为>_when_<前提条件>`
- 结构：Given（准备）→ When（执行）→ Then（验证），用注释分隔
- Mock：Service 测试 Mock Repository；集成测试用 H2 真实数据库
- 断言库：后端用 AssertJ（链式断言），前端用 Vitest expect
- 测试数据：后端使用 Fixture 工厂类（UserFixture, TaskFixture）
- 每个方法至少覆盖：正常 + 参数异常 + 业务规则异常 三种路径

### 6.3 必测场景清单

#### 认证模块（AuthService）
- [ ] 注册成功 → 返回用户信息，密码 BCrypt 加密存储
- [ ] 注册邮箱已存在 → 抛出 EMAIL_ALREADY_EXISTS
- [ ] 注册密码不符合规则 → 参数校验失败（@Valid）
- [ ] 登录成功 → 返回 JWT Token，失败计数重置为 0
- [ ] 登录邮箱不存在 → 抛出 USER_NOT_FOUND（不区分邮箱/密码错误）
- [ ] 登录密码错误 → 失败计数 +1，抛出 USER_NOT_FOUND
- [ ] 连续 5 次密码错误 → 账户锁定 30 分钟
- [ ] 锁定期内正确密码 → 仍拒绝，抛出 ACCOUNT_LOCKED
- [ ] 锁定期过后 → 可正常登录，状态恢复 ACTIVE

#### 项目模块（ProjectService）
- [ ] 创建项目 → 创建者自动成为 ADMIN，自动创建 3 个默认看板列
- [ ] 获取项目列表 → 仅返回用户已加入的项目
- [ ] 添加成员 → 默认 MEMBER 角色
- [ ] 添加已存在的成员 → 提示已存在
- [ ] 移除成员 → 成功移除
- [ ] 移除项目创建者 → 抛出 CANNOT_REMOVE_OWNER
- [ ] 非成员访问项目 → 抛出 NOT_PROJECT_MEMBER (403)

#### 任务模块（TaskService）
- [ ] 创建任务 → 默认放入第一个看板列、自动记录 ActivityLog
- [ ] 创建任务标题超长（>200字符）→ 参数校验失败
- [ ] 修改任务 → 记录变更前后差异到 ActivityLog
- [ ] 拖拽任务（改变看板列）→ 更新 column_id 和 position
- [ ] 拖拽任务（同列排序）→ 仅更新 position
- [ ] 删除任务（ADMIN）→ 成功删除 + 记录日志
- [ ] 删除任务（MEMBER）→ 抛出 FORBIDDEN (403)
- [ ] 添加标签（≤5个）→ 成功
- [ ] 添加标签（已有5个）→ 抛出 TAG_LIMIT_EXCEEDED
- [ ] 按负责人筛选 → 仅返回该负责人的任务
- [ ] 按优先级+标签组合筛选 → 交集结果
- [ ] 关键词搜索 → 模糊匹配 title 和 description

#### 看板列模块（BoardColumnService）
- [ ] 自定义列名 → 更新成功
- [ ] 删除空列 → 成功删除
- [ ] 删除非空列 → 抛出 COLUMN_NOT_EMPTY
- [ ] 调整列顺序 → position 重新排列

### 6.4 测试工具类

```java
// UserFixture.java — 测试数据工厂
public class UserFixture {
    public static User activeUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(BCrypt.encode("Test@12345"));
        user.setNickname("测试用户");
        user.setStatus(UserStatus.ACTIVE);
        user.setLoginFailCount(0);
        return user;
    }
    
    public static User lockedUser(String email) {
        User user = activeUser(email);
        user.setStatus(UserStatus.LOCKED);
        user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
        user.setLoginFailCount(5);
        return user;
    }
}

// TaskFixture.java
public class TaskFixture {
    public static Task defaultTask(Long projectId, Long columnId, Long creatorId) {
        Task task = new Task();
        task.setTitle("测试任务");
        task.setProjectId(projectId);
        task.setColumnId(columnId);
        task.setCreatorId(creatorId);
        task.setPriority(Priority.MEDIUM);
        task.setPosition(0);
        return task;
    }
}
```

### 6.5 集成测试示例

```java
@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {
    
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    
    private String adminToken;  // 测试用 JWT
    
    @BeforeEach
    void setUp() {
        // 初始化测试数据 + 获取Token
    }
    
    @Test
    void should_createTask_when_validRequest() throws Exception {
        TaskCreateDTO dto = new TaskCreateDTO("新任务", Priority.HIGH, null, null);
        
        mockMvc.perform(post("/api/v1/projects/1/tasks")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.title").value("新任务"))
            .andExpect(jsonPath("$.data.priority").value("HIGH"));
    }
    
    @Test
    void should_return403_when_nonMemberAccessTask() throws Exception {
        mockMvc.perform(get("/api/v1/projects/1/tasks")
                .header("Authorization", "Bearer " + outsiderToken))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(30002));
    }
}
```
```
