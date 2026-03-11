# AIShop 项目

这是一个基于 Spring Boot 3.3.4 + MyBatis-Plus + PostgreSQL 15 的后端基础项目。

## 🚀 快速启动 (推荐 - 环境一致性保障)

为了确保所有协作者的环境（JDK 17, Maven, PostgreSQL）完全一致，推荐使用 **Docker Compose** 一键启动。

### 1. 环境准备
确保你的本地环境已安装并启动：
- **Docker** & **Docker Desktop**

### 2. 一键启动项目
在项目根目录下运行：

```bash
docker-compose up --build
```

该命令将自动：
1. **构建应用镜像**：在容器内使用 JDK 17 进行编译和打包。
2. **启动数据库**：启动 PostgreSQL 15 容器并持久化数据。
3. **服务互联**：应用将自动连接到容器内的数据库。

### 3. 验证启动
项目启动后，访问以下地址验证：
- **API 文档 (Knife4j)**: [http://localhost:8080/doc.html](http://localhost:8080/doc.html)
- **数据库连接测试**: [http://localhost:8080/testdb](http://localhost:8080/testdb)

---

## 🛠️ 本地手动开发 (可选)

如果你需要在本地不通过 Docker 运行应用（例如使用 IDE 调试）：

1. **启动容器化数据库**: `docker-compose up -d postgres`
2. **本地环境**: 请确保已安装 **JDK 17** 和 **Maven**。
3. **运行**: `./mvnw spring-boot:run`

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

---

## ❓ 常见问题

### 1. 镜像拉取失败 (Image Not Found / Not Found)
**现象**: 报错 `docker.io/library/maven:xxx not found`。

**原因**: 该错误通常由于**网络连接 Docker Hub 受限**或加速器同步延迟导致，并非镜像不存在。

**解决方法**:
- **配置/更换加速器**: 请配置可靠的 Docker 镜像加速器。
- **手动拉取**: 尝试单独运行 `docker pull maven:3.9-eclipse-temurin-17`。
- **使用代理**: 确保 Docker Desktop 已配置正确的代理设置。

### 2. 容器名称冲突 (Conflict)
**现象**: `Error response from daemon: Conflict. The container name "/aishop-postgres" is already in use...`

**原因**: 本地已存在同名的容器。

**解决方法**: 运行命令强制删除旧容器：
```bash
docker rm -f aishop-postgres aishop-app
```
或者使用：
```bash
docker-compose down
```