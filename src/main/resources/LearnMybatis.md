# LearnMybatis

## 入门  

### 构建`SqlSessionFactory`  
- 每个mybatis应用都是**以一个`SqlSessionFactory`为核心**。  
- `SqlSessionFactory`的实例可以通过**`SqlSessionFactoryBuilder`**用来获得。  
- `SqlSessionFactoryBuilder`可以从**XML配置文件**或一个预先配置的**Configuration实例**来构建`SqlSessionFactory`。  

从XML中构建`SqlSessionFactory`： 
- XML 配置文件中包含了对 MyBatis 系统的**核心设置**， 
- 包括获取数据库连接实例的**数据源（DataSource）**以及决定事务作用域和控制方式的**事务管理器（TransactionManager）**。 

>从 XML 文件中构建 SqlSessionFactory 的实例非常简单，建议使用类路径下的资源文件进行配置。   
>但也可以使用**任意的输入流（InputStream）实例**，比如用文件路径字符串或 file:// URL 构造的输入流。  
>MyBatis 包含一个名叫 `Resources `的工具类，它包含一些实用方法，使得从类路径或其它位置加载资源文件更加容易。  
```java
public class LearnMybatis
{
    public static void main( String[] args )throws IOException
    {
        //从XML中构建SqlSessionFactory
        String resource = "META-INF/mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory xmlSqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }
}
```
>`environment`元素体中包含了**事务管理和连接池的配置**。  
>`mappers`元素则包含了**一组映射器（mapper）**，这些映射器的 XML 映射文件包含了 **SQL 代码和映射定义信息**。 
```XML
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
    PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <properties resource="META-INF/config.properties">
    </properties>
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
    <mappers>
        <mapper resource="META-INF/BlogMapper.xml"/>
    </mappers>
</configuration>
```

不使用 XML 构建 SqlSessionFactory：  
- 如果你更愿意直接**从 Java 代码**而不是 XML 文件中创建配置，或者想要**创建你自己的配置建造器**，  
- MyBatis 也提供了**完整的配置类**，提供了所有与 XML 文件等价的配置项。  

>注意该例中，configuration 添加了一个映射器类（mapper class）。  
>映射器类是 Java 类，它们**包含 SQL 映射注解**从而避免依赖 XML 文件。  
>不过，由于 Java 注解的一些限制以及某些 MyBatis 映射的复杂性，要使用大多数**高级映射（比如：嵌套联合映射），仍然需要使用 XML 配置**。  
>有鉴于此，如果存在一个同名 XML 配置文件，MyBatis 会自动查找并加载它（在这个例子中，基于类路径和 BlogMapper.class 的类名，会加载 BlogMapper.xml）。具体细节稍后讨论。
```java
public class LearnMybatis
{
    public static void main( String[] args )throws IOException
    { 
        //不适用XML构建SqlSessionFactory
        DataSource dataSource = BlogDataSourceFactory.getBlogDataSource();
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(BlogMapper.class);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }
}
```

### 从SqlSessionFactory中获取SqlSession  
- `SqlSession` 提供了在数据库**执行 SQL 命令所需的所有方法**。  
- 你可以通过`SqlSession`实例来**直接执行已映射的 SQL 语句**。  
- 更简洁的方式时使用**和指定语句**的参数和返回值**相匹配的接口**（比如 BlogMapper.class）。  
    >现在你的代码不仅**更清晰，更加类型安全**，还**不用担心可能出错**的字符串字面值以及强制类型转换。

```java
public class LearnMybatis
{
    public static void main( String[] args )throws IOException
    {
         //从SqlSessionFactory中获取SqlSession
         try(SqlSession session = xmlSqlSessionFactory.openSession()) {
             Blog blog = session.selectOne("pers.mortal.learn.mybatis.BlogMapper.selectBlog", 1);//使用全限定名调用映射语句。
             System.out.println(blog.getContent());
         }
        //更简洁的方式，使用和指定语句的参数的返回值相匹配的接口————代码更清晰，更类型安全
         try (SqlSession session = xmlSqlSessionFactory.openSession()) {
             BlogMapper mapper = session.getMapper(BlogMapper.class);        //用SqlSession实例获取映射器实例
             Blog blog = mapper.selectBlog(1);                               //在对应的映射器接口调用方法，需要先提供一个接口。
             System.out.println(blog.getContent());
         }
    }
}
```

### 探究已映射的语句
- 一个语句既可以通过**XML定义**，也可以通过**注解定义**。
- 事实上 MyBatis 提供的**所有特性**都可以利用基于 XML 的映射语言来实现。  

XML映射： 
- 它在命名空间`“org.mybatis.example.BlogMapper”`中定义了一个名为`“selectBlog”`的映射语句，  
- 这样你就可以用**全限定名**` “org.mybatis.example.BlogMapper.selectBlog” `来**调用映射语句**了。   
- 该命名就可以**直接映射**到在**命名空间中同名的映射器类**，并将已映射的 select 语句匹配到对应名称、参数和返回类型的方法。
    >因此你就可以像上面那样，不费吹灰之力地在**对应的映射器接口**调用方法。  
```XML
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="pers.mortal.learn.mybatis.BlogMapper">
  <select id="selectBlog" resultType="Blog">
    select * from Blog where id = #{id}
  </select>
</mapper>
```


Java 注解来配置映射语句： 
- 对于像 BlogMapper 这样的映射器类来说，还有另一种方法来完成语句映射。上面的 XML 示例可以被替换成如下的配置。 
- 使用注解来映射**简单语句**会使代码显得更加简洁，但对于稍微**复杂一点的语句**，Java 注解不仅力不从心，还会让你本就复杂的 SQL 语句更加混乱不堪。  
- 因此，如果你需要做一些**很复杂的操作，最好用 XML 来映射语句**。  
>就是给上述更简洁执行SQL映射语句所用的映射器接口添加注解。  
>不能同时存在相同映射语句的XML映射和注解映射，否则会报映射器id重复，其实很容易理解,它们具有相同的命名空间，相同的id。
```java
package pers.mortal.learn.mybatis;
public interface BlogMapper {
  @Select("SELECT * FROM blog WHERE id = #{id}")
  Blog selectBlog(int id);
}
```

### 命名空间  
- 在mybatis之前的版本，命名空间作用意义不大，现在随着命名空间越发重要，**必须指定命名空间**。  
- 命名空间有两个作用：  
    - 利用更长的全限定名来**隔离不同语句**。  
    - 实现上述所示的**接口绑定**。  

### 作用域和生命周期  

对象生命周期和依赖注入框架： 
- 依赖注入框架可以创建线程安全的、基于事务的 SqlSession 和映射器，并将它们直接注入到你的 bean 中，因此可以**直接忽略它们的生命周期**。  
- 如果对如何通过依赖注入框架使用 MyBatis 感兴趣，可以研究一下 **MyBatis-Spring** 或 **MyBatis-Guice** 两个子项目。  

`SqlSessionFactoryBuilder`：  
- 这个类可以被实例化、使用和丢弃，一旦创建了`SqlSessionFactory`，就不再需要它了。 
- 因此`SqlSessionFactoryBuilder`实例的最佳作用域是**方法作用域（也就是局部方法变量）**。 
>你可以重用 SqlSessionFactoryBuilder 来创建多个 SqlSessionFactory 实例，  
>但最好还是不要一直保留着它，以保证所有的 XML 解析资源可以被释放给更重要的事情。  

`SqlSessionFactory`：  
- `SqlSessionFactory` 一旦被创建就应该在**应用的运行期间一直存在**，没有任何理由丢弃它或重新创建另一个实例。   
> 使用`SqlSessionFactory`的最佳实践是在应用运行期间不要重复创建多次，多次重建 SqlSessionFactory 被视为一种代码“坏习惯”。  
> 因此 SqlSessionFactory 的最佳作用域是应用作用域。 有很多方法可以做到，最简单的就是使用单例模式或者静态单例模式。  

`SqlSession`：  
- **每个线程**都应该有它自己的`SqlSession`实例。  
- SqlSession 的实例**不是线程安全的**，因此是不能被共享的，所以它的最佳的作用域是请求或方法作用域。 
>绝对不能将 SqlSession 实例的引用放在一个类的静态域，甚至一个类的实例变量也不行。   
> 也绝不能将 SqlSession 实例的引用放在任何类型的托管作用域中，比如 Servlet 框架中的 HttpSession。   
>如果你现在正在使用一种 Web 框架，考虑将 SqlSession 放在一个和 HTTP 请求相似的作用域中。   
>换句话说，每次收到 HTTP 请求，就可以打开一个 SqlSession，返回一个响应后，就关闭它。   
>这个关闭操作很重要，为了确保每次都能执行关闭操作，你应该把这个关闭操作放到 finally 块中。 下面的示例就是一个确保 SqlSession 关闭的标准模式：  
```
try (SqlSession session = sqlSessionFactory.openSession()) {
  // 你的应用逻辑代码
}
```

映射器实例：  
- 映射器是一些**绑定映射语句的接口**。  
- 映射器接口的**实例是从`SqlSession`中获得**的`。
- **方法作用域才是映射器实例的最合适的作用域**。  
- 映射器实例**并不需要被显式地关闭**。  
> 虽然从技术层面上来讲，任何映射器实例的最大作用域与请求它们的 SqlSession 相同。  
> 但**方法作用域才是映射器实例的最合适的作用域**。  也就是说，映射器实例应该在调用它们的方法中被获取，使用完毕之后即可丢弃。  
> 映射器实例**并不需要被显式地关闭**。
> 尽管在整个请求作用域保留映射器实例不会有什么问题，但是你很快会发现，在这个作用域上管理太多像 SqlSession 的资源会让你忙不过来。   
> 因此，最好将映射器放在方法作用域内。就像下面的例子一样：
```
try (SqlSession session = sqlSessionFactory.openSession()) {
  BlogMapper mapper = session.getMapper(BlogMapper.class);
  // 你的应用逻辑代码
}
```
