## SSM RESTful API 项目指南

> 这个指南将详细介绍如何搭建一个基于 Spring、Spring MVC 和 MyBatis 的 RESTful API 项目。

这是一个典型的 Maven 项目结构：

```properties
ssm-project
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── kedaya
    │   │           ├── controller
    │   │           │   └── UserController.java
    │   │           ├── mapper
    │   │           │   └── UserMapper.java
    │   │           ├── service
    │   │           │   ├── UserService.java
    │   │           │   └── impl
    │   │           │       └── UserServiceImpl.java
    │   │           ├── pojo
    │   │           │   └── User.java
    │   │           └── task
    │   │               └── UserSyncTask.java  ←✅ 新增定时任务类
    │   ├── resources
    │   │   ├── spring
    │   │   │   ├── applicationContext.xml
    │   │   │   └── dispatcher-servlet.xml
    │   │   ├── mybatis-config.xml
    │   │   ├── jdbc.properties
    │   │   └── mapper
    │   │       └── UserMapper.xml
    │   └── webapp
    │       └── WEB-INF
    │           └── web.xml
    └── test
        └── java
            └── com
                └── kedaya
                    └── AppTest.java
```

### 步骤一：创建 Maven 项目并添加依赖

首先，使用 Maven 创建一个 Web 应用项目，并在 `pom.xml` 中添加所需的依赖。

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.kedaya</groupId>
    <artifactId>ssm-mvc-demo</artifactId>
    <packaging>war</packaging>
    <version>1.0-SNAPSHOT</version>
    <name>ssm-mvc-demo2 Maven Webapp</name>
    <url>http://maven.apache.org</url>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <spring.version>5.3.33</spring.version>
        <mybatis.version>3.5.15</mybatis.version>
        <mysql.version>8.0.33</mysql.version>
        <druid.version>1.2.20</druid.version>
        <servlet.version>4.0.1</servlet.version>
        <jsp.version>2.2</jsp.version>
        <junit.version>4.13.2</junit.version>
        <jackson.version>2.17.0</jackson.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aspects</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>${spring.version}</version>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>${mybatis.version}</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis-spring</artifactId>
            <version>2.1.2</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.version}</version>
        </dependency>

        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>${druid.version}</version>
        </dependency>

        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>${servlet.version}</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>javax.servlet.jsp</groupId>
            <artifactId>jsp-api</artifactId>
            <version>${jsp.version}</version>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>${jackson.version}</version>
        </dependency>

        <dependency>
            <groupId>com.github.pagehelper</groupId>
            <artifactId>pagehelper</artifactId>
            <version>5.3.2</version> <!-- 可替换为最新版 -->
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.4</version>
        </dependency>

        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <finalName>ssm-project</finalName>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>${maven.compiler.source}</source>
                    <target>${maven.compiler.target}</target>
                </configuration>
            </plugin>
        </plugins>
        <resources>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>false</filtering>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>false</filtering>
            </resource>
        </resources>
    </build>
</project>

```

### 步骤二：数据库配置

#### 创建数据库和表

在 MySQL 8.0 中创建数据库 `ssm_db` 和 `user` 表。

```sql
-- 创建数据库，如果不存在则创建，并指定字符集和排序规则
CREATE DATABASE IF NOT EXISTS ssm_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 切换到smm_db数据库
USE ssm_db;

-- 创建用户表
CREATE TABLE IF NOT EXISTS user (
    id INT PRIMARY KEY AUTO_INCREMENT, -- 用户ID，主键，自增长
    username VARCHAR(50) NOT NULL UNIQUE, -- 用户名，非空，唯一
    password VARCHAR(100) NOT NULL, -- 密码，非空
    email VARCHAR(100) -- 邮箱
);

-- 插入一些测试数据
INSERT INTO user (username, password, email) VALUES ('admin', '123456', 'admin@example.com');
INSERT INTO user (username, password, email) VALUES ('testuser', 'password123', 'test@example.com');
```

#### `jdbc.properties`

在 `src/main/resources/jdbc.properties` 中配置数据库连接信息。

```
# JDBC 驱动类名，MySQL 8.0 使用新的驱动类名 com.mysql.cj.jdbc.Driver
jdbc.driverClass=com.mysql.cj.jdbc.Driver
# JDBC 连接URL，包含数据库名、字符编码、时区等参数
jdbc.url=jdbc:mysql://localhost:3306/ssm_db?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true
# 数据库用户名
jdbc.username=root
# 数据库密码
jdbc.password=your_mysql_password
```

**请将 `your_mysql_password` 替换为你的 MySQL 实际密码。**

### 步骤三：Spring 配置

#### `applicationContext.xml` (Spring 核心配置)

在 `src/main/resources/spring/applicationContext.xml` 中配置数据源、MyBatis 会话工厂和事务管理器。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="
           http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
           http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd
           http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd">

    <!-- 扫描指定包，自动注册@Service、@Repository、@Component等注解标注的Bean -->
    <context:component-scan base-package="com.kedaya.service,com.kedaya.mapper"/>

    <!-- 读取外部的数据库配置文件 -->
    <context:property-placeholder location="classpath:jdbc.properties"/>

    <!-- 配置Druid数据源 -->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource" destroy-method="close">
        <!-- 数据库驱动 -->
        <property name="driverClassName" value="${jdbc.driverClass}"/>
        <!-- 数据库连接URL -->
        <property name="url" value="${jdbc.url}"/>
        <!-- 用户名 -->
        <property name="username" value="${jdbc.username}"/>
        <!-- 密码 -->
        <property name="password" value="${jdbc.password}"/>
        <!-- 初始化连接池大小 -->
        <property name="initialSize" value="5"/>
        <!-- 最小空闲连接数 -->
        <property name="minIdle" value="5"/>
        <!-- 最大连接数 -->
        <property name="maxActive" value="20"/>
        <!-- 获取连接等待超时时间 -->
        <property name="maxWait" value="60000"/>
        <!-- 间隔多久进行一次检测，检测需要关闭的空闲连接 -->
        <property name="timeBetweenEvictionRunsMillis" value="60000"/>
        <!-- 一个连接在连接池中最小生存时间 -->
        <property name="minEvictableIdleTimeMillis" value="300000"/>
        <!-- 检测连接是否有效的SQL -->
        <property name="validationQuery" value="SELECT 1"/>
        <!-- 空闲时检查连接是否可用 -->
        <property name="testWhileIdle" value="true"/>
        <!-- 申请连接时不检测连接是否可用（提升性能） -->
        <property name="testOnBorrow" value="false"/>
        <!-- 归还连接时不检测连接是否可用 -->
        <property name="testOnReturn" value="false"/>
        <!-- 是否缓存PreparedStatement -->
        <property name="poolPreparedStatements" value="true"/>
        <!-- 每个连接最多缓存多少个PreparedStatement -->
        <property name="maxPoolPreparedStatementPerConnectionSize" value="20"/>
    </bean>

    <!-- 配置MyBatis的SqlSessionFactory -->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <!-- 注入数据源 -->
        <property name="dataSource" ref="dataSource"/>
        <!-- 指定MyBatis的核心配置文件 -->
        <property name="configLocation" value="classpath:mybatis-config.xml"/>
        <!-- 指定Mapper XML文件所在位置 -->
        <property name="mapperLocations" value="classpath:mapper/*.xml"/>
    </bean>

    <!-- 扫描指定包下的接口，为其自动生成代理实现类 -->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <!-- 指定Mapper接口所在包 -->
        <property name="basePackage" value="com.kedaya.mapper"/>
        <!-- 关联使用的sqlSessionFactory -->
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
    </bean>

    <!-- 配置Spring的事务管理器，使用JDBC的数据源 -->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!-- 声明式事务配置 -->
    <tx:advice id="txAdvice" transaction-manager="transactionManager">
        <tx:attributes>
            <!-- 以下方法将开启事务，传播行为为REQUIRED（必须在事务中运行） -->
            <tx:method name="add*" propagation="REQUIRED"/>
            <tx:method name="save*" propagation="REQUIRED"/>
            <tx:method name="insert*" propagation="REQUIRED"/>
            <tx:method name="update*" propagation="REQUIRED"/>
            <tx:method name="delete*" propagation="REQUIRED"/>
            <!-- 以下方法为只读事务，适用于查询操作 -->
            <tx:method name="find*" propagation="SUPPORTS" read-only="true"/>
            <tx:method name="get*" propagation="SUPPORTS" read-only="true"/>
            <!-- 默认所有其他方法也启用事务 -->
            <tx:method name="*" propagation="REQUIRED"/>
        </tx:attributes>
    </tx:advice>

    <!-- AOP配置：将事务切入service层所有方法 -->
    <aop:config>
        <!-- 定义切入点，拦截service包下所有类的所有方法 -->
        <aop:pointcut id="txPointcut" expression="execution(* com.kedaya.service.*.*(..))"/>
        <!-- 将事务增强应用到上述切入点 -->
        <aop:advisor advice-ref="txAdvice" pointcut-ref="txPointcut"/>
    </aop:config>

</beans>
```

#### `dispatcher-servlet.xml` (SpringMVC 配置)

在 `src/main/resources/spring/dispatcher-servlet.xml` 中配置 SpringMVC 相关的注解驱动。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xsi:schemaLocation="
           http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
           http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc.xsd">

    <!-- 扫描Controller组件（@Controller 注解） -->
    <context:component-scan base-package="com.kedaya.controller"/>

    <!-- 启用默认的Servlet处理器，处理静态资源如HTML、JS、CSS、图片等 -->
    <mvc:default-servlet-handler/>

    <!-- 如果你有前后端分离的需求，可加上跨域支持 -->
    <mvc:cors>
        <mvc:mapping path="/**"
                     allowed-origins="*"
                     allowed-methods="GET,POST,PUT,DELETE,OPTIONS"
                     allowed-headers="*"/>
    </mvc:cors>

    <!-- 启用基于注解的Spring MVC功能，如 @RequestMapping、@ResponseBody等 -->
    <mvc:annotation-driven>
        <!-- 配置消息转换器，用于将Java对象转为JSON响应 -->
        <mvc:message-converters>
            <!-- 使用Jackson进行JSON转换 -->
            <bean class="org.springframework.http.converter.json.MappingJackson2HttpMessageConverter">
                <property name="supportedMediaTypes">
                    <list>
                        <!-- 支持application/json编码为UTF-8 -->
                        <value>application/json;charset=UTF-8</value>
                        <!-- 兼容一些前端请求方式为text/html时的返回 -->
                        <value>text/html;charset=UTF-8</value>
                    </list>
                </property>
            </bean>
        </mvc:message-converters>
    </mvc:annotation-driven>

</beans>
```

------

### 步骤四：MyBatis 配置

#### `mybatis-config.xml`

在 `src/main/resources/mybatis-config.xml` 中配置 MyBatis 全局设置。

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

    <!-- 设置区：配置核心行为 -->
    <settings>
        <!-- 开启驼峰命名自动映射 -->
        <setting name="mapUnderscoreToCamelCase" value="true"/>
        <!-- 日志实现（控制台输出） -->
        <setting name="logImpl" value="STDOUT_LOGGING"/>
        <!-- 启用懒加载 -->
        <setting name="lazyLoadingEnabled" value="true"/>
        <!-- 是否按需加载（默认全部加载） -->
        <setting name="aggressiveLazyLoading" value="false"/>
        <!-- 本地缓存范围（SESSION或STATEMENT） -->
        <setting name="localCacheScope" value="SESSION"/>
    </settings>

    <!-- 类型别名配置：简化Mapper XML里的类名 -->
    <typeAliases>
        <!-- 扫描实体包，类名即为别名 -->
        <package name="com.kedaya.entity"/>
    </typeAliases>

    <!-- 插件配置（分页、性能分析等） -->
    <plugins>
        <!-- PageHelper 插件（适配 MySQL） -->
        <plugin interceptor="com.github.pagehelper.PageInterceptor">
            <property name="helperDialect" value="mysql"/>
            <property name="reasonable" value="true"/>
            <property name="supportMethodsArguments" value="true"/>
            <property name="params" value="count=countSql"/>
        </plugin>
    </plugins>

    <!-- 全局二级缓存配置（默认关闭，可启用） -->
    <!--
    <cache>
        <property name="eviction" value="LRU"/>
        <property name="flushInterval" value="60000"/>
        <property name="size" value="512"/>
        <property name="readOnly" value="false"/>
    </cache>
    -->

    <!-- 可选：配置类型处理器（TypeHandler） -->
    <!--
    <typeHandlers>
        <package name="com.kedaya.config.typehandler"/>
    </typeHandlers>
    -->

</configuration>
```

### 步骤五：实体类（POJO）

#### `User.java`

```java
package com.kedaya.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author：CHENWEI
 * @Package：com.kedaya.entity
 * @Project：ssm-mvc-demo
 * @name：User
 * @Date：2025-06-21 23:22
 * @Filename：User
 */
@Data
public class User implements Serializable {
    private Integer id;       // 用户ID
    private String username;  // 用户名
    private String password;  // 密码
    private String email;     // 邮箱
}

```

### 步骤六：DAO 层（Mapper 接口和 XML）

#### `UserMapper.java`

```java
package com.kedaya.mapper;

import com.kedaya.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 用户数据访问接口，MyBatis会根据此接口及其对应的XML文件生成实现类
public interface UserMapper {
    /**
     * 根据用户ID查询用户信息
     *
     * @param id 用户ID
     * @return 对应的User对象，如果不存在则返回null
     */
    User selectUserById(@Param("id") Integer id);

    /**
     * 查询所有用户信息
     *
     * @return User对象列表
     */
    List<User> selectAllUsers();

    /**
     * 插入新用户
     *
     * @param user 待插入的User对象
     * @return 影响的行数，通常为1表示成功
     */
    int insertUser(User user);

    /**
     * 更新用户信息
     *
     * @param user 包含更新信息的User对象（id必须存在）
     * @return 影响的行数，通常为1表示成功
     */
    int updateUser(User user);

    /**
     * 根据用户ID删除用户
     *
     * @param id 待删除的用户ID
     * @return 影响的行数，通常为1表示成功
     */
    int deleteUserById(@Param("id") Integer id);
}
```

#### `UserMapper.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.kedaya.mapper.UserMapper">

    <resultMap id="UserResultMap" type="User">
        <id column="id" property="id"/>
        <result column="username" property="username"/>
        <result column="password" property="password"/>
        <result column="email" property="email"/>
    </resultMap>

    <select id="selectUserById" resultMap="UserResultMap">
        SELECT id, username, password, email FROM user WHERE id = #{id}
    </select>

    <select id="selectAllUsers" resultMap="UserResultMap">
        SELECT id, username, password, email
        FROM user
    </select>

    <insert id="insertUser" parameterType="User" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO user (username, password, email) VALUES (#{username}, #{password}, #{email})
    </insert>

    <update id="updateUser" parameterType="User">
        UPDATE user
        SET username = #{username},
        password = #{password},
        email = #{email}
        WHERE id = #{id}
    </update>

    <delete id="deleteUserById">
        DELETE FROM user WHERE id = #{id}
    </delete>

</mapper>
```

### 步骤七：Service 层

#### `UserService.java`

```java
package com.kedaya.service;

import com.kedaya.entity.User;

import java.util.List;

// UserService接口，定义用户业务逻辑操作
public interface UserService {
    /**
     * 根据用户ID获取用户信息
     *
     * @param id 用户ID
     * @return User对象
     */
    User getUserById(Integer id);

    /**
     * 获取所有用户列表
     *
     * @return User对象列表
     */
    List<User> getAllUsers();

    /**
     * 添加新用户
     *
     * @param user 待添加的用户对象
     * @return true表示添加成功，false表示失败
     */
    boolean addUser(User user);

    /**
     * 更新用户信息
     *
     * @param user 待更新的用户对象
     * @return true表示更新成功，false表示失败
     */
    boolean updateUser(User user);

    /**
     * 删除用户
     *
     * @param id 待删除的用户ID
     * @return true表示删除成功，false表示失败
     */
    boolean deleteUser(Integer id);
}

```

#### `UserServiceImpl.java`

```java
package com.kedaya.service.impl;

import com.kedaya.entity.User;
import com.kedaya.mapper.UserMapper;
import com.kedaya.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author：CHENWEI
 * @Package：com.kedaya.service.impl
 * @Project：ssm-mvc-demo
 * @name：UserServiceImpl
 * @Date：2025-06-21 23:31
 * @Filename：UserServiceImpl
 */
// @Service注解标记当前类为Spring的Service组件，Spring会自动创建其实例并管理
@Service
public class UserServiceImpl implements UserService {

    // @Autowired注解自动注入UserMapper实例，MyBatis-Spring会自动创建Mapper接口的实现类并注入
    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserById(Integer id) {
        // 调用Mapper层方法查询用户
        return userMapper.selectUserById(id);
    }

    @Override
    public List<User> getAllUsers() {
        // 调用Mapper层方法查询所有用户
        return userMapper.selectAllUsers();
    }

    @Override
    public boolean addUser(User user) {
        // 调用Mapper层方法插入用户，并判断是否成功（影响行数大于0）
        return userMapper.insertUser(user) > 0;
    }

    @Override
    public boolean updateUser(User user) {
        // 调用Mapper层方法更新用户，并判断是否成功
        return userMapper.updateUser(user) > 0;
    }

    @Override
    public boolean deleteUser(Integer id) {
        // 调用Mapper层方法删除用户，并判断是否成功
        return userMapper.deleteUserById(id) > 0;
    }
}

```

### 步骤八：Controller 层

#### `UserController.java`

```java
package com.kedaya.controller;

import com.kedaya.entity.User;
import com.kedaya.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author：CHENWEI
 * @Package：com.kedaya.controller
 * @Project：ssm-mvc-demo
 * @name：DemoController
 * @Date：2025-06-21 22:49
 * @Filename：UserController
 */
// @RestController 是 @Controller 和 @ResponseBody 的组合注解
// 表示该类的所有方法返回值都会直接作为HTTP响应体返回，通常是JSON或XML数据
@RestController
// @RequestMapping("/user") 定义了该控制器处理的所有请求的根路径
@RequestMapping("/user")
public class UserController {

    // @Autowired注解自动注入UserService实例
    @Autowired
    private UserService userService;

    // 处理GET请求，路径为/user/list
    // 返回所有用户列表的JSON数据
    @GetMapping("/list")
    public List<User> listUsers() {
        // 调用Service层方法获取所有用户列表
        return userService.getAllUsers();
    }

    // 处理GET请求，路径为/user/{id}，{id}是路径变量
    // @PathVariable("id") 将路径中的id值绑定到方法的id参数上
    // 返回单个用户信息的JSON数据
    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Integer id) {
        // 调用Service层方法根据ID查询用户
        return userService.getUserById(id);
    }

    // 处理POST请求，路径为/user/add
    // @RequestBody注解表示将HTTP请求体中的JSON或XML数据绑定到User对象上
    // 返回操作结果的JSON消息
    @PostMapping("/add")
    public String addUser(@RequestBody User user) {
        // 调用Service层方法添加用户
        if (userService.addUser(user)) {
            // 返回JSON格式的成功消息
            return "{\"msg\": \"User added successfully!\"}";
        }
        // 返回JSON格式的失败消息
        return "{\"msg\": \"Failed to add user!\"}";
    }

    // 处理PUT请求，路径为/user/update
    // 通常用于更新资源
    // 返回操作结果的JSON消息
    @PutMapping("/update")
    public String updateUser(@RequestBody User user) {
        // 调用Service层方法更新用户
        if (userService.updateUser(user)) {
            return "{\"msg\": \"User updated successfully!\"}";
        }
        return "{\"msg\": \"Failed to update user!\"}";
    }

    // 处理DELETE请求，路径为/user/delete/{id}
    // 通常用于删除资源
    // 返回操作结果的JSON消息
    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id) {
        // 调用Service层方法删除用户
        if (userService.deleteUser(id)) {
            return "{\"msg\": \"User deleted successfully!\"}";
        }
        return "{\"msg\": \"Failed to delete user!\"}";
    }
}

```

### 步骤九：Web 配置

#### `web.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                             http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">

    <!-- 启动 Spring 容器的监听器，加载 applicationContext.xml -->
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>

    <!-- 指定 Spring 主配置文件的路径（classpath 下） -->
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:spring/applicationContext.xml</param-value>
    </context-param>

    <!-- 配置 Spring MVC 的 DispatcherServlet，核心前端控制器 -->
    <servlet>
        <servlet-name>dispatcherServlet</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>

        <!-- 指定 DispatcherServlet 的 Spring MVC 配置文件路径 -->
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:spring/dispatcher-servlet.xml</param-value>
        </init-param>

        <!-- 在服务器启动时加载 DispatcherServlet，数字越小越早加载 -->
        <load-on-startup>1</load-on-startup>
    </servlet>

    <!-- 将所有请求交给 DispatcherServlet 处理（除了静态资源） -->
    <servlet-mapping>
        <servlet-name>dispatcherServlet</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>

    <!-- 配置编码过滤器，防止中文乱码 -->
    <filter>
        <filter-name>characterEncodingFilter</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>

        <!-- 设置编码方式为 UTF-8 -->
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>

        <!-- 强制请求和响应都使用该编码 -->
        <init-param>
            <param-name>forceEncoding</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>

    <!-- 对所有请求应用编码过滤器 -->
    <filter-mapping>
        <filter-name>characterEncodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

</web-app>
```

### 运行项目

1. **修改数据库密码：** 确保 `jdbc.properties` 中的 `jdbc.password` 已经替换为你的 MySQL 实际密码。

2. **清理和构建项目：** 在项目根目录下打开命令行，运行 `mvn clean install`。

3. 部署到Tomcat：

    - 将生成的 `ssm-project.war` 文件（位于 `target` 目录下）拷贝到 Tomcat 的 `webapps` 目录下。
    - 启动 Tomcat。

4. 测试API：

   由于现在是 RESTful API，你需要使用

   Postman、Insomnia

   或其他 API 测试工具进行测试。

    - **GET 所有用户：** `http://localhost:8080/ssm-project/user/list`

    - **GET 单个用户：** `http://localhost:8080/ssm-project/user/1` (将 `1` 替换为实际用户ID)

    - POST 添加用户：

      ```url
      http://localhost:8080/ssm-project/user/add
      ```

        - 请求体 (Raw JSON):

          ```json
          {
              "username": "newuser",
              "password": "newpassword",
              "email": "newuser@example.com"
          }
          ```

    - PUT 更新用户：

      ```url
      http://localhost:8080/ssm-project/user/update
      ```

        - 请求体 (Raw JSON):

          ```json
          {
              "id": 1,
              "username": "admin_updated",
              "password": "new_admin_pass",
              "email": "admin_updated@example.com"
          }
          ```

    - **DELETE 用户：** `http://localhost:8080/ssm-project/user/delete/2` (将 `2` 替换为实际用户ID)

好的，以下是你现有 SSM 项目中 **集成 Spring 定时任务功能的完整流程整理**，你可以直接复制到你的 Spring+SpringMVC+MyBatis.md 文档中相应章节。



------





## **🌟 集成定时任务功能（Spring Task）**

> Spring 提供原生 @Scheduled 注解支持定时任务，无需额外依赖，配置简单、可读性强，适用于绝大多数后台定时逻辑场景。

### **✅ 步骤一：添加 XML 配置**

#### **修改**

#### **applicationContext.xml**

#### **，启用定时任务功能**

```xml
<beans xmlns:task="http://www.springframework.org/schema/task"
       xsi:schemaLocation="http://www.springframework.org/schema/task
                           http://www.springframework.org/schema/task/spring-task.xsd">

    <!-- 启用 @Scheduled 注解支持 -->
    <task:annotation-driven/>

    <!-- 添加扫描定时任务类所在包 -->
    <context:component-scan base-package="com.kedaya.task"/>
</beans>
```

> ⚠️ 如果已有 <context:component-scan>，确保包含了定时任务所在的包，例如：

```xml
<context:component-scan base-package="com.kedaya.service,com.kedaya.mapper,com.kedaya.task"/>
```



### **✅ 步骤二：创建定时任务类**

在 `com.kedaya.task` 包下创建一个任务类：

```java
package com.kedaya.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserSyncTask {

    // 每10秒执行一次
    @Scheduled(cron = "*/10 * * * * ?")
    public void sync() {
        System.out.println("【定时任务执行】当前时间：" + LocalDateTime.now());
    }
}
```

当然可以！以下是为你的 SSM 系统日志集成 专门准备的 Markdown 文档段落，结构清晰、注释完整，可直接合并进你的主文档 Spring+SpringMVC+MyBatis.md：



------





## **📘 日志系统集成（SLF4J + Logback）**

> 为了便于调试、监控、问题追踪，建议在整个系统中统一使用日志框架进行输出和管理。我们使用 Spring 官方推荐组合：SLF4J + Logback。

### **✅ 目标**

- 统一日志输出入口：controller、service、mapper、task 全覆盖
- 控制台 + 文件日志双通道输出
- 支持按包设置日志级别（如调试定时任务或 SQL）
- 支持日志文件滚动（按天）
- 可扩展对接 ELK、钉钉告警等系统

### **🧱 步骤一：添加日志依赖（Maven）**

在 pom.xml 中添加以下依赖：

```xml
<!-- 日志门面接口 -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>1.7.36</version>
</dependency>

<!-- 日志实现：Logback -->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.2.11</version>
</dependency>
```

### **📄 步骤二：添加**

### **logback.xml**

### **配置文件**

在 src/main/resources 下新建 logback.xml：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <!-- 定义日志文件存储路径，使用相对路径 logs -->
    <property name="LOG_PATH" value="/Users/ityoung/Documents/ProjectDemo/ssm-mvc-demo/logs"/>

    <!-- 控制台输出配置 -->
    <appender name="console" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %highlight(%-5level) %cyan(%logger{36}) - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 文件输出配置：基于时间 + 文件大小滚动 -->
    <appender name="file" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 主日志文件位置 -->
        <file>${LOG_PATH}/ssm.log</file>
        <!-- 滚动策略：时间 + 大小 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!-- 日志文件名称格式 -->
            <fileNamePattern>${LOG_PATH}/ssm.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <!-- 单个日志文件最大大小 -->
            <maxFileSize>10MB</maxFileSize>
            <!-- 最多保留 7 天日志 -->
            <maxHistory>7</maxHistory>
            <!-- 总大小上限（可选） -->
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 根日志配置 -->
    <root level="INFO">
        <appender-ref ref="console"/>
        <appender-ref ref="file"/>
    </root>

    <!-- 可根据包结构设定更细的日志级别 -->
    <logger name="com.kedaya.controller" level="INFO"/>
    <logger name="com.kedaya.service" level="DEBUG"/>
    <logger name="com.kedaya.mapper" level="DEBUG"/>
    <logger name="com.kedaya.task" level="DEBUG"/>

</configuration>
```

### **📦 步骤三：在代码中使用日志**

**推荐方式：使用 SLF4J 接口统一记录日志**

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public boolean addUser(User user) {
        logger.info("添加用户: {}", user.getUsername());
        try {
            return userMapper.insertUser(user) > 0;
        } catch (Exception e) {
            logger.error("添加用户失败", e);
            return false;
        }
    }
}
```

### **🛠 日志级别说明**

| **等级** | **用途举例**                 |
| -------- | ---------------------------- |
| TRACE    | 极度详细，一般不用           |
| DEBUG    | 详细调试日志（开发阶段开启） |
| INFO     | 正常操作日志，如“添加成功”   |
| WARN     | 非致命异常，如“用户已存在”   |
| ERROR    | 程序错误、异常栈等           |

### **📁 日志文件说明**

- 控制台日志实时输出
- 日志文件位于：logs/ssm.2025-06-22.log（按天滚动）
- 保留 7 天历史日志，自动清理

### **🔮 拓展建议（前瞻性）**

- ✅ 集成 Logstash / ELK：集中日志收集和分析
- ✅ 分环境配置：dev 环境开启 DEBUG，prod 环境仅 INFO
- ✅ 使用 MDC 记录 TraceID 实现链路跟踪
- ✅ 整合钉钉/飞书日志告警推送

## 🌐 多环境日志配置方案（基于 -Dlogback.configurationFile）

> 你现在已经拥有了两个 Logback 日志配置文件（一个用于开发环境，一个用于生产环境），接下来只需要通过 JVM 启动参数 -Dlogback.configurationFile=xxx.xml 来控制**不同环境加载不同的日志配置**即可。
>
> 下面是完整方案的整理说明

### **✅ 一、创建多套日志配置文件**

将以下两个文件放入 src/main/resources/ 目录下：

#### logback-dev.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="true">

    <!-- 定义日志文件存储路径，使用相对路径 logs -->
    <property name="LOG_PATH" value="/Users/ityoung/Documents/ProjectDemo/ssm-mvc-demo/logs"/>

    <!-- 控制台输出配置 -->
    <appender name="console" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %highlight(%-5level) %cyan(%logger{36}) - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 开发环境配置 -->
    <root level="DEBUG">
        <appender-ref ref="console"/>
    </root>

    <logger name="com.kedaya.controller" level="INFO"/>
    <logger name="com.kedaya.service" level="DEBUG"/>
    <logger name="com.kedaya.mapper" level="DEBUG"/>
    <logger name="com.kedaya.task" level="DEBUG"/>


</configuration>
```

#### logback-prod.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="true">

    <!-- 定义日志文件存储路径，使用相对路径 logs -->
    <property name="LOG_PATH" value="/Users/ityoung/Documents/ProjectDemo/ssm-mvc-demo/logs"/>

    <!-- 文件输出配置：基于时间 + 文件大小滚动 -->
    <appender name="file" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 主日志文件位置 -->
        <file>${LOG_PATH}/ssm.log</file>
        <!-- 滚动策略：时间 + 大小 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!-- 日志文件名称格式 -->
            <fileNamePattern>${LOG_PATH}/ssm.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <!-- 单个日志文件最大大小 -->
            <maxFileSize>10MB</maxFileSize>
            <!-- 最多保留 7 天日志 -->
            <maxHistory>7</maxHistory>
            <!-- 总大小上限（可选） -->
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 生产环境配置 -->
    <root level="INFO">
        <appender-ref ref="file"/>
    </root>

    <logger name="com.kedaya.controller" level="INFO"/>
    <logger name="com.kedaya.service" level="INFO"/>
    <logger name="com.kedaya.mapper" level="INFO"/>
    <logger name="com.kedaya.task" level="INFO"/>


</configuration>
```

> ☑️ **建议统一只配置 com.kedaya 这个包级别日志**，而不要细分 controller/service/mapper，方便集中调控。

### **✅ 二、使用 JVM 启动参数控制环境**

在启动项目时使用以下方式来指定加载哪个日志配置：

#### **开发环境：**

```sh
-Dlogback.configurationFile=src/main/resources/logback-dev.xml
```

#### **生产环境：**

```sh
-Dlogback.configurationFile=src/main/resources/logback-prod.xml
```

> ✅ 如果你使用 IDEA 或 Tomcat，可以设置在运行配置 / setenv.sh 中。

### **✅ 三、可选：自动识别环境变量（更高级）**

你也可以在 logback.xml 中读取系统环境变量或 profile 来动态选择：

```xml
<springProfile name="dev">
    <root level="DEBUG">
        <appender-ref ref="console"/>
    </root>
</springProfile>

<springProfile name="prod">
    <root level="INFO">
        <appender-ref ref="file"/>
    </root>
</springProfile>
```

然后设置：

```sh
-Dspring.profiles.active=prod
```

> ⚠️ 上述用法适用于 Spring Boot 项目；原生 SSM 项目使用 -Dlogback.configurationFile 更直接。