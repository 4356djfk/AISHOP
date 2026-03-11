# AIShop 项目

这是一个基于 Spring Boot 3.3.4 + MyBatis-Plus + PostgreSQL 15 的后端基础项目。

## 🚀 快速启动

### 1. 环境准备
确保你的本地环境已安装以下工具：
- **JDK 17+**
- **Docker & Docker Desktop** (用于启动数据库)
- **Maven 3.6+** (或直接使用项目自带的 `./mvnw`)

### 2. 启动数据库
项目使用 Docker Compose 来管理 PostgreSQL 容器，请在项目根目录下执行：

```bash
docker compose up -d
```

**数据库连接信息（默认）：**
- **Host**: `localhost`
- **Port**: `5432`
- **User**: `admin`
- **Password**: `123456`
- **Database**: `aishop_db`

### 3. 运行项目
可以直接使用 Maven 命令启动：

```bash
./mvnw spring-boot:run
```

项目启动后，访问以下地址验证连接：
[http://localhost:8080/testdb](http://localhost:8080/testdb)

---

## 🤝 协作流程 (Git 操作指南)

为了保证项目的代码整洁和协作顺畅，请遵循以下 Git 指令进行开发。

### 1. 获取最新代码
在开始工作前，先拉取远程主分支的最新内容：
```bash
git pull origin main --rebase
```

### 2. 提交你的更改
完成开发后，按以下步骤提交到 GitHub：

```bash
# 1. 查看修改的文件
git status

# 2. 将修改添加到暂存区
git add .

# 3. 提交代码并附带描述信息
git commit -m "你的功能描述 (例如: 增加用户登录接口)"

# 4. 推送到远程分支
git push origin main
```

> [!IMPORTANT]
> **关于 `pom.xml` 和 `application.yml`**：
> 请避免随意更改这两个文件的核心版本配置。如果需要增加依赖，请确保本地测试环境启动无误后再进行 push。

## 🛠️ 技术栈
- **框架**: Spring Boot 3.3.4
- **ORM**: MyBatis-Plus 3.5.5 (Spring Boot 3 Starter)
- **数据库**: PostgreSQL 15
- **文档**: Knife4j (Swagger 3)
- **其他**: Lombok