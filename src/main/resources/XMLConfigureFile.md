## XML Configure File  

XML配置是Mybatis的核心，配置文件的顶层结构如下： 
- `<configuration>`(配置)  
    - `<properties>`(属性)  
    - `<settings>`(设置)  
    - `<typeAliases>`(类型别名)  
    - `<typeHandler>`(类型处理器)  
    - `<objectFactory>`(对象工厂)  
    - `<plugins>`(插件)
    - `<environments>`(环境配置)
        - `<environment>`(环境变量) 
            - `<transactionManager>`(事务管理器)  
            - `<dataSource>`(数据源)  
    - `<databaseIdProvider>`(数据库厂商标识)  
    - `<mapper>`(映射器)  
   
### `<properties>`属性   
- `<properties>`集中定义一些属性，这些属性可以在其他配置中被引用，**方便集中替换分散在其他配置中的属性**。  
- 在**`<property>`**子元素中可以设置属性。  
- 在**`resource`**属性可以引用在**java属性文件的属性**。  
- 在代码中通过`SqlSessionFactoryBuilder.build()`方法中传入属性值。  

属性的优先级：  
- 首先读取`<properties>`元素体内定义的属性。  
- 其次读取`<properties>`元素中`resource`属性指定的Java属性文件中属性，并**覆盖之前的同名属性**。  
- 最后读取**方法传递的属性**，并**覆盖之前的同名属性**。 

为占位符使用默认值：
- `:`字符使用默认值：可以为引用的属性添加默认值：`value=${username:root}`.  
- 开启该属性：需要在`<properties>`中开启该属性：`<property name="org.apache.ibatis.parsing.PropertyParser.enable-default-value" value="true">`。

设置分隔符：
- 如果开始开启了使用默认值，那么对于使用`:`字符的属性，语句来说，就需要**重新定义默认值的分隔符**。  
- 使用属性改变分隔分隔符：`<property name="org.apache.ibatis.paring.PropertyParser.default-value-separtor" value="?:">`.  

### `<settings>`设置
- 设置是mybatis的重要**调整设置**。
- 它会调整mybatis的行为。  
```xml
<settings><!--30个设置-->
        <setting name="cacheEnabled" value="true"/>
        <setting name="lazeLoadingEnabled" value="false"/>
        <setting name="aggressiveLazyLoading" value="false"/><!--在 3.4.1 及之前的版本中默认为 true-->
        <setting name="multipleResultSetsEnabled" value="false"/>
        <setting name="useColumnLabel" value="true"/>
        <setting name="userGeneratedKeys" value="false"/>
        <setting name="autoMappingBehavior" value="PARTIAL"/><!--NONE,PARTIAL, FULL-->
        <setting name="autoMappingUnknownColumnBehavior" value="WARNING"/><!--NONE,WARNING,FALING-->
        <setting name="defaultExecutorType" value="SIMPLE"/><!--SIMPLE,REUSE,BATCH-->
        <setting name="defaultStatementTimeout" value="25"/>
        <setting name="defaultFetchSize" value="100"/>
        <setting name="defaultResultSetType" value="DEFAuLT"/><!--FORWARD_ONLY,SCROLL_SENSITIVE,SCROLL_INSENSITIVE,DEFAULT-->
        <setting name="safeRowBoundsEnabled" value="false"/>
        <setting name="safeResultHandlerEnabled" value="true"/>
        <setting name="mapUnderscoreToCamelCase" value="false"/>
        <setting name="localCacheScope" value="SESSION"/><!--SESSION,STATEMENT-->
        <setting name="jdbcTypeForNull" value="OTHER"/><!--NULL,VARCHAR,OTHER-->
        <setting name="lazyLoadTriggerMethods" value="equals,clone,hasCode,toString"/><!--逗号分隔的方法列表-->
        <setting name="defaultScriptingLanguage" value="org.apache.ibatis.scripting.xmltags.XMLLanguageDriver"/><!--类型别名或完全限定名-->
        <setting name="callSettersOnNulls" value="false"/>
        <setting name="returnInstanceForEmptyRow" value="false"/>
        <setting name="logPrefix" value=""/><!--任何字符串-->
        <setting name="logImpl" value="NO_LOGGING"/><!--SLF4J,LOG4J,LOG4J2,JDK_LOGGING,COMMONS_LOGGING,STDOUT_LOGGING,NO_LOGGING-->
        <setting name="proxyFactory" value="JAVASSIST"/><!--CGLIB,JAVASSIST-->
        <setting name="vfsImpl" value=""/><!--自定义 VFS 的实现的类全限定名，以逗号分隔。-->
        <setting name="userActualParamName" value="true"/>
        <setting name="configurationFactory" value=""/><!--类型别名或完全限定名-->
        <setting name="shrinkWhitespacesInSql" value="false"/>
        <setting name="defaultSqlProviderType" value=""/><!--类型别名或限定名-->
    </settings>
```
### `<TypeAliases>`类型别名  
- **设置别名**：类型别名可为 Java 类型**设置一个缩写名字**。 它仅用于 XML 配置，意在降低冗余的全限定类名书写。
- **指定包名**：也可以指定一个包名，MyBatis 会在包名下面搜索需要的 Java Bean。默认使用Bean的**首字母小写**的非限定名来作为别名。  
- **`@Alias("newName")`**：指定包名的请况，为包内的类**提供特定的别名**，而不是使用默认的别名。  
```xml
<typeAliases>
    <typeAlias alias="Author" type="domain.blog.Author"/>
    <package name="domain.blog"/>
</typeAliases>
```
```java
@Alias("author")
public class Author{
}
```

### `<TypeHandlers>`  
- **转换为Java类型**：mybatis在设置预处理语句中的参数或从结果中取出一个值时，都会**使用类型处理器**将取到的值以合适的方式**转换成Java类型**。 
- **默认的类型处理器**：mybatis提供了一些默认的类型处理器。  
- **自定义类型处理器**：可以重写已有的类型处理器或创建自己的类型处理器来**处理不支持的或非标准的类型**。
    - **实现`TypeHandler`接口**：实现`org.apache.ibatis.type.TypeHandler`接口，或继承一个类。  
    - **映射到一个JDBC类型**：并且（可选地）将它映射到一个JDBC类型。  
```xml
     <typeHandlers>
            <typeHandler handler="pers.mortal.learn.mybatis.xmlconfigure.ExampleTypeHandler"/>
     </typeHandlers>
```
```java
@MappedJdbcTypes(JdbcType.VARCHAR)
public class ExampleTypeHandler extends BaseTypeHandler<String> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)throws SQLException {
        ps.setString(i, parameter);
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException{
        return rs.getString(columnName);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex)throws SQLException{
       return  rs.getString(columnIndex);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex)throws SQLException {
        return cs.getString(columnIndex);
    }
}   
```

>使用上述的类型处理器将会覆盖已有的处理 Java String 类型的属性以及 VARCHAR 类型的参数和结果的类型处理器。 
>要注意 MyBatis 不会通过检测数据库元信息来决定使用哪种类型，所以你必须在参数和结果映射中指明字段是 VARCHAR 类型， 以使其能够绑定到正确的类型处理器上。
>这是因为 MyBatis 直到语句被执行时才清楚数据类型。  

设置Java类型：
- **通过泛型设置Java类型**：通过类型处理器的泛型，MyBatis 可以得知该类型处理器处理的 Java 类型，不过这种行为可以通过两种方法改变。  
- **`<typeHandler>`的`javaType`属性**：该属性指定关联的Java类型：`javaType="String"。  
- **`@MappedTypes`注解：在类型处理器的类上增加一个`@MappedTypes`注解**指定与其关联的 Java 类型列表**。 
>如果在 javaType 属性中也**同时指定，则注解上的配置将被忽略**。  

设置JDBC类型：
- **`<typeHandler>`的`jdbcType`属性：该属性指定关联的JDBC类型：`jdbcType="VARCHAR"`。  
- **`@MappedJdbcTypes`**注解：在类型处理器的类上增加一个 @MappedJdbcTypes 注解指定与其关联的 JDBC 类型列表。   
>如果在 jdbcType 属性中也**同时指定，则注解上的配置将被忽略**。  

类型处理器的使用：
- **已知Java类型，未知JDBC类型**：当在`ResultMap`中决定使用哪种类型处理器时，此时 Java 类型是已知的（从结果类型中获得），但是 JDBC 类型是未知的。 
- **选择类型处理器**：因此 Mybatis 使用`javaType=[Java 类型], jdbcType=null`的组合来选择一个类型处理器。  
- **限制类型处理的作用范围**：这意味着使用`@MappedJdbcTypes`注解可以限制类型处理器的作用范围，并且可以确保，除非显式地设置，否则类型处理器在`ResultMap`中将不会生效。 
- **隐式地使用类型处理器**：如果希望能在 ResultMap 中隐式地使用类型处理器，那么设置`@MappedJdbcTypes`注解的**`includeNullJdbcType=true`**即可。 
>然而从 Mybatis 3.4.0 开始，如果某个 Java 类型只有一个注册的类型处理器，即使没有设置 includeNullJdbcType=true，那么这个类型处理器也会是 ResultMap 使用 Java 类型时的默认处理器。

查找类型处理器：
- **XML配置查找类型处理器**：可以通过设置`<typeHandlers>`的`让 MyBatis 帮你查找类型处理器
- **必须注解JDBC类型**：使用自动发现功能的时候，只能通过注解方式来指定 JDBC 的类型。  
```xml
 <typeHandlers>
    <package name="pers.mortal.learn.mybatis.xmlconfigure"/>
 </typeHandlers>
```

泛型类型处理器：  
- 你可以创建能够**处理多个类**的泛型类型处理器。
- 为了使用泛型类型处理器， 需要增加一个**接受该类的 class 作为参数的构造器**，这样 MyBatis 会在构造一个类型处理器实例的时候**传入一个具体的类**。  
- `EnumTypeHandler`和`EnumOrdinalTypeHandler`都是泛型类型处理器。  
```java
public class GenericTypeHandler<E extends MyObjct> extends BaseTypeHandler<E>{
    private Class<E> type;
    public GenericTypeHandler(Class<E> type){
        if(null == type) throw new IllegalArgumentException("Type argument cannot be null");
        this.type = type;
    }
}
```

### 对象工厂  
- **实例化工作**： 每次MyBatis**创建结果的新实例**时，它会使用一个对象工厂实例来完成实例化工作。  
- **默认实例化**： 默认的对象工厂需要做的**仅仅是实例化目标类**，要么通过默认无参构造方法，要么通过存在的参数映射来调用带有参数的构造方法。  
- **覆盖默认行为**： 如果想覆盖对象工厂的默认行为，可以通过创建自己的对象工厂来实现。  
    - 创造实例：ObjectFactory 接口很简单，它包含两个创建实例用的方法，一个是处理默认无参构造方法的，另外一个是处理带参数的构造方法的。 
    - **`setProperties`**方法：另外，**`setProperties`**方法可以被用来配置ObjectFactory，在初始化你的 ObjectFactory 实例后， 
    - `<objectFactory>`的**`<property>`**：objectFactory元素体中定义的**属性会被传递给`setProperties`**方法。
```xml
<objectFactory type="pers.mortal.learn.mybatis.xmlconfigure.ExampleObjectFactory">
        <property name="someProperty" value="100"/>
 </objectFactory>
```
```java
public class ExampleObjectFactory extends DefaultObjectFactory {

    @Override
    public <T> T create(Class<T> type) {
        return super.create(type);
    }

    @Override
    public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        return super.create(type, constructorArgTypes, constructorArgs);
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
    }

    @Override
    public <T> boolean isCollection(Class<T> type) {
        return Collection.class.isAssignableFrom(type);
    }
}
```  

### 插件  
- **拦截方法调用**：MyBatis 允许你在映射语句执行过程中的某一点进行拦截调用。
- **默认允许拦截的方法**： 默认情况下，MyBatis 允许使用插件来拦截的方法调用包括：  
    - `Executro(update, query, flushStatements, commit, rollback, getTransaction, close, isClosed)`  
    - `ParameterHandler(getParameterObject, setParameters)`   
    - `ResultSetHandler(handleResultSets, handleOutputParameters)`  
    - `StatementHandler(prepare, parameterize, batch, update, query)`  
>这些类中方法的细节可以通过查看每个方法的签名来发现，或者直接查看 MyBatis 发行包中的源代码。 
>如果你想做的不仅仅是监控方法的调用，那么你最好相当了解要重写的方法的行为。 
>因为在试图修改或重写已有方法的行为时，很可能会破坏 MyBatis 的核心模块。 
>这些都是更底层的类和方法，所以使用插件的时候要特别当心。  

使用插件：  
- **实现`Interceptro`**接口：通过 MyBatis 提供的强大机制，使用插件是非常简单的，只需实现 Interceptor 接口。  
- **指定拦截方法**： 并指定想要拦截的方法签名即可。  
>下面的插件将会拦截在 Executor 实例中所有的 “update” 方法调用， 这里的 Executor 是负责执行底层映射语句的内部对象。
```xml
    <plugins>
        <plugin interceptor="pers.mortal.learn.mybatis.xmlconfigure.ExamplePlugin">
            <property name="someProperty" value="100"/>
        </plugin>
    </plugins>
```
```java
@Intercepts({
        @Signature(
        type = Executor.class,
        method = "update",
        args = {MappedStatement.class, Object.class}
        )
})
public class ExamplePlugin implements Interceptor {
    private Properties properties = new Properties();
    @Override
    public Object intercept(Invocation invocation) {
        // implement pre processing if need
        Object returnObject = invocation.proceed();
        // implement post processing if need
        return returnObject;
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
```

覆盖配置类:
- 除了用插件来修改 MyBatis 核心行为以外，还可以通过完全覆盖配置类来达到目的。
- 只需继承配置类后覆盖其中的某个方法，再把它传递到 SqlSessionFactoryBuilder.build(myConfig) 方法即可。
- 再次重申，这可能会极大影响 MyBatis 的行为，务请慎之又慎。

### 环境配置  
- **适应多种环境**：Mybatis可以配置成适应多种环境，有助于**将SQL映射应用于多种数据库**中。  
- **环境差异**：
    - 例如，开发、测试和生产环境需要有不同的配置；  
    - 或者想在具有相同 Schema 的多个生产数据库中使用相同的 SQL 映射。  
    - 还有许多类似的使用场景。  
- **单环境选择**：尽管可以配置多个环境，但**每个`SqlSessionFactory`实例只能选择一种环境**。  
>每个数据库对应一个 SqlSessionFactory 实例。

环境选择：  
- `SqlSessionFactoryBuilder`的**`bulid`方法可选参数：为了指定创建哪种环境，只要将**环境作为可选的参数传递**给 SqlSessionFactoryBuilder 即可。  
- **默认环境**：如果忽略了环境参数，那么将会加载默认环境。  
```
//指定环境
SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, environment);
SqlSessionFactory factroy = new SqlSessionFactoryBuilder().build(reader, environment, properties)
//默认环境
SqlSessionFactory factory = new SqlSessionFactoryBuilder().bulid(reader)
SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, properties)
```

`<encironments>`元素配置环境：
environments 元素定义了如何配置环境。
- `<enviroments>`元素的**`default`属性**：通过引用环境ID指定默认使用的环境。  
- **`<environment>`**子元素：定义一个环境。  
- **`<transactionManager`**子元素：通过**`type`属性**配置事务管理器。 
- **`<dataSource>`**元素：配置数据源。  
```xml
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="${driver}"/>
                <property name="url" value="${url}"/>
                <property name="username" value="${username}"/>
                <property name="password" value="${password}"/>
            </dataSource>
        </environment>
    </environments>
```

#### 事务管理器
- **`JDBC`**事务管理器：这个配置直接使用了**JDBC的提交和回滚**设施，依赖从数据源获的连接来管理事务作用域。  
- **`MANAGER`**事务管理器：这个配置几乎没做什么。它**从不提交或回滚**一个连接。，而是让**容器来管理事务的整个生命周期**（比如 JEE 应用服务器的上下文）。
    -  **默认关闭连接**： 默认情况下它会关闭连接。  
    - **`closeConnection`**属性：然而一些容器并不希望连接被关闭，因此需要将`closeConnection`属性设置为`false来**阻止默认的关闭行为**。
- **Spring覆盖管理器**：如果你正在使用`Spring + MyBatis`，则**没有必要配置事务管理器**，因为 Spring 模块会使用自带的管理器来覆盖前面的配置。
```xml
<transactionManager type="MANAGED">
    <property naem="closeConnection" value="false"/>
</transactionManager>
```

自定义事务处理：  
- 这两种事务管理器类型都不需要设置任何属性。它们其实是类型别名，换句话说，你可以用**`TransactionFactory`**接口实现类的全限定名或类型别名代替它们。
- 在事务管理器实例化后，所有在 XML 中配置的属性将会被传递给**`setProperties()`**方法。你的实现还需要创建一个**`Transaction`**接口的实现类，这个接口也很简单：
- 使用这两个接口，你可以完全自定义 MyBatis 对事务的处理。
```java
public interface TransactionFactory {
  default void setProperties(Properties props) { // 从 3.5.2 开始，该方法为默认方法
    // 空实现
  }
  Transaction newTransaction(Connection conn);
  Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit);
}
public interface Transaction {
  Connection getConnection() throws SQLException;
  void commit() throws SQLException;
  void rollback() throws SQLException;
  void close() throws SQLException;
  Integer getTimeout() throws SQLException;
}
```

#### 数据源（dataSource）
- 使用**JDBC数据源接口**：dataSource 元素使用标准的 JDBC 数据源接口来配置 JDBC 连接对象的资源。
- **延迟加载必须配置数据源**：大多数 MyBatis 应用程序会按示例中的例子来配置数据源。虽然数据源配置是可选的，但如果要启用延迟加载特性，就必须配置数据源。
- `<dataSource`元素的`type`属性：有三种内建的数据源类型（也就是 `type="[UNPOOLED|POOLED|JNDI]"`）：
    - `UNPOOLED`：这个数据源的实现会每次请求时打开和关闭连接,仅仅需要配置**5 种属性**。  
    - `POOLED`:这种数据源的实现利用 **“池”的概念** 将 JDBC 连接对象组织起来，避免了创建新的连接实例时所必需的初始化和认证时间。**在`UNPOOLED`属性的基础上还有若干属性**。
    - `JNDI` – 这个数据源实现是为了能在如 EJB 或应用服务器这类容器中使用，容器可以集中或在外部配置数据源，然后放置一个 JNDI 上下文的数据源引用。这种数据源配置**只需要两个属性**。

`UNPOOLED`数据源的属性： 
- **`drivers`**: 这是 JDBC 驱动的 Java 类全限定名（并不是 JDBC 驱动中可能包含的数据源类）。  
- **`url`**: 这是数据库的 JDBC URL 地址。  
- **`username`**: 登录数据库的用户名。  
- **`password`**: 登录数据库的密码。  
- **`defaultTransactionIsolationLevel`**: 默认的连接**事务隔离级别**。  
- **`defaultNetworkTimeout`**: 等待数据库操作完成的默认**网络超时**时间（单位：毫秒）。查看 java.sql.Connection#setNetworkTimeout() 的 API 文档以获取更多信息。
- **`driver.`前缀传递属性**：作为可选项，你也可以**传递属性给数据库驱动**。只需在属性名**加上`driver.`前缀**即可。
>例如：`driver.encoding=UTF8`。
>这将通过 `DriverManager.getConnection(url, driverProperties)`方法传递值为 UTF8 的 encoding 属性给数据库驱动。  

`POOLED`数据源的属性：
- **包括`UNPOOLED`属性**：除了上述提到 UNPOOLED 下的属性外，还有更多属性用来配置 POOLED 的数据源。  
- `poolMaximumActiveConnections`:在任意时间可存在的活动（正在使用）连接数量，默认值：10  
- `poolMaximumIdleConnections`:任意时间可能存在的空闲连接数。  
- `poolMaximumCheckoutTime`:被强制返回之前，池中连接被检出（checked out）时间，默认值：20000 毫秒（即 20 秒）  
- `poolTimeToWait`: 这是一个底层设置，如果获取连接花费了相当长的时间，连接池会打印状态日志并重新尝试获取一个连接（避免在误配置的情况下一直失败且不打印日志），默认值：20000 毫秒（即 20 秒）。  
- `poolMaximumLocalBadConnectionTolerance`:这是一个关于坏连接容忍度的底层设置， 作用于每一个尝试从缓存池获取连接的线程。 如果这个线程获取到的是一个坏的连接，那么这个数据源允许这个线程尝试重新获取一个新的连接，但是这个重新尝试的次数不应该超过 poolMaximumIdleConnections 与 poolMaximumLocalBadConnectionTolerance 之和。 默认值：3（新增于 3.4.5）  
- `poolPingQuery`: 发送到数据库的侦测查询，用来检验连接是否正常工作并准备接受请求。默认是“NO PING QUERY SET”，这会导致多数数据库驱动出错时返回恰当的错误消息。  
- `poolPingEnabled`:是否启用侦测查询。若开启，需要设置 poolPingQuery 属性为一个可执行的 SQL 语句（最好是一个速度非常快的 SQL 语句），默认值：false。  
- `poolPingConnectionsNotUsedFor`: 配置 poolPingQuery 的频率。可以被设置为和数据库连接超时时间一样，来避免不必要的侦测，默认值：0（即所有连接每一时刻都被侦测 — 当然仅当 poolPingEnabled 为 true 时适用）。  

`JNDI`数据源的属性：  
>这个数据源实现是为了能在如 EJB 或应用服务器这类容器中使用，容器可以集中或在外部配置数据源，然后放置一个 JNDI 上下文的数据源引用。这种数据源配置只需要两个属性：  
- `initial_context`：这个属性用来在 InitialContext 中寻找上下文（即，initialContext.lookup(initial_context)）。这是个可选属性，如果忽略，那么将会直接从 InitialContext 中寻找 data_source 属性。 
- `data_source`: 这是引用数据源实例位置的上下文路径。提供了 initial_context 配置时会在其返回的上下文中进行查找，没有提供时则直接在 InitialContext 中查找。 
- **`env.`前缀传递属性**：和其他数据源配置类似，可以通过添加**前缀“env.”直接把属性传递**给`InitialContext`。  
>比如：`env.encoding=UTF8`
>这就会在 InitialContext 实例化时往它的构造方法传递值为 UTF8 的 encoding 属性。  

自定义数据源：  
- **`DataSourceFactory`**接口：你可以通过实现接口`org.apache.ibatis.datasource.DataSourceFactory`来使用第三方数据源实现：
- **`UnpooledDataSourceFactory`**类：`org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory`可被用作父类来构建新的数据源适配器。  
    - 为了令其工作，记得在配置文件中为每个希望 MyBatis 调用的 setter 方法增加对应的属性。 
```java
public interface DataSourceFactory {
  void setProperties(Properties props);
  DataSource getDataSource();
}

public class C3P0DataSourceFactory extends UnpooledDataSourceFactory {

  public C3P0DataSourceFactory() {
    this.dataSource = new ComboPooledDataSource();
  }
}
```
```xml
<dataSource type="org.myproject.C3P0DataSourceFactory">
  <property name="driver" value="org.postgresql.Driver"/>
  <property name="url" value="jdbc:postgresql:mydb"/>
  <property name="username" value="postgres"/>
  <property name="password" value="root"/>
</dataSource>
```

### 数据库厂商标识  
- **`databaseId`**标识数据库厂商；MyBatis可以根据不同的数据库产商执行不同的语句。这种多厂商的支持是基于**映射语句中的`databaseId`**属性。  
- **加载的语句**：MyBatis会加载带有**匹配当前数据库`databaseId`**属性和**所有不带`databaseId`**属性的语句。  
- **启动支持多厂商特性**：在配置文件添加**`<databaseIdProvider type="DB_VENDOR" />`**。  
    - `databaseId`**值**：databaseIdProvider 对应的 DB_VENDOR 实现会将 databaseId 设置为**`DatabaseMetaData#getDatabaseProductName()`**返回的字符串。 
    - `databaseId`**值的别名**：由于通常情况下这些字符串都非常长，而且相同产品的不同版本会返回不同的值，你可能想通过设置属性别名来使其变短。  
 ```xml
  <databaseIdProvider type="DB_VENDOR">
         <property name="SQL Server" value="sqlserver"/>
         <property name="DB2" value="db2"/>
         <property name="Oracle" value="oracle"/>
     </databaseIdProvider>
```

自定义`DatabaseIdProvider`：  
- **`DatabaseIdProvider`**接口：你可以通过实现**接口`org.apache.ibatis.mapping.DatabaseIdProvider`**。  
- **注册**：**并在 mybatis-config.xml 中注册**来构建自己的`DatabaseIdProvider`。  

### 映射器  
可以使用**相对于类路径的资源引用**，**完全资源限定符（包括fild://）**，**类名**，**包名**指定映射器所在。  
```xml
<!-- 使用相对于类路径的资源引用 -->
<mappers>
  <mapper resource="org/mybatis/builder/AuthorMapper.xml"/>
  <mapper resource="org/mybatis/builder/BlogMapper.xml"/>
  <mapper resource="org/mybatis/builder/PostMapper.xml"/>
</mappers>
```
```xml
<!-- 使用完全限定资源定位符（URL） -->
<mappers>
  <mapper url="file:///var/mappers/AuthorMapper.xml"/>
  <mapper url="file:///var/mappers/BlogMapper.xml"/>
  <mapper url="file:///var/mappers/PostMapper.xml"/>
</mappers>
```
```xml
<!-- 使用映射器接口实现类的完全限定类名 -->
<mappers>
  <mapper class="org.mybatis.builder.AuthorMapper"/>
  <mapper class="org.mybatis.builder.BlogMapper"/>
  <mapper class="org.mybatis.builder.PostMapper"/>
</mappers>
```
```xml
<!-- 将包内的映射器接口实现全部注册为映射器 -->
<mappers>
  <package name="org.mybatis.builder"/>
</mappers>
```