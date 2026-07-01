# 🏠 智慧租房管理系统

一个基于 **Vue 3 + Spring Boot** 的在线租房管理平台，支持租户、房东、管理员三种角色。

## ✨ 功能概览

| 角色 | 功能 |
|------|------|
| 🧑 **租户** | 注册登录、浏览房源、下单租房、租房攻略 |
| 🏠 **房东** | 房源管理（发布/编辑/下架）、订单管理 |
| 🛠️ **管理员** | 用户管理、房源管理、订单管理、数据概览 |

## 🛠️ 技术栈

### 前端
- **Vue 3** — 前端框架
- **Vite** — 构建工具
- **Element Plus** — UI 组件库
- **Vue Router** — 路由管理
- **Axios** — HTTP 请求

### 后端
- **Spring Boot 3.4** — 后端框架
- **MyBatis-Plus** — ORM 框架
- **MySQL** — 数据库
- **Spring Mail** — 邮件验证码
- **DeepSeek API** — AI 租房推荐

## 📁 项目结构

```
code/
├── frontend/                # 前端项目 (Vue 3 + Vite)
│   ├── src/
│   │   ├── api/             # API 接口
│   │   ├── router/          # 路由配置
│   │   └── views/           # 页面组件
│   │       ├── tenant/      # 租户端页面
│   │       ├── landlord/    # 房东端页面
│   │       └── admin/       # 管理员端页面
│   └── package.json
├── backend/                 # 后端项目 (Spring Boot)
│   └── src/main/java/com/smartrental/
│       ├── controller/      # 控制器
│       ├── service/         # 业务逻辑
│       ├── entity/          # 实体类
│       ├── mapper/          # 数据库映射
│       └── config/          # 配置类
├── 初始化数据库.py           # 数据库初始化脚本
├── 启动前端.py               # 一键启动前端
└── 启动后端.py               # 一键启动后端
```

## 🚀 快速开始

### 环境要求

- **JDK 21** 或以上
- **Maven** 3.x
- **Node.js** 18 或以上
- **MySQL** 8.0 或以上

### 1. 初始化数据库

确保 MySQL 已启动，密码为 `123456`（可在脚本中修改），然后运行：

```bash
python 初始化数据库.py
```

### 2. 启动后端

```bash
python 启动后端.py
```

后端运行在：**http://localhost:8080**

### 3. 启动前端

```bash
python 启动前端.py
```

前端运行在：**http://localhost:3000**

### 4. 访问系统

打开浏览器访问 http://localhost:3000 即可使用。

## 📝 数据库信息

- 数据库名：`smart_rental`
- 默认账号密码在 `backend/src/main/resources/db/init.sql` 中查看

## 📄 License

本项目仅供学习交流使用。
