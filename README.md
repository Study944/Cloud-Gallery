# Cloud Gallery 图片素材管理平台

Cloud Gallery 是一个支持**公共图库**、**私人空间**、**团队空间**、**AI 拓展**和**协同编辑**的图片素材管理平台。适用于个人、团队及企业对图片资源的高效管理与协作。

---

## 主要功能模块

### 1. 公共图库
- **功能**：所有注册用户可访问的图片资源库，便于素材共享与检索。
- **实现**：图片表（image）中 `space_id=0` 的图片即为公共图库图片，支持上传、浏览、标签检索、审核等功能。
- **权限**：普通用户可浏览，管理员可审核、删除。

### 2. 私人空间
- **功能**：每个用户拥有独立的图片空间，支持私密管理、个性化分类、容量和数量限制。
- **实现**：`space` 表中 `space_type=0`，与用户一对一绑定。支持空间等级（普通/VIP/SVIP），不同等级有不同容量和图片数量上限。
- **权限**：仅空间拥有者可管理和访问。

### 3. 团队空间
- **功能**：支持团队成员协作管理图片，分配不同角色和权限，适合项目协作。
- **实现**：
  - `space` 表中 `space_type=1`，为团队空间。
  - `user_space` 关系表维护团队成员与空间的多对多关系，并分配角色（owner/编辑/editor/只读/viewer）。
  - 角色权限通过 `role.json` 配置，支持 RBAC 灵活扩展。
- **权限**：
  - owner（管理员）：管理空间成员、图片的所有操作权限。
  - editor（编辑）：上传、编辑、删除图片。
  - viewer（只读）：仅浏览图片。

### 4. AI 拓展模块
- **功能**：集成 AI 能力，提升图片管理智能化水平。
  - 图片描述生成：自动为图片生成描述文本。
  - AI 生成图片：根据提示词生成图片并自动入库。
- **实现**：集成 Spring AI，封装在 `SpringAiManager`，通过 `ImageService` 调用。

### 5. 协同编辑模块
- **功能**：支持多用户实时协作编辑图片及其元数据，提升团队效率。
- **实现**：
  - 基于 WebSocket 实现实时通信，`WsImageEditHandle` 负责消息分发与同步。
  - 权限校验：仅团队空间成员可参与协同编辑，且需登录。
  - 支持用户加入/退出编辑、编辑内容广播、编辑锁等机制。

---

## 技术架构

- **后端框架**：Spring Boot
- **安全认证**：Sa-Token（支持多账号体系、空间级权限校验）
- **数据库**：MySQL（表结构见 `sql/table.sql`）
- **对象存储**：腾讯云 COS（图片文件存储与访问）
- **AI 能力**：Spring AI（图片描述、生成）
- **WebSocket**：实现实时协同编辑
- **ORM 框架**：MyBatis Plus
- **RBAC 权限**：基于 `role.json` 配置，支持灵活扩展

---

## 快速启动

1. **克隆项目**
   ```bash
   git clone https://github.com/your-org/Cloud-Gallery.git
   cd Cloud-Gallery
   ```

2. **配置数据库与对象存储**
   - 修改 `src/main/resources/application.yml`，配置数据库、COS 等相关信息。

3. **初始化数据库**
   - 执行 `sql/table.sql` 脚本，初始化数据库表结构。

4. **启动项目**
   - 使用 IDE 运行 `CloudGalleryApplication.java`，或命令行执行：
     ```bash
     mvn spring-boot:run
     ```

5. **访问接口**
   - 默认服务端口为 `8000`，可通过 Postman 或前端页面进行接口调试。

---

## 目录结构说明

```
src/
  main/
    java/com/cloudgallery/
      annotation/         // 注解定义（如角色校验）
      aop/                // 切面与拦截器
      common/             // 通用响应、工具类
      config/             // 配置类（如 Sa-Token、COS、WebSocket）
      constant/           // 常量
      controller/         // 控制器（接口层）
      exception/          // 全局异常处理
      manager/            // 业务管理（AI、权限、COS、WebSocket等）
      mapper/             // MyBatis 映射接口
      model/              // DTO/VO/Entity/枚举
      service/            // 业务服务接口与实现
    resources/
      application.yml     // 配置文件
      mapper/             // MyBatis 映射文件
      role.json           // 角色权限配置
      static/             // 静态资源
  test/                   // 测试代码
sql/                      // 数据库脚本
```

---

## 权限与角色说明

- **空间角色**：owner（管理员）、editor（编辑）、viewer（只读）
- **权限粒度**：图片浏览、上传、编辑、删除、空间成员管理等
- **配置方式**：`src/main/resources/role.json`，支持自定义扩展
- **权限校验**：基于 Sa-Token + 注解 + AOP 实现，支持空间级、图片级权限动态校验

---

## 贡献指南

1. Fork 本仓库并创建分支。
2. 提交代码前请确保通过所有单元测试。
3. 提交 Pull Request，并详细描述你的修改内容。

---

## 联系与支持

如有问题或建议，欢迎提交 Issue 或 PR。

---

如需英文版或更详细的接口文档，可补充说明！ 
