# GameLive 游戏直播网站

> 一个基于 Vue3 + SpringBoot 前后端分离的游戏直播平台，支持 RTMP 推流、FLV 拉流播放、WebSocket 实时弹幕与后台管理。

## 📖 项目简介

GameLive 是一个软件工程课程设计项目，模拟主流游戏直播平台的核心业务：主播通过 RTMP 推流开播，观众在直播间观看直播、发送弹幕、点赞与打赏，平台管理员对用户、直播间和游戏分类进行审核与管理。

## ✨ 功能预览

### 游客
- 浏览直播列表与游戏分类
- 按游戏分类筛选直播间
- 搜索直播间
- 进入直播间观看直播回放

### 普通观众
- 注册、登录，修改个人信息
- 关注主播，查看粉丝列表
- 直播间实时观看
- 发送弹幕、点赞、赠送礼物打赏
- 查看主播信息与粉丝列表

### 主播
- 申请开播，系统自动生成 RTMP 推流地址
- 管理自己的直播间
- 直播间内禁言违规观众
- 直播回放查看

### 平台管理员
- 用户管理（查询、封禁、解封）
- 直播间审核与违规封禁
- 游戏分类管理（增删改查）
- 基础数据统计（用户数、直播间数、礼物/弹幕等）

## 🛠 技术栈

| 层级 | 技术 | 说明 |
| --- | --- | --- |
| 前端 | Vue 3 | 前端框架（组合式 API） |
| 前端 | Element Plus | UI 组件库 |
| 前端 | Axios | HTTP 请求封装 |
| 前端 | FLV.js | 直播流拉流播放 |
| 后端 | SpringBoot | 后端框架 |
| 后端 | MyBatis-Plus | ORM 持久层框架 |
| 实时通信 | WebSocket | 弹幕实时推送 |
| 数据库 | MySQL 8.0 | 业务数据存储 |
| 缓存 | Redis（可选） | 会话、热门数据缓存 |
| 直播方案 | RTMP 推流 + FLV 拉流 | OBS 等推流端推流，浏览器 FLV.js 播放 |

## 🧩 项目架构说明

```
┌──────────────────────────────────────────────┐
│                   浏览器端                     │
│  Vue3 + Element Plus + Axios + FLV.js        │
│  WebSocket 客户端（弹幕）                     │
└───────────────┬──────────────┬──────────────┘
                │ HTTP / REST   │ WebSocket
┌───────────────▼──────────────▼──────────────┐
│                  后端服务                     │
│        SpringBoot + MyBatis-Plus             │
│  用户模块 / 直播模块 / 互动模块 / 管理模块     │
└───────────────┬──────────────┬──────────────┘
                │              │
        ┌───────▼──────┐  ┌───▼────────┐
        │   MySQL 8.0   │  │  Redis(可选)│
        └───────────────┘  └────────────┘

推流链路：OBS / 推流端 ──RTMP──> 流媒体服务 ──FLV(HLS/HTTP-FLV)──> FLV.js 播放
```

- **前端**：单页应用，通过 Axios 调用后端 RESTful 接口，WebSocket 连接弹幕服务，FLV.js 解析直播流。
- **后端**：按业务划分为用户、直播、互动、管理四大模块，统一返回 JSON 结构。
- **数据层**：MyBatis-Plus 操作 MySQL；Redis 可选，用于登录态、热门直播间等缓存。
- **直播链路**：主播使用 OBS 等工具按系统生成的 RTMP 地址推流，前端通过 FLV.js 拉取可播放的 FLV 流。

## 📌 环境依赖

开发前请确保本机已安装以下软件：

| 软件 | 版本要求 | 说明 |
| --- | --- | --- |
| JDK | 17（或 SpringBoot 对应版本） | 后端运行环境 |
| Maven | 3.6+ | 后端依赖管理与构建 |
| Node.js | 16+（推荐 18 LTS） | 前端构建环境 |
| npm / pnpm | 随 Node 安装 | 前端包管理 |
| MySQL | 8.0 | 业务数据库 |
| Redis | 6.0+（可选） | 缓存，不启用可在配置中关闭 |
| OBS Studio | 任意稳定版本 | 本地推流测试工具 |

## 🚀 快速启动

### 1. 克隆仓库

```bash
git clone https://github.com/ikun-ai-liguan/GameLive.git
cd GameLive
```

### 2. 初始化数据库

详见 [数据库初始化说明](#-数据库初始化说明)。

### 3. 启动后端

进入后端目录，修改配置文件中的数据库、Redis 连接信息后启动：

```bash
# 进入后端模块目录（按实际目录名调整）
cd backend

# 编译并启动
mvn clean install
mvn spring-boot:run
```

后端默认端口：`8080`，接口基础路径示例：`http://localhost:8080/api`。

### 4. 启动前端

新开终端，进入前端目录安装依赖并启动：

```bash
# 进入前端模块目录（按实际目录名调整）
cd frontend

# 安装依赖
npm install

# 开发模式启动
npm run dev
```

前端默认端口：`5173`，浏览器访问终端输出的本地地址即可。

### 5. 推流测试

1. 在网站中以主播身份开播，获取系统生成的 RTMP 推流地址与流密钥。
2. 打开 OBS Studio，在「设置 → 推流」中填写该 RTMP 地址和密钥。
3. 开始推流后，回到对应直播间即可观看直播。

## 🗄 数据库初始化说明

1. 启动 MySQL 8.0，创建数据库（字符集建议 `utf8mb4`）：

```sql
CREATE DATABASE gamelive DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

2. 执行仓库中的 SQL 初始化脚本：

```
backend/
└── src/
    └── main/
        └── resources/
            └── sql/
                └── gamelive.sql
```

```bash
# 方式一：命令行导入
mysql -u root -p gamelive < backend/src/main/resources/sql/gamelive.sql
```

3. 在后端配置文件（`application.yml` / `application.properties`）中修改数据库连接：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/gamelive?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 你的数据库密码
```

> 若不使用 Redis，请在配置文件中关闭 Redis 相关自动配置，或注释掉对应依赖。

## 🖼 项目截图

> 截图待补充，可按以下位置放入：

```
frontend/
└── public/
    └── screenshots/
        ├── home.png          # 首页 / 直播列表
        ├── live-room.png     # 直播间页面
        ├── danmaku.png       # 弹幕互动
        ├── anchor.png        # 主播开播页
        └── admin.png         # 管理后台
```

在本小节按示例插入图片：

```markdown
![首页](frontend/public/screenshots/home.png)
```

## 🧠 项目难点 & 待优化点

### 已实现难点

- **实时弹幕**：基于 WebSocket 实现直播间消息的广播与接收，按直播间维度隔离消息。
- **直播推流与播放链路**：打通 RTMP 推流地址生成与 FLV.js 拉流播放的完整流程。
- **角色权限控制**：区分游客、观众、主播、管理员四类角色，按角色控制接口与页面访问。
- **关注 / 粉丝关系**：维护主播与观众之间的关注关系及粉丝列表。

### 待优化点

- 直播流目前依赖独立流媒体服务，可进一步集成 SRS / Nginx-RTMP 并补充 HLS 低延迟方案。
- 礼物打赏目前为模拟数据，可接入真实支付与流水对账。
- 弹幕高并发场景下可引入消息队列（如 Redis Pub/Sub 或 RabbitMQ）做异步削峰。
- 补充单元测试、接口文档（Swagger / Knife4j）与部署脚本（Docker Compose）。
- 增加直播回放转存、弹幕录像回放联动等功能。

## 📄 许可证

本项目基于 [MIT License](./LICENSE) 开源。

## ⚠️ 声明

本项目仅用于课程学习与技术交流，不用于任何商业用途。项目中出现的平台名称、界面、礼物及数据均为教学演示用途，如有侵权请联系删除。
