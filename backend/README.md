# CRS Backend

Java 21、Spring Boot 和 Maven 后端模块。

## 开发

```bash
./mvnw spring-boot:run
```

默认地址：`http://localhost:8080`

健康检查：`GET http://localhost:8080/api/health`

## 验证

```bash
./mvnw test
./mvnw package
```
