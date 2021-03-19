## Java API  

- 主要接口`SqlSession`： 使用MyBatis的主要Java接口时`SqlSession`。   
- 工厂对象`SqlSessionFactory`：`SqlSession`对象由`SqlSessionFactory`构建。   
- 创建工厂`SqlSessionFactoryBuilder`： `SqlSessionFactory`由`SqlSessionFactoryBuilder`构建，它从**XML、注解、配置代码**来构建`SqlSessionFactory`。  

> 依赖注入框架内： 
> 依赖注入：当MyBatis与一些依赖注入框架(Spring|Guice)搭配使用时，SqlSession将被依赖注入框架创建并注入。  
> 无需构建：所以不需要SqlSessionFactory或SqlSessionFactoryBuilder。  

### `SqlSessionFactoryBuilder`   

```java
public class SqlSessionFactoryBuilder{
    public SqlSessionFactory build(InputStream inputStream){}    //接受一个指向XML配置文件的InputStream实例，获取配置信息构建SqlSessionFactory，使用默认环境。
    public SqlSessionFactory build(InputStream inputStream, String environment){}   //使用指定环境对应的配置。 指定无效环境会收到错误。   
    public SqlSessionFactory build(InputStream inputStream, Properties properties){}    //加载properties中的属性，并在配置中提供使用。可以用${propName}形式引用这些配置值。  
    public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties){}    //同时指定环境，加载配置。
    public SqlSessionFactory build(Configuration configuration);    //使用代表MyBatis配置的Configuration类实例，配置SqlSessionFactory.                                         
}
```

#### 属性加载优先级 
1. 加载**`<properties>`定义**的属性：首先，读取在`<properties>`元素体中指定的属性。  
2. 加载**`<preperties>`引用**的属性：其次，读取在`<properties>`元素的类路径`resource`或`url`指定属性，且会**覆盖已经指定了的重复属性**。  
3. 加载**方法参数传递**的属性：       最后，读取作为方法参数传递的属性，且会**覆盖已经**从`properties`元素和`reource`或`url`属性中**加载了的重复属性**。  

#### `Resources`工具类  
`Resources`工具类加载资源：`org.apache.ibatis.io`包中的`Resources`类会帮助你从**类路径**下、**文件系统**或一个**`web URL`**中加载资源文件。  
```java
package org.apache.ibatis;
public class Resource{
    URL getResourceURL(String resource){}
    URL getResourceURL(ClassLoader loader, String resource){}
    InputStream getResourceAsStream(String resource){}
    InputStream getResourceAsStream(ClassLoader loader, String resource){}
    Properties getResourceAsProperties(String resource){}
    Properties getResourceAsProperties(ClassLoader loader, String resource){}
    Reader getResourceAsReader(String resource){}
    Reader getResourceAsReader(ClassLoader loader, String resource){}
    File getResourceAsFile(String resource){}
    File getResourceAsFile(ClassLoader loader, String resource){}
    InputStream getUrlAsStream(String urlString){}
    Reader getUrlAsReader(String urlString){}
    Properties getUrlAsProperties(String urlString){}
    Class classForName(String className){}
}
```

#### `Configuration`配置类  
- `Configuration`类包含了对一个`SqlSessionFactory`实例你可能关心的所有内容。  
- 在检查配置时，Configuration 类很有用，它允许你查找和操纵 SQL 映射（但当应用开始接收请求时不推荐使用）。   
- 你之前学习过的所有配置开关都存在于 Configuration 类，只不过它们是以 Java API 形式暴露的。  

#### 示例
```xml
<!--环境配置-->
<configuration>
    <environments default="development">
      <environment id="development">
        <transactionManager type="JDBC"/>
        <dataSource type="POOLED">
            <!--...-->
        </dataSource>
      </environment>
      <environment id="production">
        <transactionManager type="MANAGED"/>
        <dataSource type="JNDI">
             <!--...-->
        </dataSource>
      </environment>
    </environments>
</configuration>
```
```java
//使用XML配置创建SqlSessionFactory
public class Example{
    public static void main(String[] args){
        String resource = "org/mybatis/builder/mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(inputStream);
    }                
}
```
```java
//使用Configuration配置类创建SqlSessionFactory
public class Example{
    public static void main(String[] args){
        DataSource dataSource = BaseDataTest.createBlogDataSource();
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        
        Environment environment = new Environment("development", transactionFactory, dataSource);
        
        Configuration configuration = new Configuration(environment);
        configuration.setLazyLoadingEnabled(true);
        configuration.setEnhancementEnabled(true);
        configuration.getTypeAliasRegistry().registerAlias(Blog.class);
        configuration.getTypeAliasRegistry().registerAlias(Post.class);
        configuration.getTypeAliasRegistry().registerAlias(Author.class);
        configuration.addMapper(BoundBlogMapper.class);
        configuration.addMapper(BoundAuthorMapper.class);
        
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(configuration); 
    }
}
```

### `SqlSessionFactory`  

**创建`SqlSession`关键点**：  
>SqlSessionFactory 有六个方法创建 SqlSession 实例。通常来说，当你选择其中一个方法时，你需要考虑以下几点。  
- 事务处理： 在session 作用域中使用**事务作用域**，还是使用**自动提交*。  
- 数据库连接：从**已配置的数据源**获取连接，还是使用**自己提供的连接**。  
- 语句执行： **复用`PreparedStatement`**和/或*批量更新语句**（包括插入语句和删除语句）吗。  
- 事务隔离级别： 

```java
public class SqlSessionFactory{
    SqlSession openSession(){}
    SqlSession openSession(boolean autoCommit){}
    SqlSession openSession(Connection connection){}
    SqlSession openSession(TransactionIsolationLevel level){}
    SqlSession openSession(ExecutorType executorType){}
    SqlSession openSession(ExecutorType executorType, boolean autoCommit){}
    SqlSession openSession(ExecutorType executorType, Connection connection){}
    SqlSession openSession(ExecutorType executorType, TransactionIsolationLevel level){}
    Configuration getConfiguration(){}  //返回一个 Configuration 实例，你可以在运行时使用它来检查 MyBatis 的配置。  
}
```

**`SqlSessionFactroy.build()`方法说明**： 
- 默认的`openSession()`: 默认的 openSession() 方法没有参数，它会创建具备如下特性的 SqlSession：  
    - 启动事务： 事务作用域将会开启（也就是不自动提交）。  
    - 使用配置连接： 将由当前环境配置的 DataSource 实例中获取 Connection 对象。  
    - 使用默认事务隔离级别： 事务隔离级别将会使用驱动或数据源的默认设置。  
    - 每次都执行新预处理语句： 预处理语句不会被复用，也不会批量处理更新。 
- `autoCommit`参数： 向`autoCommit`可选参数传递`true`值即可开启自动提交功能。  
- `Connection`参数： 若要使用自己的 Connection 实例，传递一个`Connection`实例给`connection`参数即可。 
- `TransactionIsolationLevel`枚举参数： MyBatis 使用了一个 Java 枚举包装器`TransactionIsolationLevel`表示事务隔离级别。  
    - `NONE`: 
    - `READ_UNCOMMITTED`: X锁（写锁），解决丢失修改
    - `READ_COMMITTED`:   X锁，S锁用完即释放，解决脏读。  
    - `REPEATABLE_READ`:  X锁，S锁事务结束在释放，解决不可重复读。 
    - `SERIALIZABLE`:     序列化调度，解决幻读。  
- `ExcutorType`枚举参数： 
     - `ExcutorType.NONE`: 该类型的执行器没有特别的行为。它为每个语句的执行创建一个新的预处理语句。  
     - `ExcutorType.REUSE`: 该类型的执行器会复用预处理语句。  
     - `ExcutorType.BATCH`:该类型的执行器会批量执行所有更新语句，如果 SELECT 在多个更新中间执行，将在必要时将多条更新语句分隔开来，以方便理解。  
     

### `SqlSession`  
> SqlSession 在 MyBatis 中是非常强大的一个类。它包含了所有执行语句、提交或回滚事务以及获取映射器实例的方法。  
> SqlSession 类的方法超过了 20 个，为了方便理解，我们将它们分成几种组别。  

#### 语句执行方法  
> 这些方法被用来执行定义在 SQL 映射 XML 文件中的 SELECT、INSERT、UPDATE 和 DELETE 语句。  
> 你可以通过名字快速了解它们的作用，每一方法都接受语句的 ID 以及参数对象，参数可以是原始类型（支持自动装箱或包装类）、JavaBean、POJO 或 Map。  
```java
//SqlSession的语句执行方法
public class SqlSession{
    //有参版本
    public <T> T selectOne(String statementID, Object parameter){}
    public <E> List<E> selectList(String statementID, Object parameter){}
    public <T> Cursor<T> selectCursor(String statementID, Object parameter){}
    public <K,V> Map<K,V> selectMap(String statementID, Object parameter, String mapKey){}
    public int insert(String statementID, Object parameter){}
    public int update(String statementID, Object parameter){}
    public int delete(String statementID, Object parameter){}
    //无参版本
    public <T> T selectOne(String statementID){}
    public <E> List<E> selectList(String statementID){}
    public <T> Cursor<T> selectCursor(String statementID){}
    public <K,V> Map<K,V> selectMap(String statementID, String mapKey){}
    public int insert(String statementID){}
    public int update(String statementID){}
    public int delete(String statementID){}
    //高级版本，限制返回行数范围，或是提供自定义结果处理逻辑
    public <E> List<E> selectList(String statementID, Object parameter, RowBounds rowBounds){}
    public <T> Cursor<T> selectCursor(String statementID, Object parameter, RowBounds rowBounds){}
    public <K,V> Map<K,V> selectMap(String statementID, Object parameter, RowBounds rowBounds){}
    public void select(String statementID, Object parameter, ResultHandler<T> handler){}
    public void select(String statementID, Object parameter, RowBounds rowBounds, ResultHandler<T> handler){}
}
```
```java
//RowBounds类型
public class RowBounds{
    private int offset;
    private int limit;
    public RowBounds(int offset, int limit){
        this.offset = offset;
        this.limit = limit;
    }
}
```
```java
//ResultHandler接口
package org.apache.ibatis.session;
interface ResultHandler<T>{
    void handleResult(ResultContext<? extends T> context);
}
```

**语句执行方说明**：  
- `selectOne`方法:`selectOne`必须**返回一个**对象或`null`值。如果返回值**多于一个，就会抛出异常**。  
- `selectList`方法: `selectList`**返回多个**对象，如果你不知道返回对象会有多少，请使用`selectList`。  
- `selectCursor`方法: `selectCursor`**返回多个**对象，并且借助迭代器实现了**惰性加载**。  
- `selectMap`方法: 将返回对象的其中**一个属性作为`key`**值，将**对象作为`value`**值，从而将多个结果集转为`Map`类型值。  
-  更新语句方法：`insert`、`update` 以及 `delete` 方法返回的值表示受该语句影响的行数。  
- `RowBounds`类型参数: 会告诉 MyBatis 略过指定数量的记录，并限制返回结果的数量。  
    - 构建`RowBounds`示例：`RowBounds`类的`offset`和`limit`值**只有在构造函数时才能传入**，其它时候是不能修改的。  
    - 设置`ResultSet`配置提高查询效率：建议将`ResultSet`类型设置为`SCROLL_SENSITIVE`或`SCROLL_INSENSITIV`（换句话说：不要使用`FORWARD_ONLY`）。  
- `Resulthandler`类型参数: `ResultHandler`参数允许**自定义每行结果的处理**过程。  
    - 自定义处理：你可以将它添加到`List`中、创建`Map`和`Set`，甚至*丢弃**每个返回值，只保留计算后的**统计**结果。  
    - 是内部实现：你可以使用`ResultHandler`做很多事，这其实就是 MyBatis **构建结果列表的内部实现办法**。   
    - `ResultContext`参数：
        - 访问结果对象和创建信息：`ResultContext` 参数允许你访问结果对象和当前已被创建的对象数目。  
        - `stop`方法：另外还提供了一个返回值为 Boolean 的 stop 方法，你可以使用此 stop 方法来停止 MyBatis 加载更多的结果。  
        - 限制： 
            - 不缓存数据： 使用带 ResultHandler 参数的方法时，收到的数据不会被缓存。  
            - 连接对象不完整：当使用高级的结果映射集（resultMap）时，MyBatis 很可能需要数行结果来构造一个对象。如果你使用了 ResultHandler，你可能会接收到关联（association）或者集合（collection）中**尚未被完整填充的对象**。  
    
```java
//selectCursor惰性加载
public class Example{
    public static void main(String[] args){
        try(Cursor<MyEntity> entities = 
                session.selectCursor(statementID, parameter)){
            for(MyEntity entity: entities){
                entity;
            }
        }
    }
}
```
```java
//RowBounds示例
public class Example{
    public static void main(String[] args){
        int offset = 100;
        int limit = 25;
        RowBounds rowBounds = new RowBounds(offset, limit);
    }
}
```
#### 立即批量更新方法  
> 当你将 ExecutorType 设置为 **ExecutorType.BATCH** 时，  
> 可以使用这个方法清除（执行）缓存在 JDBC 驱动类中的批量更新语句。  
```java
public class SqlSession{
    List<BatchResult> flushStatements(){}
}
```
#### 事务控制方法  
> 有四个方法用来控制事务作用域。 
> 当然，如果你已经设置了自动提交或你使用了外部事务管理器，这些方法就没什么作用了。  
> 然而，如果你正在使用由 Connection 实例控制的 JDBC 事务管理器，那么这四个方法就会派上用场。  

```java
public class SqlSession{
    public void commit(){}
    public void rollback(){}
    public void commit(boolean force){}
    public void rollback(boolean force){}
}
```

**事务控制方法说明**：  
- 默认不自动提交事务： 默认情况下 MyBatis 不会自动提交事务，除非它侦测到调用了插入、更新或删除方法改变了数据库。  
- `force`参数保证事务正常提交： 如果你没有使用这些方法提交修改，那么你可以在 commit 和 rollback 方法参数中传入 true 值，来保证事务被正常提交。  
- `force`参数无效的请况：注意，在自动提交模式或者使用了外部事务管理器的情况下，设置 force 值对 session 无效。  
- `rollback()`方法：
    > 大部分情况下你无需调用 rollback()，因为 MyBatis 会在你没有调用 commit 时替你完成回滚操作。  
    > 不过，当你要在一个可能多次提交或回滚的 session 中详细控制事务，回滚操作就派上用场了。  
> 提示 MyBatis-Spring 和 MyBatis-Guice 提供了声明式事务处理，所以如果你在使用 Mybatis 的同时使用了 Spring 或者 Guice，请参考它们的手册以获取更多的内容。  

#### 本地缓存  
Mybatis使用了两种缓存：  
- **本地缓存(local cache)**:
    - **本地缓存属于`SqlSession`**： 每当一个新 session 被创建，MyBatis 就会创建一个与之相关联的本地缓存。    
    - **保存查询结果**： 任何在 session 执行过的查询结果都会被保存在本地缓存中，所以，当再次执行参数相同的相同查询时，就不需要实际查询数据库了。  
    - **清空缓存的时机**： 本地缓存将会在做出**修改**、**事务提交**或**回滚**，以及**关闭`session`**时清空。  
    - **生命周期**： 默认情况下，本地缓存数据的生命周期等同于整个 session 的周期。  
        - **`localCacheScope`**设置：由于缓存会被用来解决循环引用问题和加快重复嵌套查询的速度，所以无法将其完全禁用。  
            - **`STATEMENT`**: 但是你可以通过设置`localCacheScope=STATEMENT`来只在**语句执行时使用缓存**。  
            - **`SESSION`**: 注意，如果`localCacheScope`被设置为`SESSION`，对于某个对象，MyBatis 将返回在本地缓存中**唯一对象的引用**。  
                - 谨慎更改对象： 对返回的对象（例如 list）做出的任何修改将会影响本地缓存的内容，进而将会影响到在本次 session 中从缓存返回的值。因此，不要对 MyBatis 所返回的对象作出更改，以防后患。          
- **二级缓存(second level cache)**: `<cache>`、`<cache-ref>`标签。  

**清空本地缓存**：  
> 你可以随时调用以下方法来清空本地缓存。  
```java
public class SqlSession{
    public void clearCache();
}
```

**确保 SqlSession 被关闭**:  
> 对于你打开的任何 session，你都要保证它们被妥善关闭，这很重要。保证妥善关闭的最佳代码模式如下：  
```java
public class SqlSession{
    public void close();
}
public class Example{
    public static void main(String[] args){
        try(SqlSession session = sqlSessionFactory.openSession){
            session;
        }
    }
}
```

**获取`Configuation`实例**：  
> 和 SqlSessionFactory 一样，你可以调用当前使用的 SqlSession 的 getConfiguration 方法来获得 Configuration 实例。  
```java
public class SqlSession{
    public Configuration getConfiguration();
}
```
#### 使用映射器  
> 上述的各个 insert、update、delete 和 select 方法都很强大，但也有些繁琐，它们并不符合类型安全，对你的 IDE 和单元测试也不是那么友好。  
> 因此，使用映射器类来执行映射语句是更常见的做法。  

```java
//获取映射器接口的实例。  
public class SqlSession{
    <T> T getMapper(Class<T> type){}
}
```
**映射器**：  
- **映射器接口**：映射器接口不需要去实现任何接口或继承自任何类。只要方法签名可以被用来唯一识别对应的映射语句就可以了。  
- **映射器方法**： 每个映射器**方法签名应该匹配相关联的`SqlSession`方法**。 字符串参数 ID 无需匹配。而是由**方法名匹配映射语句的 ID**。    
    - 命名空间匹配：与包名匹配。  
    - 返回类型：必须匹配期望的结果类型，放回单个值时返回类型是放回值的类型，返回多个值是则未数组或集合类或游标。  
- **多参数传递**： 可以传递多个参数给一个映射器方法。  
    - 默认参数命名： 在多个参数的情况下，默认它们将会以`param`加上它们在参数列表中的位置来命名，比如：`#{param1}`、`#{param2}`等。  
    - 自定义参数命名：  如果你想（在有多个参数时）自定义参数的名称，那么你可以在参数上使用**`@Param("paramName")`注解**。  
- **接口继承**：映射器接口可以继承自其他接口。  
    - **匹配XML配置的命名空间**： 在使用 XML 来绑定映射器接口时，保证语句处于合适的命名空间中即可。  
    - **限制相同的方法签名**：唯一的限制是，不能在两个具有继承关系的接口中拥有相同的方法签名（这是潜在的危险做法，不可取）。       

**映射器示例**：  
```java
//映射器接口
public interface AuthorMapper {
  // (Author) selectOne("selectAuthor",5);
  Author selectAuthor(int id);
  // (List<Author>) selectList(“selectAuthors”)
  List<Author> selectAuthors();
  // (Map<Integer,Author>) selectMap("selectAuthors", "id")
  @MapKey("id")
  Map<Integer, Author> selectAuthors();
  // insert("insertAuthor", author)
  int insertAuthor(Author author);
  // updateAuthor("updateAuthor", author)
  int updateAuthor(Author author);
  // delete("deleteAuthor",5)
  int deleteAuthor(int id);
}
//获取映射器实例，并执行映射的SQL语句。
public class Example{
    public static SqlSessionFactory sqlSessionFactory;
    public static void main(String[] args){
        String resource = "/META-INF/resources/config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        sqlSessionFactory = new SqlSessionFactroyBuilder().build(inputStream);
        
        try(SqlSession sqlSession = sqlSessionFactory.openSession()){
            //获取映射器接口
            AuthorMapper authorMapper = sqlSession.getMapper(AuthorMapper.class);
            //通过映射器实例方法执行映射的语句。  
            Author author = authorMapper.selectAuthor(1);
        }
    }
}
```

### 映射器注解  
- 配置API：MyBatis 3 构建在全面且强大的基于 Java 语言的配置 API 之上。它是 XML 和注解配置的基础。  
- XML 配置： 设计初期的 MyBatis 是一个 XML 驱动的框架。配置信息是基于 XML 的，映射语句也是定义在 XML 中的。  
- 注解配置：而在 MyBatis 3 中，我们提供了其它的配置方式。 注解提供了一种简单且低成本的方式来实现简单的映射语句。  

**注解配置的限制**：  
> 不幸的是，Java 注解的表达能力和灵活性十分有限。尽管我们花了很多时间在调查、设计和试验上，  
> 但最强大的 MyBatis 映射并不能用注解来构建——我们真没开玩笑。  
> 而 C# 属性就没有这些限制，因此 MyBatis.NET 的配置会比 XML 有更大的选择余地。  
> 虽说如此，基于 Java 注解的配置还是有它的好处的。 

<table border="0" class="table table-striped">
    <thead>
        <tr class="a">
            <th>注解</th>
            <th>使用对象</th>
            <th>XML 等价形式</th>
            <th>描述</th>
        </tr>
    </thead>
    <tbody>
      <tr class="b">
        <td><code>@CacheNamespace</code></td>
        <td><code>类</code></td>
        <td><code>&lt;cache&gt;</code></td><td>为给定的命名空间（比如类）配置缓存。属性：<code>implementation</code>、<code>eviction</code>、<code>flushInterval</code>、<code>size</code>、<code>readWrite</code>、<code>blocking</code>、<code>properties</code>。</td>
      </tr>
      <tr class="a">
        <td><code>@Property</code></td>
        <td>N/A</td>
        <td><code>&lt;property&gt;</code></td><td>指定参数值或占位符（placeholder）（该占位符能被 <code>mybatis-config.xml</code> 内的配置属性替换）。属性：<code>name</code>、<code>value</code>。（仅在 MyBatis 3.4.2 以上可用）</td>
      </tr>
      <tr class="b">
        <td><code>@CacheNamespaceRef</code></td>
        <td><code>类</code></td>
        <td><code>&lt;cacheRef&gt;</code></td>
        <td>引用另外一个命名空间的缓存以供使用。注意，即使共享相同的全限定类名，在 XML 映射文件中声明的缓存仍被识别为一个独立的命名空间。属性：<code>value</code>、<code>name</code>。如果你使用了这个注解，你应设置 <code>value</code> 或者&nbsp;<code>name</code> 属性的其中一个。<code>value</code> 属性用于指定能够表示该命名空间的 Java 类型（命名空间名就是该 Java 类型的全限定类名），<code>name</code> 属性（这个属性仅在 MyBatis 3.4.2 以上可用）则直接指定了命名空间的名字。</td>
      </tr>
      <tr class="a">
        <td><code>@ConstructorArgs</code></td>
        <td><code>方法</code></td>
        <td><code>&lt;constructor&gt;</code></td><td>收集一组结果以传递给一个结果对象的构造方法。属性：<code>value</code>，它是一个 <code>Arg</code> 数组。</td>
      </tr>
      <tr class="b">
        <td><code>@Arg</code></td>
        <td>N/A</td>
        <td>
            <ul>
                <li><code>&lt;arg&gt;</code></li>
                <li><code>&lt;idArg&gt;</code></li>
            </ul>
        </td>
        <td>ConstructorArgs 集合的一部分，代表一个构造方法参数。属性：<code>id</code>、<code>column</code>、<code>javaType</code>、<code>jdbcType</code>、<code>typeHandler</code>、<code>select</code>、<code>resultMap</code>。id 属性和 XML 元素 <code>&lt;idArg&gt;</code> 相似，它是一个布尔值，表示该属性是否用于唯一标识和比较对象。从版本 3.5.4 开始，该注解变为可重复注解。</td>
      </tr>
      <tr class="a">
        <td><code>@TypeDiscriminator</code></td>
        <td><code>方法</code></td>
        <td><code>&lt;discriminator&gt;</code></td><td>决定使用何种结果映射的一组取值（case）。属性：<code>column</code>、<code>javaType</code>、<code>jdbcType</code>、<code>typeHandler</code>、<code>cases</code>。cases 属性是一个 <code>Case</code> 的数组。</td>
      </tr>
      <tr class="b">
        <td><code>@Case</code></td>
        <td>N/A</td>
        <td><code>&lt;case&gt;</code></td><td>表示某个值的一个取值以及该取值对应的映射。属性：<code>value</code>、<code>type</code>、<code>results</code>。results 属性是一个 <code>Results</code> 的数组，因此这个注解实际上和 <code>ResultMap</code> 很相似，由下面的 <code>Results</code> 注解指定。</td>
      </tr>
      <tr class="a">
        <td><code>@Results</code></td>
        <td><code>方法</code></td>
        <td><code>&lt;resultMap&gt;</code></td>
        <td>一组结果映射，指定了对某个特定结果列，映射到某个属性或字段的方式。属性：<code>value</code>、<code>id</code>。value 属性是一个 <code>Result</code> 注解的数组。而 id 属性则是结果映射的名称。从版本 3.5.4 开始，该注解变为可重复注解。</td>
      </tr>
      <tr class="b">
        <td><code>@Result</code></td>
        <td>N/A</td>
        <td>
            <ul>
                <li><code>&lt;result&gt;</code></li>
                <li><code>&lt;id&gt;</code></li>
            </ul>
        </td><td>在列和属性或字段之间的单个结果映射。属性：<code>id</code>、<code>column</code>、<code>javaType</code>、<code>jdbcType</code>、<code>typeHandler</code>、<code>one</code>、<code>many</code>。id 属性和 XML 元素 <code>&lt;id&gt;</code> 相似，它是一个布尔值，表示该属性是否用于唯一标识和比较对象。one 属性是一个关联，和 <code>&lt;association&gt;</code> 类似，而 many 属性则是集合关联，和 <code>&lt;collection&gt;</code> 类似。这样命名是为了避免产生名称冲突。</td>
      </tr>
      <tr class="a">
        <td><code>@One</code></td>
        <td>N/A</td>
        <td><code>&lt;association&gt;</code></td><td>复杂类型的单个属性映射。属性：
            <code>select</code>，指定可加载合适类型实例的映射语句（也就是映射器方法）全限定名；
            <code>fetchType</code>，指定在该映射中覆盖全局配置参数 <code>lazyLoadingEnabled</code>；
            <code>resultMap</code>(available since 3.5.5), which is the fully qualified name of a result map that map to a single container object from select result；
            <code>columnPrefix</code>(available since 3.5.5), which is column prefix for grouping select columns at nested result map.
            <span class="label important">提示</span> 注解 API 不支持联合映射。这是由于 Java 注解不允许产生循环引用。
        </td>
      </tr>
      <tr class="b">
        <td><code>@Many</code></td>
        <td>N/A</td>
        <td><code>&lt;collection&gt;</code></td><td>复杂类型的集合属性映射。属性：
            <code>select</code>，指定可加载合适类型实例集合的映射语句（也就是映射器方法）全限定名；
            <code>fetchType</code>，指定在该映射中覆盖全局配置参数 <code>lazyLoadingEnabled</code>
            <code>resultMap</code>(available since 3.5.5), which is the fully qualified name of a result map that map to collection object from select result；
            <code>columnPrefix</code>(available since 3.5.5), which is column prefix for grouping select columns at nested result map.
            <span class="label important">提示</span> 注解 API 不支持联合映射。这是由于 Java 注解不允许产生循环引用。
        </td>
        </tr>
      <tr class="a">
        <td><code>@MapKey</code></td>
        <td><code>方法</code></td>
        <td></td>
        <td>供返回值为 Map 的方法使用的注解。它使用对象的某个属性作为 key，将对象 List 转化为 Map。属性：<code>value</code>，指定作为 Map 的 key 值的对象属性名。</td>
      </tr>
      <tr class="b">
        <td><code>@Options</code></td>
        <td><code>方法</code></td>
        <td>映射语句的属性</td><td>该注解允许你指定大部分开关和配置选项，它们通常在映射语句上作为属性出现。与在注解上提供大量的属性相比，<code>Options</code> 注解提供了一致、清晰的方式来指定选项。属性：<code>useCache=true</code>、<code>flushCache=FlushCachePolicy.DEFAULT</code>、<code>resultSetType=DEFAULT</code>、<code>statementType=PREPARED</code>、<code>fetchSize=-1</code>、<code>timeout=-1</code>、<code>useGeneratedKeys=false</code>、<code>keyProperty=""</code>、<code>keyColumn=""</code>、<code>resultSets=""</code>, <code>databaseId=""</code>。注意，Java 注解无法指定 <code>null</code> 值。因此，一旦你使用了 <code>Options</code> 注解，你的语句就会被上述属性的默认值所影响。要注意避免默认值带来的非预期行为。
        The <code>databaseId</code>(Available since 3.5.5), in case there is a configured <code>DatabaseIdProvider</code>,
        the MyBatis use the <code>Options</code> with no <code>databaseId</code> attribute or with a <code>databaseId</code>
        that matches the current one. If found with and without the <code>databaseId</code> the latter will be discarded.<br><br>
        &nbsp; &nbsp; &nbsp; &nbsp;注意：<code>keyColumn</code> 属性只在某些数据库中有效（如 Oracle、PostgreSQL 等）。要了解更多关于 <code>keyColumn</code> 和&nbsp;<code>keyProperty</code> 可选值信息，请查看“insert, update 和 delete”一节。</td>
      </tr>
      <tr class="a">
        <td>
            <ul>
                <li><code>@Insert</code>
                </li><li><code>@Update</code></li>
                <li><code>@Delete</code></li>
                <li><code>@Select</code></li>
            </ul>
        </td>
        <td><code>方法</code></td>
        <td>
            <ul>
                <li><code>&lt;insert&gt;</code></li>
                <li><code>&lt;update&gt;</code></li>
                <li><code>&lt;delete&gt;</code></li>
                <li><code>&lt;select&gt;</code></li>
            </ul>
        </td><td>
        每个注解分别代表将会被执行的 SQL 语句。它们用字符串数组（或单个字符串）作为参数。如果传递的是字符串数组，字符串数组会被连接成单个完整的字符串，每个字符串之间加入一个空格。这有效地避免了用 Java 代码构建 SQL 语句时产生的“丢失空格”问题。当然，你也可以提前手动连接好字符串。属性：<code>value</code>，指定用来组成单个 SQL 语句的字符串数组。
        The <code>databaseId</code>(Available since 3.5.5), in case there is a configured <code>DatabaseIdProvider</code>,
        the MyBatis use a statement with no <code>databaseId</code> attribute or with a <code>databaseId</code>
        that matches the current one. If found with and without the <code>databaseId</code> the latter will be discarded.
        </td>
      </tr>
      <tr class="b">
        <td>
            <ul>
                <li><code>@InsertProvider</code></li>
                <li><code>@UpdateProvider</code></li>
                <li><code>@DeleteProvider</code></li>
                <li><code>@SelectProvider</code></li>
            </ul>
        </td>
        <td><code>方法</code></td>
        <td>
            <ul>
                <li><code>&lt;insert&gt;</code></li>
                <li><code>&lt;update&gt;</code></li>
                <li><code>&lt;delete&gt;</code></li>
                <li><code>&lt;select&gt;</code></li>
            </ul>
        </td><td>
        允许构建动态 SQL。这些备选的 SQL 注解允许你指定返回 SQL 语句的类和方法，以供运行时执行。（从 MyBatis 3.4.6 开始，可以使用 <code>CharSequence</code> 代替&nbsp;<code>String</code> 来作为返回类型）。当执行映射语句时，MyBatis 会实例化注解指定的类，并调用注解指定的方法。你可以通过 <code>ProviderContext</code> 传递映射方法接收到的参数、"Mapper interface type" 和 "Mapper method"（仅在 MyBatis 3.4.5 以上支持）作为参数。（MyBatis 3.4 以上支持传入多个参数）
        属性：<code>value</code>、<code>type</code>、<code>method</code>、<code>databaseId</code>。
        <code>value</code> and <code>type</code> 属性用于指定类名
        (The <code>type</code> attribute is alias for <code>value</code>, you must be specify either one.
        But both attributes can be omit when specify the <code>defaultSqlProviderType</code> as global configuration)。
        <code>method</code> 用于指定该类的方法名（从版本 3.5.1 开始，可以省略 <code>method</code> 属性，MyBatis 将会使用 <code>ProviderMethodResolver</code> 接口解析方法的具体实现。如果解析失败，MyBatis 将会使用名为 <code>provideSql</code> 的降级实现）。<span class="label important">提示</span> 接下来的“SQL 语句构建器”一章将会讨论该话题，以帮助你以更清晰、更便于阅读的方式构建动态 SQL。
        The <code>databaseId</code>(Available since 3.5.5), in case there is a configured <code>DatabaseIdProvider</code>,
        the MyBatis will use a provider method with no <code>databaseId</code> attribute or with a <code>databaseId</code>
        that matches the current one. If found with and without the <code>databaseId</code> the latter will be discarded.
        </td>
      </tr>  
      <tr class="a">
        <td><code>@Param</code></td>
        <td><code>参数</code></td>
        <td>N/A</td><td>如果你的映射方法接受多个参数，就可以使用这个注解自定义每个参数的名字。否则在默认情况下，除 <code>RowBounds</code> 以外的参数会以 "param" 加参数位置被命名。例如 <code>#{param1}</code>, <code>#{param2}</code>。如果使用了 <code>@Param("person")</code>，参数就会被命名为 <code>#{person}</code>。</td>
      </tr>
      <tr class="b">
        <td><code>@SelectKey</code></td>
        <td><code>方法</code></td>
        <td><code>&lt;selectKey&gt;</code></td><td>
        这个注解的功能与 <code>&lt;selectKey&gt;</code> 标签完全一致。该注解只能在 <code>@Insert</code> 或 <code>@InsertProvider</code> 或 <code>@Update</code> 或 <code>@UpdateProvider</code> 标注的方法上使用，否则将会被忽略。如果标注了 <code>@SelectKey</code> 注解，MyBatis 将会忽略掉由 <code>@Options</code> 注解所设置的生成主键或设置（configuration）属性。属性：<code>statement</code> 以字符串数组形式指定将会被执行的 SQL 语句，<code>keyProperty</code> 指定作为参数传入的对象对应属性的名称，该属性将会更新成新的值，<code>before</code> 可以指定为 <code>true</code> 或&nbsp;<code>false</code> 以指明 SQL 语句应被在插入语句的之前还是之后执行。<code>resultType</code> 则指定&nbsp;<code>keyProperty</code> 的 Java 类型。<code>statementType</code> 则用于选择语句类型，可以选择 <code>STATEMENT</code>、<code>PREPARED</code> 或 <code>CALLABLE</code> 之一，它们分别对应于 <code>Statement</code>、<code>PreparedStatement</code> 和 <code>CallableStatement</code>。默认值是 <code>PREPARED</code>。
        The <code>databaseId</code>(Available since 3.5.5), in case there is a configured <code>DatabaseIdProvider</code>,
        the MyBatis will use a statement with no <code>databaseId</code> attribute or with a <code>databaseId</code>
        that matches the current one. If found with and without the <code>databaseId</code> the latter will be discarded.
        </td>
      </tr>
      <tr class="a">
        <td><code>@ResultMap</code></td>
        <td><code>方法</code></td>
        <td>N/A</td>
        <td>这个注解为 <code>@Select</code> 或者 <code>@SelectProvider</code> 注解指定 XML 映射中 <code>&lt;resultMap&gt;</code> 元素的 id。这使得注解的 select 可以复用已在 XML 中定义的 ResultMap。如果标注的 select 注解中存在 <code>@Results</code> 或者 <code>@ConstructorArgs</code> 注解，这两个注解将被此注解覆盖。</td>
      </tr>
      <tr class="b">
        <td><code>@ResultType</code></td>
        <td><code>方法</code></td>
        <td>N/A</td><td>在使用了结果处理器的情况下，需要使用此注解。由于此时的返回类型为 void，所以 Mybatis 需要有一种方法来判断每一行返回的对象类型。如果在 XML 有对应的结果映射，请使用 <code>@ResultMap</code> 注解。如果结果类型在 XML 的 <code>&lt;select&gt;</code> 元素中指定了，就不需要使用其它注解了。否则就需要使用此注解。比如，如果一个标注了 @Select 的方法想要使用结果处理器，那么它的返回类型必须是 void，并且必须使用这个注解（或者 @ResultMap）。这个注解仅在方法返回类型是 void 的情况下生效。</td>
      </tr>
      <tr class="a">
        <td><code>@Flush</code></td>
        <td><code>方法</code></td>
        <td>N/A</td><td>如果使用了这个注解，定义在 Mapper 接口中的方法就能够调用 <code>SqlSession#flushStatements()</code> 方法。（Mybatis 3.3 以上可用）</td>
      </tr>
    </tbody>
  </table>

**映射器注解示例**： 
```java
public interface ExampleMapper{
    //使用 @SelectKey 注解来在插入前读取数据库序列的值  
    @Insert("insert into table3 (id, name) values(#{nameId}, #{name})")
    @SelectKey(statement="call next value for TestSequence", keyProperty="nameId", before=true, resultType=int.class)
    int insertTable3(Name name);
    
    //使用 @SelectKey 注解来在插入后读取数据库自增列的值 
    @Insert("insert into table2 (name) values(#{name})")
    @SelectKey(statement="call identity()", keyProperty="nameId", before=false, resultType=int.class)
    int insertTable2(Name name);
    
    //使用 @Flush 注解来调用 SqlSession#flushStatements()。  
    @Flush
    List<BatchResult> flush();
    
    //通过指定 @Result 的 id 属性来命名结果集 
    @Results(id = "userResult", value = {
      @Result(property = "id", column = "uid", id = true),
      @Result(property = "firstName", column = "first_name"),
      @Result(property = "lastName", column = "last_name")
    })
    @Select("select * from users where id = #{id}")
    User getUserById(Integer id);
    
    @Results(id = "companyResults")
    @ConstructorArgs({
      @Arg(column = "cid", javaType = Integer.class, id = true),
      @Arg(column = "name", javaType = String.class)
    })
    @Select("select * from company where id = #{id}")
    Company getCompanyById(Integer id);
    
    //使用单个参数的 @SqlProvider 注解
    @SelectProvider(type = UserSqlBuilder.class, method = "buildGetUsersByName")
    List<User> getUsersByName(String name); 
    
    class UserSqlBuilder {
      public static String buildGetUsersByName(final String name) {
        return new SQL(){{
          SELECT("*");
          FROM("users");
          if (name != null) {
            WHERE("name like #{value} || '%'");
          }
          ORDER_BY("id");
        }}.toString();
      }
    }

    //使用多个参数的 @SqlProvider 注解 
    @SelectProvider(type = UserSqlBuilder.class, method = "buildGetUsersByName")
    List<User> getUsersByName(
        @Param("name") String name, @Param("orderByColumn") String orderByColumn);
    
    class UserSqlBuilder {
      // 如果不使用 @Param，就应该定义与 mapper 方法相同的参数
      public static String buildGetUsersByName(
          final String name, final String orderByColumn) {
        return new SQL(){{
          SELECT("*");
          FROM("users");
          WHERE("name like #{name} || '%'");
          ORDER_BY(orderByColumn);
        }}.toString();
      }
    
      // 如果使用 @Param，就可以只定义需要使用的参数
      public static String buildGetUsersByName(@Param("orderByColumn") final String orderByColumn) {
        return new SQL(){{
          SELECT("*");
          FROM("users");
          WHERE("name like #{name} || '%'");
          ORDER_BY(orderByColumn);
        }}.toString();
      }
    }
    
    //ProviderMethodResolver（3.5.1 后可用）的默认实现使用方法
    @SelectProvider(UserSqlProvider.class)
    List<User> getUsersByName(String name);
    
    // 在你的 provider 类中实现 ProviderMethodResolver 接口
    class UserSqlProvider implements ProviderMethodResolver {
      // 默认实现中，会将映射器方法的调用解析到实现的同名方法上
      public static String getUsersByName(final String name) {
        return new SQL(){{
          SELECT("*");
          FROM("users");
          if (name != null) {
            WHERE("name like #{value} || '%'");
          }
          ORDER_BY("id");
        }}.toString();
      }
    }
    
    //在语句注解上使用`databaseId`属性
    @Select(value = "SELECT SYS_GUID() FROM dual", databaseId = "oracle") // Use this statement if DatabaseIdProvider provide "oracle"
    @Select(value = "SELECT uuid_generate_v4()", databaseId = "postgres") // Use this statement if DatabaseIdProvider provide "postgres"
    @Select("SELECT RANDOM_UUID()") // Use this statement if the DatabaseIdProvider not configured or not matches databaseId
    String generateId();
}

//This example shows usage that share an sql provider class to all mapper methods using global configuration(Available since 3.5.6):
public class Example{
    public static void main(String[] args){
                Configuration configuration = new Configuration();
        configuration.setDefaultSqlProviderType(TemplateFilePathProvider.class); // Specify an sql provider class for sharing on all mapper methods
    }
}    
// Can omit the type/value attribute on sql provider annotation
// If omit it, the MyBatis apply the class that specified on defaultSqlProviderType.
public interface UserMapper {

  @SelectProvider // Same with @SelectProvider(TemplateFilePathProvider.class)
  User findUser(int id);

  @InsertProvider // Same with @InsertProvider(TemplateFilePathProvider.class)
  void createUser(User user);

  @UpdateProvider // Same with @UpdateProvider(TemplateFilePathProvider.class)
  void updateUser(User user);

  @DeleteProvider // Same with @DeleteProvider(TemplateFilePathProvider.class)
  void deleteUser(int id);
}
```