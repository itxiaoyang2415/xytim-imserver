# WEB后台用户充值功能开发指南 - AI提示词文档

## 项目背景

本项目是一个即时通讯（IM）系统，已完成通讯端的支付转账功能。现在需要为Web后台管理系统开发用户充值管理功能，供管理员为用户充值并查看充值记录。

## 数据库表结构

### 1. 用户钱包表 (`t_user_wallet`)
```sql
CREATE TABLE `t_user_wallet` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID，关联IM系统用户',
  `balance` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '可用余额',
  `frozen_balance` decimal(15, 2) NOT NULL DEFAULT 0.00 COMMENT '冻结金额',
  `currency` varchar(10) NOT NULL DEFAULT 'CNY' COMMENT '币种',
  `wallet_status` tinyint NOT NULL DEFAULT 1 COMMENT '钱包状态:1-正常,2-冻结,3-禁用',
  `security_level` tinyint NOT NULL DEFAULT 1 COMMENT '安全等级',
  `pay_password` varchar(100) DEFAULT NULL COMMENT '支付密码(加密存储)',
  `version` int NOT NULL DEFAULT 0 COMMENT '版本号(乐观锁)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户钱包表';
```

### 2. 充值提现订单表 (`t_wallet_order`)
```sql
CREATE TABLE `t_wallet_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `type` tinyint NOT NULL COMMENT '类型:1-充值,2-提现',
  `amount` decimal(15, 2) NOT NULL COMMENT '金额',
  `channel` varchar(50) NOT NULL COMMENT '支付渠道:alipay,wechat,bank,admin',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态:1-处理中,2-成功,3-失败,4-已取消',
  `channel_order_no` varchar(100) DEFAULT NULL COMMENT '渠道订单号',
  `notify_data` text COMMENT '渠道回调数据',
  `completed_time` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_type` (`user_id`, `type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充值提现表';
```

### 3. 交易流水表 (`t_wallet_transaction`)
```sql
CREATE TABLE `t_wallet_transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `transaction_no` varchar(32) NOT NULL COMMENT '交易流水号',
  `wallet_id` bigint DEFAULT NULL COMMENT '钱包ID',
  `from_user_id` bigint NOT NULL COMMENT '付款用户ID',
  `to_user_id` bigint NOT NULL COMMENT '收款用户ID',
  `amount` decimal(15, 2) NOT NULL COMMENT '交易金额',
  `transaction_type` tinyint NOT NULL COMMENT '交易类型:1-转账,2-红包,3-充值,4-提现,5-退款',
  `business_type` varchar(50) DEFAULT NULL COMMENT '业务类型',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态:1-处理中,2-成功,3-失败,4-已退款',
  `remark` varchar(200) DEFAULT NULL COMMENT '交易备注',
  `relation_id` varchar(50) DEFAULT NULL COMMENT '关联业务ID(如红包ID、订单ID)',
  `fee` decimal(15, 2) DEFAULT 0.00 COMMENT '手续费',
  `before_balance` decimal(15, 2) DEFAULT NULL COMMENT '交易前余额',
  `after_balance` decimal(15, 2) DEFAULT NULL COMMENT '交易后余额',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_transaction_no` (`transaction_no`),
  KEY `idx_from_user` (`from_user_id`, `created_at`),
  KEY `idx_to_user` (`to_user_id`, `created_at`),
  KEY `idx_type_status` (`transaction_type`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易流水表';
```

### 4. 用户表 (`im_user`)
```sql
CREATE TABLE `im_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_name` varchar(255) NOT NULL COMMENT '用户名',
  `nick_name` varchar(255) NOT NULL COMMENT '用户昵称',
  -- 其他字段省略...
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户';
```

## 项目技术栈与代码规范

### 技术栈
- **后端框架**: Spring Boot 3.x
- **ORM框架**: MyBatis-Plus
- **API文档**: Swagger (OpenAPI 3)
- **校验框架**: Jakarta Validation
- **密码加密**: BCrypt (Spring Security)
- **工具库**: Lombok

### 现有代码结构规范

#### 1. 包结构
```
com.bx.implatform
├── controller       # 控制器层
├── service          # 服务接口层
│   └── impl        # 服务实现层
├── mapper          # MyBatis Mapper层
├── entity          # 实体类（对应数据库表）
├── dto             # 数据传输对象（接收请求参数）
├── vo              # 视图对象（返回响应数据）
├── enums           # 枚举类
├── result          # 统一响应结果类
├── exception       # 异常处理
└── annotation      # 自定义注解
```

#### 2. Controller层规范
```java
@Tag(name = "功能模块名称")
@RestController
@RequestMapping("/路径前缀")
@RequiredArgsConstructor
public class XxxController {
    
    private final XxxService xxxService;
    
    @PostMapping("/methodPath")
    @Operation(summary = "接口简要说明", description = "接口详细说明")
    public Result<返回类型> methodName(@Valid @RequestBody 参数DTO dto) {
        返回类型 result = xxxService.methodName(dto);
        return ResultUtils.success(result);
    }
}
```

#### 3. Service层规范
```java
// 接口
public interface XxxService extends IService<Entity> {
    返回类型 methodName(参数类型 param);
}

// 实现类
@Service
@RequiredArgsConstructor
public class XxxServiceImpl extends ServiceImpl<XxxMapper, Entity> implements XxxService {
    
    private final OtherService otherService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public 返回类型 methodName(参数类型 param) {
        // 实现逻辑
    }
}
```

#### 4. Entity层规范
```java
@Data
@TableName("表名")
public class Entity {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private 类型 字段名;  // 驼峰命名，自动映射到下划线数据库字段
    
    @TableField("created_at")
    private Date createdAt;  // 特殊字段可手动指定映射
}
```

#### 5. DTO层规范（请求参数）
```java
@Data
public class XxxDTO {
    @NotNull(message = "xxx不能为空")
    private Long id;
    
    @NotBlank(message = "xxx不能为空")
    @Length(max = 100, message = "xxx长度不能超过100")
    private String name;
    
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;
}
```

#### 6. VO层规范（响应数据）
```java
@Data
public class XxxVO {
    private Long id;
    private String name;
    // 只包含需要返回给前端的字段
}
```

#### 7. 统一响应结果
```java
// 成功响应
return ResultUtils.success();           // 无数据
return ResultUtils.success(data);       // 有数据

// 失败响应会抛出异常，由全局异常处理器统一处理
throw new GlobalException("错误信息");
```

## 需求详细说明

### 需求一：管理员为用户充值接口

#### 功能描述
Web后台管理员可以为指定用户进行充值操作，充值成功后：
1. 更新用户钱包余额
2. 创建充值订单记录（`t_wallet_order`）
3. 创建交易流水记录（`t_wallet_transaction`）

#### 接口要求

**请求路径**: `POST /admin/wallet/recharge`

**请求参数** (AdminRechargeDTO):
```java
{
    "userId": 用户ID (Long, 必填),
    "amount": 充值金额 (BigDecimal, 必填, 最小0.01, 最大100000),
    "currency": 币种 (String, 可选, 默认"USDT"),
    "remark": 充值备注 (String, 可选, 最大200字符),
    "adminId": 管理员ID (Long, 必填, 从Session或Token获取),
    "adminName": 管理员名称 (String, 可选, 从Session或Token获取)
}
```

**响应数据** (AdminRechargeVO):
```java
{
    "orderNo": 订单号,
    "userId": 用户ID,
    "userName": 用户名,
    "nickName": 用户昵称,
    "amount": 充值金额,
    "currency": 币种,
    "beforeBalance": 充值前余额,
    "afterBalance": 充值后余额,
    "remark": 备注,
    "rechargeTime": 充值时间
}
```

#### 业务逻辑要求

1. **参数校验**
   - 用户ID不能为空
   - 充值金额必须大于0.01，小于等于100000
   - 币种如果不传默认为"USDT"

2. **用户校验**
   - 验证用户是否存在（查询`im_user`表）
   - 用户状态必须正常（status=0）
   - 用户不能是已注销状态

3. **钱包操作**
   - 查询或创建用户钱包（如果用户没有钱包，自动创建）
   - 验证钱包状态是否正常（walletStatus=1）
   - 钱包币种必须与充值币种一致
   - 使用乐观锁更新钱包余额（防止并发问题）

4. **生成订单号和流水号**
   - 订单号格式：`ORD + yyyyMMddHHmmss + 6位随机数`
   - 流水号格式：`TXN + yyyyMMddHHmmss + 6位随机数`

5. **记录充值订单**（`t_wallet_order`表）
   ```
   - order_no: 生成的订单号
   - user_id: 用户ID
   - type: 1 (充值)
   - amount: 充值金额
   - channel: "admin" (后台充值)
   - status: 2 (成功)
   - channel_order_no: 可存储管理员ID或其他标识
   - completed_time: 当前时间
   ```

6. **记录交易流水**（`t_wallet_transaction`表）
   ```
   - transaction_no: 生成的流水号
   - wallet_id: 钱包ID
   - from_user_id: 0 (系统充值)
   - to_user_id: 用户ID
   - amount: 充值金额
   - transaction_type: 3 (充值)
   - business_type: "ADMIN_RECHARGE"
   - status: 2 (成功)
   - remark: 充值备注或"管理员充值"
   - relation_id: 订单号
   - before_balance: 充值前余额
   - after_balance: 充值后余额
   ```

7. **事务控制**
   - 整个充值操作必须在一个事务中完成
   - 任何步骤失败都要回滚
   - 使用`@Transactional(rollbackFor = Exception.class)`

8. **异常处理**
   - 用户不存在：抛出异常 "用户不存在"
   - 用户状态异常：抛出异常 "用户状态异常，无法充值"
   - 钱包被冻结：抛出异常 "用户钱包已被冻结"
   - 币种不匹配：抛出异常 "币种不匹配"
   - 余额更新失败（乐观锁）：抛出异常 "充值失败，请重试"

#### 代码实现要点

1. **DTO参数校验**
```java
@Data
public class AdminRechargeDTO {
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "0.01", message = "充值金额必须大于0.01")
    @DecimalMax(value = "100000", message = "充值金额不能超过100000")
    private BigDecimal amount;
    
    @Length(max = 10, message = "币种长度不能超过10")
    private String currency = "USDT";
    
    @Length(max = 200, message = "备注长度不能超过200")
    private String remark;
}
```

2. **Service层核心逻辑伪代码**
```
1. 查询用户信息，验证用户存在且状态正常
2. 获取或创建用户钱包
3. 验证钱包状态和币种
4. 记录充值前余额
5. 生成订单号和流水号
6. 使用乐观锁更新钱包余额：
   UPDATE t_user_wallet 
   SET balance = balance + #{amount}, 
       version = version + 1 
   WHERE user_id = #{userId} 
     AND version = #{version}
7. 检查更新结果，如果失败（version冲突）抛异常
8. 插入充值订单记录
9. 插入交易流水记录
10. 返回充值结果VO
```

3. **需要注入的依赖Service**
```java
@RequiredArgsConstructor
public class AdminWalletServiceImpl {
    private final UserWalletService userWalletService;      // 钱包服务
    private final WalletOrderService walletOrderService;    // 订单服务
    private final WalletTransactionService walletTransactionService;  // 流水服务
    private final UserService userService;                  // 用户服务
}
```

---

### 需求二：充值记录列表查询接口

#### 功能描述
Web后台管理员可以查询所有用户的充值记录，支持分页和条件筛选。

#### 接口要求

**请求路径**: `GET /admin/wallet/recharge-records`

**请求参数** (Query Parameters):
```
- page: 页码 (Integer, 可选, 默认1)
- size: 每页数量 (Integer, 可选, 默认20, 最大100)
- userId: 用户ID (Long, 可选, 用于筛选指定用户)
- userName: 用户名/昵称 (String, 可选, 模糊查询)
- startTime: 开始时间 (String, 可选, 格式: yyyy-MM-dd HH:mm:ss)
- endTime: 结束时间 (String, 可选, 格式: yyyy-MM-dd HH:mm:ss)
- minAmount: 最小金额 (BigDecimal, 可选)
- maxAmount: 最大金额 (BigDecimal, 可选)
- channel: 支付渠道 (String, 可选, 如: admin, alipay, wechat)
- status: 订单状态 (Integer, 可选, 1-处理中, 2-成功, 3-失败, 4-已取消)
```

**响应数据** (Page<AdminRechargeRecordVO>):
```java
{
    "total": 总记录数,
    "pages": 总页数,
    "current": 当前页码,
    "size": 每页数量,
    "records": [
        {
            "id": 记录ID,
            "orderNo": 订单号,
            "userId": 用户ID,
            "userName": 用户名,
            "nickName": 用户昵称,
            "amount": 充值金额,
            "currency": 币种,
            "channel": 充值渠道,
            "channelName": 渠道名称（中文）,
            "status": 状态,
            "statusName": 状态名称（中文）,
            "beforeBalance": 充值前余额（从transaction表获取）,
            "afterBalance": 充值后余额（从transaction表获取）,
            "remark": 备注,
            "completedTime": 完成时间,
            "createdAt": 创建时间
        }
    ]
}
```

#### 业务逻辑要求

1. **参数校验**
   - page最小为1
   - size最小为1，最大为100
   - 时间格式校验（如果传了startTime/endTime）
   - 金额范围校验（minAmount不能大于maxAmount）

2. **查询逻辑**
   - 基础查询：查询`t_wallet_order`表，type=1（充值类型）
   - 关联查询：LEFT JOIN `im_user`表获取用户信息
   - 关联查询：LEFT JOIN `t_wallet_transaction`表获取余额变动信息

3. **筛选条件**（按需组合）
   ```sql
   WHERE o.type = 1  -- 只查充值记录
   AND o.user_id = ? (如果传了userId)
   AND (u.user_name LIKE ? OR u.nick_name LIKE ?) (如果传了userName)
   AND o.created_at >= ? (如果传了startTime)
   AND o.created_at <= ? (如果传了endTime)
   AND o.amount >= ? (如果传了minAmount)
   AND o.amount <= ? (如果传了maxAmount)
   AND o.channel = ? (如果传了channel)
   AND o.status = ? (如果传了status)
   ```

4. **排序**
   - 默认按创建时间倒序排列（最新的在前）
   - `ORDER BY o.created_at DESC`

5. **分页**
   - 使用MyBatis-Plus的Page分页
   - 返回分页结果包含：总记录数、总页数、当前页、每页数量、记录列表

6. **数据处理**
   - 渠道名称转换：
     - "admin" -> "后台充值"
     - "alipay" -> "支付宝"
     - "wechat" -> "微信支付"
     - "bank" -> "银行卡"
   - 状态名称转换：
     - 1 -> "处理中"
     - 2 -> "成功"
     - 3 -> "失败"
     - 4 -> "已取消"

#### 代码实现要点

1. **Controller层**
```java
@GetMapping("/recharge-records")
@Operation(summary = "查询充值记录", description = "分页查询用户充值记录，支持多条件筛选")
public Result<Page<AdminRechargeRecordVO>> getRechargeRecords(
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "20") Integer size,
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) String userName,
        @RequestParam(required = false) String startTime,
        @RequestParam(required = false) String endTime,
        @RequestParam(required = false) BigDecimal minAmount,
        @RequestParam(required = false) BigDecimal maxAmount,
        @RequestParam(required = false) String channel,
        @RequestParam(required = false) Integer status) {
    // 调用Service
}
```

2. **Service层查询逻辑**
```
1. 参数校验和处理
2. 构建分页对象：Page<WalletOrder> pageParam = new Page<>(page, size)
3. 构建查询条件：
   LambdaQueryWrapper<WalletOrder> wrapper = new LambdaQueryWrapper<>()
       .eq(WalletOrder::getType, 1)  // 充值类型
       .eq(userId != null, WalletOrder::getUserId, userId)
       .ge(startTime != null, WalletOrder::getCreatedAt, startTime)
       .le(endTime != null, WalletOrder::getCreatedAt, endTime)
       .ge(minAmount != null, WalletOrder::getAmount, minAmount)
       .le(maxAmount != null, WalletOrder::getAmount, maxAmount)
       .eq(channel != null, WalletOrder::getChannel, channel)
       .eq(status != null, WalletOrder::getStatus, status)
       .orderByDesc(WalletOrder::getCreatedAt);
4. 执行分页查询：Page<WalletOrder> orderPage = page(pageParam, wrapper)
5. 如果需要关联用户名模糊查询，使用Mapper自定义SQL或分两步查询
6. 遍历结果，填充VO数据：
   - 查询用户信息（批量查询优化）
   - 查询对应的transaction记录获取余额信息
   - 转换渠道名称和状态名称
7. 返回分页结果
```

3. **SQL优化建议**
```java
// 可以在Mapper中定义自定义SQL实现关联查询，提升性能
@Select("SELECT o.*, u.user_name, u.nick_name, " +
        "t.before_balance, t.after_balance, t.remark " +
        "FROM t_wallet_order o " +
        "LEFT JOIN im_user u ON o.user_id = u.id " +
        "LEFT JOIN t_wallet_transaction t ON o.order_no = t.relation_id " +
        "WHERE o.type = 1 " +
        "AND (#{userName} IS NULL OR u.user_name LIKE CONCAT('%', #{userName}, '%') " +
        "     OR u.nick_name LIKE CONCAT('%', #{userName}, '%')) " +
        "-- 其他条件... " +
        "ORDER BY o.created_at DESC")
Page<AdminRechargeRecordVO> selectRechargeRecordsPage(Page<?> page, @Param("userName") String userName, ...);
```

4. **枚举类建议**
```java
// 充值渠道枚举
public enum RechargeChannel {
    ADMIN("admin", "后台充值"),
    ALIPAY("alipay", "支付宝"),
    WECHAT("wechat", "微信支付"),
    BANK("bank", "银行卡");
    
    private final String code;
    private final String name;
}

// 订单状态枚举
public enum OrderStatus {
    PROCESSING(1, "处理中"),
    SUCCESS(2, "成功"),
    FAILED(3, "失败"),
    CANCELLED(4, "已取消");
    
    private final Integer code;
    private final String name;
}
```

---

## 文件创建清单

根据以上需求，需要创建以下文件：

### Controller层
- **文件路径**: `im-platform/src/main/java/com/bx/implatform/controller/AdminWalletController.java`
- **说明**: 新建管理后台钱包控制器，包含2个接口方法

### Service层
- **接口文件**: `im-platform/src/main/java/com/bx/implatform/service/AdminWalletService.java`
- **实现文件**: `im-platform/src/main/java/com/bx/implatform/service/impl/AdminWalletServiceImpl.java`
- **说明**: 新建管理后台钱包服务，实现充值和查询逻辑

### DTO层（请求参数）
- **AdminRechargeDTO.java**: 管理员充值请求DTO
  - 路径：`im-platform/src/main/java/com/bx/implatform/dto/AdminRechargeDTO.java`

### VO层（响应数据）
- **AdminRechargeVO.java**: 充值成功响应VO
  - 路径：`im-platform/src/main/java/com/bx/implatform/vo/AdminRechargeVO.java`
- **AdminRechargeRecordVO.java**: 充值记录列表项VO
  - 路径：`im-platform/src/main/java/com/bx/implatform/vo/AdminRechargeRecordVO.java`

### Mapper层（可选）
如果需要自定义SQL查询，可扩展现有Mapper：
- **WalletOrderMapper.java**: 添加自定义查询方法
  - 路径：`im-platform/src/main/java/com/bx/implatform/mapper/WalletOrderMapper.java`
- **WalletOrderMapper.xml**: 添加自定义SQL
  - 路径：`im-platform/src/main/resources/mapper/WalletOrderMapper.xml`

### 枚举类（建议创建）
- **RechargeChannelEnum.java**: 充值渠道枚举
  - 路径：`im-platform/src/main/java/com/bx/implatform/enums/RechargeChannelEnum.java`
- **OrderStatusEnum.java**: 订单状态枚举（如果还没有）
  - 路径：`im-platform/src/main/java/com/bx/implatform/enums/OrderStatusEnum.java`

---

## 权限控制建议

由于这是管理后台接口，需要考虑权限控制：

1. **路径前缀统一**: 建议所有管理后台接口使用`/admin`前缀，如`/admin/wallet/recharge`

2. **权限拦截器**: 添加拦截器验证请求是否来自管理员
   ```java
   // 可在现有的AuthInterceptor中添加管理员角色校验
   // 或创建新的AdminAuthInterceptor
   ```

3. **管理员身份验证**:
   - 从Session/Token中获取当前登录管理员信息
   - 验证是否具有充值权限
   - 记录操作日志（可选）

4. **操作日志**（可选扩展）:
   ```sql
   -- 可创建管理员操作日志表
   CREATE TABLE `t_admin_operation_log` (
     `id` bigint NOT NULL AUTO_INCREMENT,
     `admin_id` bigint NOT NULL COMMENT '管理员ID',
     `operation_type` varchar(50) NOT NULL COMMENT '操作类型',
     `operation_desc` varchar(200) COMMENT '操作描述',
     `target_user_id` bigint COMMENT '目标用户ID',
     `operation_data` text COMMENT '操作数据(JSON)',
     `ip_address` varchar(50) COMMENT 'IP地址',
     `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
     PRIMARY KEY (`id`)
   ) COMMENT='管理员操作日志表';
   ```

---

## 测试用例建议

### 充值接口测试用例

#### 正常场景
1. **正常充值**: 为存在的用户充值100 USDT
2. **首次充值**: 为没有钱包的用户充值（自动创建钱包）
3. **小额充值**: 充值0.01（最小金额）
4. **大额充值**: 充值100000（最大金额）
5. **不同币种**: 充值其他币种（如CNY）

#### 异常场景
1. **用户不存在**: userId=9999999（不存在）
2. **金额为0**: amount=0
3. **金额为负数**: amount=-100
4. **金额超限**: amount=100001
5. **用户已注销**: status=1的用户
6. **钱包被冻结**: walletStatus=2的用户
7. **币种不匹配**: 用户钱包币种为CNY，但充值USDT
8. **并发充值**: 同时为同一用户发起多次充值（测试乐观锁）

### 查询接口测试用例

#### 正常场景
1. **无条件查询**: 查询全部充值记录
2. **分页查询**: page=2, size=10
3. **按用户查询**: userId=1
4. **按用户名模糊查询**: userName="user"
5. **按时间范围查询**: startTime和endTime
6. **按金额范围查询**: minAmount=10, maxAmount=1000
7. **按渠道查询**: channel="admin"
8. **按状态查询**: status=2（成功）
9. **组合条件查询**: 多个条件同时传入

#### 边界场景
1. **空结果**: 查询不存在的条件
2. **单条结果**: 精确查询某个订单号
3. **大量数据**: 查询全部记录（测试性能）
4. **页码超限**: page=999（超过总页数）
5. **每页数量超限**: size=1000（超过最大限制）

---

## 性能优化建议

1. **批量查询用户信息**
   ```java
   // 避免在循环中逐个查询用户
   List<Long> userIds = orders.stream()
       .map(WalletOrder::getUserId)
       .distinct()
       .collect(Collectors.toList());
   List<User> users = userService.listByIds(userIds);
   Map<Long, User> userMap = users.stream()
       .collect(Collectors.toMap(User::getId, u -> u));
   ```

2. **使用自定义SQL减少关联查询**
   - 一次性关联查询order、user、transaction三表
   - 避免N+1查询问题

3. **添加数据库索引**
   ```sql
   -- 已有索引
   KEY `idx_user_type` (`user_id`, `type`),
   KEY `idx_status` (`status`),
   
   -- 建议添加复合索引
   KEY `idx_type_created` (`type`, `created_at`),
   KEY `idx_channel_status` (`channel`, `status`)
   ```

4. **缓存用户信息**（可选）
   - 如果用户信息不常变动，可使用Redis缓存
   - 减少数据库查询压力

5. **异步记录操作日志**
   - 使用消息队列或异步任务
   - 不阻塞主业务流程

---

## API文档示例（Swagger）

### 1. 充值接口文档

```
POST /admin/wallet/recharge
Summary: 管理员为用户充值
Description: 管理员可为指定用户进行余额充值，充值成功后自动更新钱包余额并生成充值记录

Request Body (application/json):
{
  "userId": 1,
  "amount": 100.00,
  "currency": "USDT",
  "remark": "后台充值"
}

Success Response (200):
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "orderNo": "ORD20251007120000123456",
    "userId": 1,
    "userName": "user",
    "nickName": "用户昵称",
    "amount": 100.00,
    "currency": "USDT",
    "beforeBalance": 500.00,
    "afterBalance": 600.00,
    "remark": "后台充值",
    "rechargeTime": "2025-10-07 12:00:00"
  }
}

Error Response (400/500):
{
  "code": 400,
  "msg": "用户不存在",
  "data": null
}
```

### 2. 查询接口文档

```
GET /admin/wallet/recharge-records
Summary: 查询充值记录列表
Description: 分页查询用户充值记录，支持多条件筛选

Query Parameters:
- page: 页码 (默认1)
- size: 每页数量 (默认20)
- userId: 用户ID (可选)
- userName: 用户名/昵称 (可选，模糊查询)
- startTime: 开始时间 (可选, 格式: yyyy-MM-dd HH:mm:ss)
- endTime: 结束时间 (可选)
- minAmount: 最小金额 (可选)
- maxAmount: 最大金额 (可选)
- channel: 支付渠道 (可选)
- status: 订单状态 (可选)

Success Response (200):
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "total": 100,
    "pages": 5,
    "current": 1,
    "size": 20,
    "records": [
      {
        "id": 1,
        "orderNo": "ORD20251007120000123456",
        "userId": 1,
        "userName": "user",
        "nickName": "用户昵称",
        "amount": 100.00,
        "currency": "USDT",
        "channel": "admin",
        "channelName": "后台充值",
        "status": 2,
        "statusName": "成功",
        "beforeBalance": 500.00,
        "afterBalance": 600.00,
        "remark": "后台充值",
        "completedTime": "2025-10-07 12:00:00",
        "createdAt": "2025-10-07 12:00:00"
      }
    ]
  }
}
```

---

## 注意事项

1. **事务管理**: 充值操作必须保证原子性，使用`@Transactional`注解
2. **并发控制**: 使用乐观锁（version字段）防止并发充值导致数据不一致
3. **金额精度**: 使用`BigDecimal`处理金额，避免浮点数精度问题
4. **安全性**: 充值接口需要严格的权限控制，防止未授权访问
5. **日志记录**: 关键操作（充值成功/失败）需记录详细日志
6. **参数校验**: 使用Jakarta Validation注解进行参数校验
7. **异常处理**: 统一使用`GlobalException`抛出业务异常
8. **订单号唯一性**: 确保订单号和流水号全局唯一
9. **幂等性**: 考虑接口幂等性，防止重复充值（可使用订单号或请求ID）
10. **审计追踪**: 记录管理员操作日志，方便问题追溯

---

## 扩展功能建议（可选）

1. **批量充值**: 支持一次为多个用户充值
2. **充值审核**: 充值需要审核流程（待审核->已审核->已完成）
3. **充值撤销**: 支持撤销错误的充值操作（退款）
4. **导出功能**: 支持导出充值记录为Excel
5. **统计报表**: 充值金额统计、趋势分析
6. **通知功能**: 充值成功后通知用户（站内信/邮件/短信）
7. **限额控制**: 单笔/单日充值限额设置
8. **币种兑换**: 支持不同币种充值并自动兑换
9. **充值优惠**: 充值满额赠送活动
10. **分级管理**: 不同级别管理员充值权限不同

---

## 开发流程建议

### 第一步：创建实体类和枚举
1. 确认`UserWallet`、`WalletOrder`、`WalletTransaction`实体已存在
2. 创建`RechargeChannelEnum`和`OrderStatusEnum`（如果没有）

### 第二步：创建DTO和VO
1. 创建`AdminRechargeDTO`（充值请求参数）
2. 创建`AdminRechargeVO`（充值响应数据）
3. 创建`AdminRechargeRecordVO`（充值记录查询响应）

### 第三步：实现Service层
1. 创建`AdminWalletService`接口
2. 创建`AdminWalletServiceImpl`实现类
3. 实现充值方法：
   - 用户验证
   - 钱包验证
   - 生成订单号和流水号
   - 更新余额（使用乐观锁）
   - 插入订单和流水记录
4. 实现查询方法：
   - 构建分页查询
   - 关联用户表
   - 关联流水表
   - 数据转换（枚举转中文）

### 第四步：实现Controller层
1. 创建`AdminWalletController`
2. 添加充值接口方法
3. 添加查询接口方法
4. 添加Swagger注解

### 第五步：权限控制
1. 添加管理员认证拦截器（或在现有拦截器中扩展）
2. 验证管理员身份和权限

### 第六步：测试
1. 单元测试：测试Service层各个方法
2. 接口测试：使用Postman/Apifox测试接口
3. 压力测试：测试并发充值场景
4. 异常测试：测试各种异常情况

### 第七步：优化
1. 检查SQL性能，添加必要的索引
2. 优化查询逻辑，减少N+1查询
3. 添加日志记录
4. 完善异常处理

### 第八步：文档
1. 完善Swagger文档
2. 编写接口使用说明
3. 提供前端对接文档

---

## AI开发提示词模板

### 提示词1：创建充值接口

```
请根据以下需求，在Spring Boot项目中实现管理员为用户充值的功能：

【项目信息】
- 框架：Spring Boot 3.x + MyBatis-Plus
- 包路径：com.bx.implatform
- 已存在的Service：UserWalletService、WalletOrderService、WalletTransactionService、UserService

【数据库表】
1. t_user_wallet - 用户钱包表（包含字段：id, user_id, balance, frozen_balance, currency, wallet_status, version等）
2. t_wallet_order - 充值订单表（包含字段：id, user_id, order_no, type, amount, channel, status, completed_time等）
3. t_wallet_transaction - 交易流水表（包含字段：id, transaction_no, from_user_id, to_user_id, amount, transaction_type, status, before_balance, after_balance等）

【需求】
创建一个管理员充值接口，实现以下功能：
1. 接口路径：POST /admin/wallet/recharge
2. 请求参数：userId（用户ID）、amount（充值金额）、currency（币种，默认USDT）、remark（备注）
3. 业务逻辑：
   - 验证用户存在且状态正常
   - 验证用户钱包存在且状态正常
   - 生成订单号（格式：ORD+时间戳+随机数）和流水号（格式：TXN+时间戳+随机数）
   - 使用乐观锁更新钱包余额
   - 插入充值订单记录（type=1, channel="admin", status=2）
   - 插入交易流水记录（transaction_type=3, from_user_id=0, to_user_id=用户ID）
   - 整个操作在一个事务中完成
4. 返回数据：订单号、用户信息、充值金额、充值前后余额、充值时间

【文件要求】
请创建以下文件：
1. AdminRechargeDTO.java - 请求参数DTO（包含参数校验注解）
2. AdminRechargeVO.java - 响应数据VO
3. AdminWalletService.java - 服务接口
4. AdminWalletServiceImpl.java - 服务实现（包含详细的业务逻辑和异常处理）
5. AdminWalletController.java - 控制器（包含Swagger注解）

【代码规范】
- 使用Lombok的@Data、@RequiredArgsConstructor注解
- 使用Jakarta Validation进行参数校验
- Service方法使用@Transactional(rollbackFor = Exception.class)
- 异常抛出使用GlobalException
- 成功响应使用ResultUtils.success(data)
- Controller使用@Tag、@Operation添加Swagger文档
```

### 提示词2：创建查询接口

```
请根据以下需求，在Spring Boot项目中实现充值记录查询功能：

【项目信息】
- 框架：Spring Boot 3.x + MyBatis-Plus
- 包路径：com.bx.implatform
- 已存在的Service：WalletOrderService、UserService、WalletTransactionService

【需求】
创建一个充值记录查询接口，实现以下功能：
1. 接口路径：GET /admin/wallet/recharge-records
2. 查询参数：
   - page（页码，默认1）
   - size（每页数量，默认20，最大100）
   - userId（用户ID，可选）
   - userName（用户名/昵称，可选，模糊查询）
   - startTime（开始时间，可选）
   - endTime（结束时间，可选）
   - minAmount（最小金额，可选）
   - maxAmount（最大金额，可选）
   - channel（支付渠道，可选）
   - status（订单状态，可选）
3. 业务逻辑：
   - 查询t_wallet_order表，type=1（充值类型）
   - 关联im_user表获取用户名、昵称
   - 关联t_wallet_transaction表获取余额变动信息
   - 根据传入的筛选条件动态构建查询
   - 按创建时间倒序排列
   - 返回分页结果
4. 数据处理：
   - 将channel代码转换为中文名称（admin->后台充值，alipay->支付宝等）
   - 将status代码转换为中文名称（1->处理中，2->成功等）
5. 返回数据：分页对象（包含总数、页码、记录列表）

【文件要求】
1. AdminRechargeRecordVO.java - 充值记录VO
2. RechargeChannelEnum.java - 充值渠道枚举
3. 在AdminWalletService中添加查询方法
4. 在AdminWalletServiceImpl中实现查询逻辑
5. 在AdminWalletController中添加查询接口

【代码规范】
- 使用MyBatis-Plus的LambdaQueryWrapper构建查询条件
- 使用Page对象实现分页
- 优化性能，避免N+1查询问题（批量查询用户信息）
- 添加详细的Swagger注解
```

---

## 总结

本文档详细描述了Web后台用户充值功能的开发需求，包括：
1. 数据库表结构说明
2. 项目技术栈和代码规范
3. 两个核心接口的详细需求（充值接口、查询接口）
4. 业务逻辑要求和代码实现要点
5. 测试用例建议
6. 性能优化建议
7. 开发流程和AI提示词模板

开发者可以根据本文档的详细描述，快速理解需求并实现相应功能。如果使用AI辅助开发，可以直接使用文档中提供的提示词模板。

