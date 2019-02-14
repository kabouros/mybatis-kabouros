# mybatis-kabouros

[![License](https://img.shields.io/:license-MIT-brightgreen.svg)](https://opensource.org/licenses/mit-license.php)

## Requirements

Java 8 or higher is required.

## How Do I Use It?
Create an entity:

```java
import com.kabouros.mybatis.api.annotation.Id;
import com.kabouros.mybatis.api.annotation.Table;

@Table(name="xxx")
public class User {

  @Id
  private Long id;
  private String name;
       
  //Getters and setters
}
```

Create an mapper interface:

```java
import com.kabouros.mybatis.api.mapper.BaseMapper;

public interface UserMapper extends BaseMapper<User,Long>  {

}
```

Create an service interface:

```java
import com.kabouros.mybatis.api.BaseService;

public interface UserService extends BaseService<User, Long>  {

}
```

Create an service impl:

```java
import com.kabouros.mybatis.spring.SpringBaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public UserServiceImpl extends SpringBaseServiceImpl<User,Long,UserMapper> implements UserService {

}
```

Write a application.yml:

```yml
server:
  port: 9090
  tomcat:
    uri-encoding: UTF-8
spring:
  application:
    name: mybatis-test
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    type: com.zaxxer.hikari.HikariDataSource
    url: jdbc:mysql://127.0.0.1:3306/mybatis_test?allowMultiQueries=true&useUnicode=true&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false
    username: xxx
    password: xxx
    hikari:
      minimum-idle: 1
      maximum-pool-size: 15
      auto-commit: true
      idle-timeout: 30000
      pool-name: CmsHikariCP
      max-lifetime: 1800000
      connection-timeout: 30000
      connection-test-query: SELECT 1
mybatis:
  mapper-locations:
  - classpath:mapper/*.xml
  type-aliases-package: xxx.entity
  configuration:
    call-setters-on-nulls: true
    map-underscore-to-camel-case: true
logging:
  level:
    xxx.mapper: debug
```

Write a spring-boot client:
```java

@RestController
@SpringBootApplication
@MapperScan("xxx.*")
public class Application {
	
  @Autowired
  private UserService userService; 
	 
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
	
  @GetMapping("/test")
  @Transactional(rollbackFor=Exception.class)
  public void testName(){
    User jiang = new User("jiang");
	User zhang = new User("zhang");
	userService.save(jiang,zhang);
  }
}
```

