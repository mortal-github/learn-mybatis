## XML Mapper  

### XML Mapper 文件声明  
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="pers.mortal.learn.mybatis.BlogMapper">
    <select id="selectBlog" resultType="pers.mortal.learn.mybatis.Blog">
        select * from Blog where id = #{id}
    </select>
</mapper>
```

### XML 映射器 顶级元素  
- `<cache>`: 该命名空间的**缓存配置**。  
- `<cache-ref>`: 引用**其他命名空间**的缓存配置。   
- `<resultMap>`: 描述如何从数据库**结果集中加载对象**。  
- `<parameterMap>`: 老式风格的参数映射。元素已弃用。  
- `<sql>`: 可被其他语句引用的**可重用语句块**。  
- `<insert>`: 映射插入语句。 
- `<update>`: 映射更新语句。  
- `<delete>`: 映射删除语句。  
- `<select>`: 映射查询语句。  

### `<select>`元素  
- `id`属性： 映射语句的表示符号，用来调用这条映射语句。  
- `#{ }`转义占位符：花括号内属性转义成JDBC中预备语句`?`参数。  
```xml
<select id="selectBlog" resultType="pers.mortal.learn.mybatis.Blog">
    select * from Blog where id = #{id}
</select>
```   

**`<select>`属性**： 
- `id`:	在命名空间中唯一的标识符，可以被用来引用这条语句。  
- `parameterType`:	将会传入这条语句的**参数的类全限定名或别名**。这个属性是可选的，因为 MyBatis 可以通过**类型处理器（TypeHandler）推断**出具体传入语句的参数，**默认值为未设置（unset）**。  
- `resultType`:	期望从这条语句中**返回结果的类全限定名或别名**。 注意，如果返回的是集合，那应该设置为**集合包含的类型**，而不是集合本身的类型。 resultType 和 resultMap 之间只能同时使用一个。  
- `parameterMap`:	用于引用外部 parameterMap 的属性，目前已被废弃。请使用行内参数映射和 parameterType 属性。  
- `resultMap`:	对外部 resultMap 的**命名引用**。结果映射是 MyBatis 最强大的特性，如果你对其理解透彻，许多复杂的映射问题都能迎刃而解。 resultType 和 resultMap 之间只能同时使用一个。  
- `statementType`:	可选**`STATEMENT`，`PREPARED` 或 `CALLABLE`**。这会让 MyBatis 分别使用 `Statement`，`PreparedStatement` 或 `CallableStatement`，**默认值：`PREPARED`**。  
- `resultSetType`:	**`FORWARD_ONLY`，`SCROLL_SENSITIVE`, `SCROLL_INSENSITIVE` 或 `DEFAULT`**（等价于 unset） 中的一个，默认值为 unset （依赖数据库驱动）。  
- `resultOrdered`:	这个设置仅针对嵌套结果 select 语句：如果为 true，将会**假设包含了嵌套结果集或是分组**，当返回一个主结果行时，就不会产生对前面结果集的引用。 这就使得在获取嵌套结果集的时候不至于内存不够用。默认值：false。  
- `resultSets`:	这个设置仅适用于**多结果集的情况**。它将列出语句执行后返回的结果集并赋予每个结果集一个名称，多个名称之间以逗号分隔。  
- `fetchSize`:	这是一个给驱动的建议值，尝试让驱动程序每次批量返回的结果行数等于这个设置值。 默认值为未设置（unset）（依赖驱动）。  
- `timeout`:	这个设置是在抛出异常之前，驱动程序等待数据库返回请求结果的秒数。默认值为未设置（unset）（依赖数据库驱动）。  
- `useCache`:	将其设置为 true 后，将会导致**本条语句**的结果被**二级缓存**缓存起来，默认值：**对 select 元素为 true**。  
- `flushCache`:	将其设置为 true 后，只要语句被调用，都会导致**本地缓存和二级缓存被清空**，默认值：**false**。  
- `databaseId`:	如果配置了数据库厂商标识（databaseIdProvider），MyBatis 会加载所有不带 databaseId 或匹配当前 databaseId 的语句；如果带和不带的语句都有，则不带的会被忽略。  

### `<insert>`、`<update>`、`<delete>`元素  
属性描述
- `id`:	在命名空间中唯一的标识符，可以被用来引用这条语句。
- `parameterType`:	将会传入这条语句的**参数的类全限定名或别名**。这个属性是可选的，因为 MyBatis 可以通过类型处理器（TypeHandler）推断出具体传入语句的参数，默认值为未设置（unset）。  
- `parameterMap`:	用于引用外部 parameterMap 的属性，目前已被废弃。请使用行内参数映射和 parameterType 属性。  
- `statementType`：可选**`STATEMENT`，`PREPARED` 或 `CALLABLE`**。这会让 MyBatis 分别使用 Statement，PreparedStatement 或 CallableStatement，默认值：PREPARED。  
- `useGeneratedKeys`:	（仅适用于 insert 和 update）这会令 MyBatis 使用 JDBC 的 **`getGeneratedKeys`方法来取出由数据库内部生成的主键**（比如：像 MySQL 和 SQL Server 这样的关系型数据库管理系统的自动递增字段），默认值：false。  
- `keyProperty`:	（仅适用于 insert 和 update）指定能够**唯一识别对象的属性**，MyBatis 会使用**`getGeneratedKeys` 的返回值**或 `insert` 语句的 **`selectKey` 子元素设置**它的值，默认值：未设置（unset）。如果生成列不止一个，可以用**逗号分隔多个属性名称**。  
- `keyColumn`:	（仅适用于 insert 和 update）设置**生成键值在表中的列名**，在某些数据库（像 PostgreSQL）中，当**主键列不是表中的第一列的时候，是必须设置**的。如果生成列不止一个，可以用**逗号分隔多个属性名称**。  
- `timeout`:	这个设置是在抛出异常之前，驱动程序等待数据库返回请求结果的秒数。默认值为未设置（unset）（依赖数据库驱动）。
- `flushCache`:	将其设置为 true 后，只要语句被调用，都会导致**本地缓存和二级缓存被清空**，默认值：（对 insert、update 和 delete 语句）**true**。
- `databaseId`:	如果配置了数据库厂商标识（databaseIdProvider），MyBatis 会加载所有不带 databaseId 或匹配当前 databaseId 的语句；如果带和不带的语句都有，则不带的会被忽略。

**`<insert>`示例**：   
>首先，如果你的数据库支持自动生成主键的字段（比如 MySQL 和 SQL Server），那么你可以设置 useGeneratedKeys=”true”，然后再把 keyProperty 设置为目标属性就 OK 了。  
```xml
<insert id="insertAuthor" useGeneratedKeys="true" keyProperty="id">
    INSERT
    INTO Author (username, password, email, bio)
    VALUES (#{username}, #{password}, E{email}, #{bio})
</insert>
```

**多行插入**：  
>如果你的数据库还支持多行插入, 你也可以传入一个 Author 数组或集合，并返回自动生成的主键。  
```xml
<insert id="insertAuthor" useGeneratedKeys="true" KeyProperty="id">
    INSERT 
    INTO Author(username, password, emali, boid)
    VALUES 
    <foreach item="item" collection="list" separator=",">
        (#{item.username}, #{item.password}, #{item.email}, #{item.bio})
    </foreach>
</insert>
```

**`<selectKey>`**子元素：  
>对于不支持自动生成主键列的数据库和可能不支持自动生成主键的 JDBC 驱动，MyBatis 有另外一种方法来生成主键。    

selectKey 元素的属性
- `resultType`：	结果的类型。通常 MyBatis 可以推断出来，但是为了更加准确，写上也不会有什么问题。MyBatis 允许将任何简单类型用作主键的类型，包括字符串。如果生成列不止一个，则可以使用包含期望属性的 Object 或 Map。
- `statementType`：	和前面一样，MyBatis 支持**`STATEMENT`，`PREPARED` 和 `CALLABLE`** 类型的映射语句，分别代表 Statement, PreparedStatement 和 CallableStatement 类型。
- `keyProperty`：	selectKey 语句结果应该被设置到的目标属性。如果生成列不止一个，可以用逗号分隔多个属性名称。
- `keyColumn`：	返回结果集中生成列属性的列名。如果生成列不止一个，可以用逗号分隔多个属性名称。
- `order`：	可以设置为**`BEFORE` 或 `AFTER`**。如果设置为 BEFORE，那么它首先会生成主键，设置 keyProperty 再执行插入语句。如果设置为 AFTER，那么先执行插入语句，然后是 selectKey 中的语句 - 这和 Oracle 数据库的行为相似，在插入语句内部可能有嵌入索引调用。
```xml
<select id="insertAuthor">
    <selectKey KeyProperty="id" resultType="int" order="BEFORE" >
        SELECT CAST(RANDOM()*1000000 AS INTEGER) a  from SYSIBM.SYSDUMMY1
    </selectKey>
    INSERT 
    INTO Author(id, username, password, email, bio, favourite_section)
    VALUES (#{id}, #{username}, #{password}, #{email}, #{bio}, #{favouriteSection, jdbcType=VARCHAR})
</select>
```

### `<sql>`元素  
- 这个元素可以用来定义可重用的 SQL 代码片段，以便在其它语句中使用。   
- **参数**可以静态地（在加载的时候）**确定**下来，并且可以**在不同的`include`**元素中定义**不同的参数值**。   

```xml
<mapper namespace="...">
    <sql id="userColumns">
        ${alias}.id, ${alias}.username, ${alias}.password
    </sql>
    <select id="selectUsers" resultType="map">
        SELECT 
        <include refid="userColumns">
            <property name="alias" value="t1"/>
        </include>
        ,
        <include refid="userColumns">
            <property name="alias" value="t2"/>
        </include>
        FROM some_table t1 
            cross join some_table t2 
    </select>
</mapper>
```

- 也可以在`include`元素的**`refid`**属性或**内部语句**中**使用属性值**。  
```xml
<mapper namespace="...">
    <sql id="sometable">
        ${prefix}Table
    </sql>
    <sql id="someinclude">
        FROM 
        <include refid="${include_target}"/><!--refid属性中使用属性值-->
    </sql>

    <select id="select"  resultType="map">
        SELECT field1, field2, field3 
        <include refid="someinclude">
            <property name="prefix" value="Some"/>
            <property name="include_target" value="sometable"/>
        </include>
    </select>
</mapper>
```

### 参数`#{...}`
之前见到的所有语句都使用了简单的参数形式。  
但实际上，参数是 MyBatis 非常强大的元素。  
对于大多数简单的使用场景，你都不需要使用复杂的参数。  
- **原始类型或简单数据类型**：（比如 Integer 和 String）因为没有其它属性，会用它们的值来作为参数。  
- **复杂对象类型**：往往需要一一指定传入对象的**一些属性**。   
- **简单参数**： `#{property}`。  
- **`javaType, jdbcType`**：指定**数据类型**,`#{property, javaType=int, jdbcType=NUMERIC}`参数可以**指定特殊的数据类型**，一般情况下mybatis可以自动推断java类型。但是如果是**`HashMap`**对象则需要显示指定javaType, 如果有一个列允许使用**null值**，必须指定JDBC类型。  
- **`typeHandler`**: 指定特殊的**类型处理器类**,`#{property, javaType=int, jdbcType=NUMERIC, typeHandler=MyTypeHandler}`。  
- **`numericScale`**：指定小数点后保留的位数。 `#{height, javaType=double, jdbcType=NUMERIC, numericScale=2}`   
- **`mode`**属性：**`IN,OUT,INOUT`**参数。
- **`STRUCT`**：`#{.... mode=OUT, jdbcType=STRUCT, jdbcTypeName=My_TYPE, resultMap=...}` MyBatis 也支持很多高级的数据类型，比如结构体（structs），但是当使用 out 参数时，你**必须显式设置类型的名称**。  
> 和 MyBatis 的其它部分一样，几乎总是可以根据参数对象的类型确定 javaType，除非该对象是一个 HashMap。这个时候，你需要显式指定 javaType 来确保正确的类型处理器（TypeHandler）被使用。
> 提示 JDBC 要求，如果一个列允许使用 null 值，并且会使用值为 null 的参数，就必须要指定 JDBC 类型（jdbcType）。阅读 PreparedStatement.setNull()的 JavaDoc 来获取更多信息。
>
> 如果参数的 mode 为 OUT 或 INOUT，将会修改参数对象的属性值，以便作为输出参数返回。   
> 如果 mode 为 OUT（或 INOUT），而且 jdbcType 为 CURSOR（也就是 Oracle 的 REFCURSOR），你必须指定一个 resultMap 引用来将结果集 ResultMap 映射到参数的类型上。  
> 要注意这里的 javaType 属性是可选的，如果留空并且 jdbcType 是 CURSOR，它会被自动地被设为 ResultMap。
>
>尽管上面这些选项很强大，但大多时候，你只须简单指定属性名，顶多要为可能为空的列指定 jdbcType，其他的事情交给 MyBatis 自己去推断就行了。

### 字符串替换`${...}`  
> 默认情况下，使用 #{} 参数语法时，MyBatis 会创建 PreparedStatement 参数占位符，并通过占位符安全地设置参数（就像使用 ? 一样）。   
> 这样做更安全，更迅速，通常也是首选做法，不过有时你就是想直接在 SQL 语句中直接插入一个不转义的字符串。 

- `${...}`：插入不转义的字符串。  
- `#{...}`：转义效果与JDBC中的预备语句的`?`参数的转义效果相同，本质上就是传入`?`参数。  

```java
public interface ExampleMapper{
    @Select("SELECT * FROM user WHERE ${column} = #{value}")
    User findByColumn(@Param("column") String column, @Param("value") String value);
}
public class Example{
    public static void main(String[] args){
        //...get SqlSession, getMapper。
        User userOfId1 = userMapper.findByColumn("id", 1L);
        User userOfNameKid = userMapper.findByColumn("name", "kid");
        User userOfEmail = userMapper.findByColumn("email", "noone@nowhere.com");
    }
}
```  
>示 用这种方式接受用户的输入，并用作语句参数是不安全的，会导致潜在的 SQL 注入攻击。  
> 因此，要么不允许用户输入这些字段，要么自行转义并检验这些参数。  

### 缓存  
**两种缓存**：
- **会话缓存**： 默认请况下，mybatis只启动了本地的会话缓存，仅仅对一个会话中数据进行缓存。  
- **二级缓存**：在SQL**映射文件中添加`<cache>`**，就可以开启**全局的**二级缓存。  

二级缓存的**作用**：  
- 映射语句**文件中的所有**`select`语句的**结果将被缓存**。  
- 映射语句**文件中的所有**`insert, update, delete`语句**会刷新缓存**。  
- 缓存会使用清楚算法**清除不需要的缓存**，默认使用最近最少算法。  
- 缓存会不定时**刷新**（也就是说没有刷新间隔）。  
- 缓存会保存对象或列表的**1024个引用**。  
- 缓存视为**读写缓存**，这意味着获取到的对象并不是共享的，可以安全地被调用者修改，而不干扰其他调用者或线程所做的潜在修改。  

二级缓存的**作用域**：
- 缓存**只作用于 cache 标签所在的映射文件中的语句**。  
- 如果你混合使用 Java API 和 XML 映射文件，在共用接口中的语句**将不会被默认缓存**。  
- 你需要使用**`@CacheNamespaceRef`**注解指定缓存作用域。  

二级缓存的**属性**：  
- **`eviction`**：缓存的清除策略。  
    - `LRU` – 最近最少使用：移除最长时间不被使用的对象。  
    - `FIFO` – 先进先出：按对象进入缓存的顺序来移除它们。  
    - `SOFT` – 软引用：基于垃圾回收器状态和软引用规则移除对象。  
    - `WEAK` – 弱引用：更积极地基于垃圾收集器状态和弱引用规则移除对象。  
- **`flushInterval`**：（刷新间隔）属性可以被设置为任意的正整数，设置的值应该是一个以毫秒为单位的合理时间量。 默认情况是不设置，也就是没有刷新间隔，缓存仅仅会在调用语句时刷新。  
- **`size`**：（引用数目）属性可以被设置为任意正整数，要注意欲缓存对象的大小和运行环境中可用的内存资源。默认值是 1024。  
- **`readOnly`**:（只读）属性可以被设置为 true 或 false。只读的缓存会给所有调用者返回缓存对象的相同实例。 因此这些对象不能被修改。这就提供了可观的性能提升。而可读写的缓存会（通过序列化）返回缓存对象的拷贝。 速度上会慢一些，但是更安全，因此默认值是 false。 
>提示 二级缓存是事务性的。这意味着，当 SqlSession 完成并提交时，或是完成并回滚，但没有执行 flushCache=true 的 insert/delete/update 语句时，缓存会获得更新。

#### 自定义缓存  
除了上述自定义缓存的方式，你也可以通过实现你自己的缓存，或为其他第三方缓存方案创建适配器，来完全覆盖缓存行为。  
- **`org.apache.ibatis.cache.Cache`**： **type 属性**指定的类必须实现`org.apache.ibatis.cache.Cache`接口，且提供一个接受 **`String`参数作为`id`** 的构造器。   
- 配置缓存：为了对你的缓存进行配置，只需要简单地在你的缓存**实现中添加公有的`JavaBean`属性**，然后**通过`cache`元素传递属性值**，例如，下面的例子将在你的缓存实现上调用一个名为 setCacheFile(String file) 的方法：
- 初始化方法`org.apache.ibatis.builder.InitializingObject`：从版本 3.4.2 开始，MyBatis 已经支持在所有属性设置完毕之后，调用一个初始化方法。 如果想要使用这个特性，请在你的自定义缓存类里**实现`org.apache.ibatis.builder.InitializingObject`接口**。

```xml
<cache type="com.domain.something.MyCustomCache">
    <property name="cacheFile" value="/tmp/my-custom-cache.tmp"/>
</cache>
```
```java
public interface Cache {
  String getId();
  int getSize();
  void putObject(Object key, Object value);
  Object getObject(Object key);
  boolean hasKey(Object key);
  Object removeObject(Object key);
  void clear();
}
public interface InitializingObject {
  void initialize() throws Exception;
}
```
>你可以使用所有简单类型作为 JavaBean 属性的类型，MyBatis 会进行转换。 你也可以使用占位符（如 ${cache.file}），以便替换成在配置文件属性中定义的值。 
>提示 上一节中对缓存的配置（如清除策略、可读或可读写等），不能应用于自定义缓存。 


**缓存、语句、命名空间**：
>请注意，缓存的配置和缓存实例会被绑定到 SQL 映射文件的命名空间中。 因此，同一命名空间中的所有语句和缓存将通过命名空间绑定在一起。 
>每条语句可以自定义与缓存交互的方式，或将它们完全排除于缓存之外，这可以通过在每条语句上使用两个简单属性来达成。 默认情况下，语句会这样来配置：
>鉴于这是默认行为，显然你永远不应该以这样的方式显式配置一条语句。但如果你想改变默认的行为，只需要设置 flushCache 和 useCache 属性。比如，某些情况下你可能希望特定 select 语句的结果排除于缓存之外，或希望一条 select 语句清空缓存。类似地，你可能希望某些 update 语句执行时不要刷新缓存。
```xml
<mapper namespace="...">
    <select flushCache="false" useCache="true"/>
    <insert flushCache="true"/>
    <update flushCache="true"/>
    <delete flushCache="true"/>
</mapper>
```

#### `cache-ref`
>回想一下上一节的内容，对某一命名空间的语句，只会使用该命名空间的缓存进行缓存或刷新。   
>但你可能会想要在多个命名空间中共享相同的缓存配置和实例。要实现这种需求，你可以使用 cache-ref 元素来引用另一个缓存。
```xml
<cache-ref namespace="com.someone.application.data.SomeMapper"/>
```

### 结果映射  
ResultMap 的设计思想是，对简单的语句做到零配置，对于复杂一点的语句，只需要描述语句之间的关系就行了。 

#### `resultType`简单映射  
- **映射到`HashMap`**: 简单地将所有的**列映射到`hashMap`的键**上，这由`resultType`属性指定。虽然在大部分情况下都够用，但是 HashMap **并不是一个很好的领域模型**。  
- **映射到`POJO`**：你的程序更可能会使用`JavaBean`或`POJO`（Plain Old Java Objects，普通老式 Java 对象）作为领域模型。MyBatis **对两者都提供了支持**。  
- **映射机制**：  
    - **创建`ResultMap`**：在这些情况下，MyBatis 会在**幕后自动创建一个 ResultMap**，
    - **属性名对应到列名**：再根据**属性名来映射列**到 JavaBean 的属性上。  
    > 如果列名和属性名不能匹配上，可以在 SELECT 语句中*设置列别名**（这是一个基本的 SQL 特性）来完成匹配。  
```xml
<mapper naemspace="...">
    <!--隐射到HashMap-->
    <select id="selectUsers" resultType="map">
        SELECT id, username, hashedPassword 
        FROM some_table 
        WHERE id = #{id} 
    </select>
    <!--映射到POJO-->
    <select id="selectUsers" resultType="User">
        SELECT id, username, hashedPassword
        FROM some_table
        WHERE id = #{id} 
    </select>
    <!--设置列别名以匹配对象属性名-->
    <select id="selectUsers" resultType="User">
        SELECT 
            user_id AS "id",
            user_name AS "userName",
            hashed_password AS "hashedPassword" 
        FROM some_table 
        WHERE id = #{id}    
    </select>
</mapper>
```

#### `<resultMap>`结果映射
> 显式使用外部的 resultMap 会怎样，这也是解决列名不匹配的另外一种方式。
- **`<resultMap>`**： 定义结果映射。  
- **`<select>`的`resultMap`**属性：引用结果映射，使用它的规则映射结果。    
```xml
<mapper namespace="...">
    <!--resultMap元素-->
    <resultMap id="userResultMap" type="User">
        <id property="id" column="user_id"/>
        <result property="username" column="user_name"/>
        <result property="password" column="hashed_password"/>
    </resultMap>
    <!--resultMap属性-->
    <select id="selectUsers" resultMap="userResultMap">
        SELECT user_id, user_name, hashed_password 
        FROM some_table 
        WHERE id = #{id}
    </select>
</mapper>
```

#### 高级结果映射  
>resultMap 元素有很多子元素和一个值得深入探讨的结构。 下面是resultMap 元素的概念

##### `<resultMap>`的子元素：
- `<constructor>`: 用于在实例化类时，注入结果到构造方法中。  
    - `<idArg>`: ID 参数；标记出作为 ID 的结果可以帮助提高整体性能。  
    - `<arg>`: 将被注入到构造方法的一个普通结果。  
- `<id>`: 一个 ID 结果；标记出作为 ID 的结果可以帮助提高整体性能。  
- `<result>`: 注入到字段或 JavaBean 属性的普通结果。  
- `<association>`: 一个复杂类型的关联；许多结果将包装成这种类型。  
    - 嵌套结果映射 – 关联可以是 resultMap 元素，或是对其它结果映射的引用。  
- `<collection>`: 一个复杂类型的集合。  
    - 嵌套结果映射 – 集合可以是 resultMap 元素，或是对其它结果映射的引用。  
- `<discriminator>`: 使用结果值来决定使用哪个 resultMap。  
    - `<case>`: 基于某些值的结果映射。 
        - 嵌套结果映射 – case 也是一个结果映射，因此具有相同的结构和元素；或者引用其它的结果映射。  
        
##### `<resultMap>`的属性  
- `id`: 当前命名空间中的一个唯一标识，用于标识一个结果映射。  
- `type`: 类的完全限定名, 或者一个类型别名（关于内置的类型别名，可以参考上面的表格）。  
- `autoMapping`: 如果设置这个属性，MyBatis 将会为本结果映射开启或者关闭自动映射。 这个属性会覆盖全局的属性 autoMappingBehavior。默认值：未设置（unset）。  

##### `<id>`、`<result>`  
- **结果映射的基础**：`<id>`和`<result>`元素都将一个**列的值映射到一个简单数据类型**（String, int, double, Date 等）的属性或字段。
- 这两者之间的唯一不同是: **`<id`>**元素对应的属性会被**标记为对象的标识符**，在比较对象实例时使用。  
    > 这样可以提高整体的性能，尤其是进行缓存和嵌套结果映射（也就是连接映射）的时候。  

`<id>`和`<result>`的属性：  
- `property`: 映射到列结果的字段或属性。
- `column`: 数据库中的列名，或者是列的别名。 一般情况下，这和传递给 resultSet.getString(columnName) 方法的参数一样。  
- `javaType`: 一个 Java 类的全限定名，或一个类型别名（关于内置的类型别名，可以参考上面的表格）。 
    > 如果你**映射到一个`JavaBean`**，`MyBatis`通常可以**推断类型**。  
    > 然而，如果你**映射到的是`HashMap`**，那么你应该**明确地指定`javaType`**来保证行为与期望的相一致。  
- `jdbcType`: JDBC 类型，所支持的 JDBC 类型参见这个表格之后的“支持的 JDBC 类型”。  
    > 只需要在可能执行插入、更新和删除的**且允许空值的列**上指定 JDBC 类型。  
    > 这是 JDBC 的要求而非 MyBatis 的要求。如果你直接面向 JDBC 编程，你需要对可以为空值的列指定这个类型。  
- `typeHandler`: **覆盖默认的类型处理器**，这个属性值是一个类型处理器实现类的全限定名，或者是类型别名。  

##### `<constructor>`与构造方法  
- 构造方法注入允许你在初始化时为类设置属性的值，而**不用暴露出公有方法**。  
- MyBatis 也支持私有属性和私有 JavaBean 属性来完成注入，但有一些人更青睐于通过构造方法进行注入。   
> constructor 元素就是为此而生的。  

**`<constructor>`用法**：  
>为了将结果注入构造方法，MyBatis 需要通过某种方式定位相应的构造方法。 
- 用**类型和顺序**确定形参：例子中，MyBatis 搜索一个声明了三个形参的构造方法，参数类型以`java.lang.Integer`, `java.lang.String` 和`int`的**顺序给出**。  
- 任意顺序下**用名字来引用**参数：从版本 3.4.3 开始，可以在使用**`name`属性指定参数名**来引用参数的前提下，以任意顺序编写 arg 元素，
    - **`@Param`**注解：为了通过名称来引用构造方法参数，你可以**添加`@Param`注解**，
    - **`'-parameters'`**编译选项： 或者使用**`'-parameters'`**编译选项并启用**`useActualParamName`**选项（默认开启）来编译项目。  
```java
public class User{
    public User(Integer id, String username, int age){
    }
}
```
```xml
<constructor>
    <idArg column="id" javaType="int"/>
    <arg column="username" javaType="String"/>
    <arg column="age" javaType="_int"/>
</constructor>
```
```xml
<constructor>
    <idArg column="id" javaType="int" name="id"/>
    <arg column="age" javaType="_int" name="age"/>
    <arg column="username" javaType="String" name="username"/>
</constructor>
```

`<constructor>`的**子元素属性**：
- `column`: 数据库中的列名，或者是列的别名。一般情况下，这和传递给 resultSet.getString(columnName) 方法的参数一样。  
- `javaType`: 一个 Java 类的完全限定名，或一个类型别名（关于内置的类型别名，可以参考上面的表格）。   
    > 如果你映射到一个 JavaBean，MyBatis 通常可以推断类型。 
    > 然而，如果你映射到的是 HashMap，那么你应该明确地指定 javaType 来保证行为与期望的相一致。  
- `jdbcType`: JDBC 类型，所支持的 JDBC 类型参见这个表格之前的“支持的 JDBC 类型”。   
    > 只需要在可能执行插入、更新和删除的且允许空值的列上指定 JDBC 类型。  
    > 这是 JDBC 的要求而非 MyBatis 的要求。如果你直接面向 JDBC 编程，你需要对可能存在空值的列指定这个类型。 
- `typeHandler`: 覆盖默认的类型处理器。 
- `select`: 用于加载**复杂类型属性**的映射语句的 ID，它会从`column`属性中指定的**列检索数据**，作为**参数传递**给此`select`语句。具体请参考关联元素。
- `resultMap`: 结果映射的 ID，可以将**嵌套的结果集**映射到一个合适的对象树中。  
    > 它可以作为使用额外 select 语句的替代方案。 
    > 它可以将**多表连接操作的结果**映射成一个**单一的 ResultSet**。这样的 ResultSet 将会将包含重复或部分数据重复的结果集。  
    > 为了将结果集正确地映射到嵌套的对象树中，MyBatis 允许你 “串联”结果映射，以便解决嵌套结果集的问题。  
- `name`: 构造方法形参的名字。从 3.4.3 版本开始，通过指定具体的参数名，你可以以任意顺序写入 arg 元素。 

##### 关联 
>多表连接的时候一定会有连接点，这个连接点就是关联对相应的数据库列
>例如表A.b_id, 表B.b_id, 则关联 select A.b_id as a_b_id, B.b_id where A.b_id == B.b_id。 对于对象A,有一个B类型的属性b，则A对象的域b对应数据库列a_b_id。  
>关联（association）元素处理“有一个”类型的关系。  

**两种方式加载关联**：  
- **嵌套Select查询**：通过执行另外一个 SQL 映射语句来加载期望的复杂类型。  
- **嵌套结果映射**：使用嵌套的结果映射来处理连接结果的重复子集。  

**`<association>`属性与关联**：  
- `property`: 映射到列结果的字段或属性。
- `javaType`: 一个 Java 类的完全限定名，或一个类型别名。一般mybatis可以自动推断，若映射到HashMap则要明确指定javaType。  
- `jdbcType`: JDBC类型。只需要在可能执行插入、更新和删除的且允许空值的列上指定 JDBC 类型。 
- `typeHandler`: 类型处理器实现类的完全限定名或别名， 覆盖默认的类型处理器。 
- 嵌套Select查询所用的属性： 
    - `column`：数据库中的列名。复合主键语法`column="{prop1=col1,prop2=col2}"`,prop作为传入Select查询语句的参数对象。  
    - `select`: 用于加载复杂类型属性的映射语句的 ID，它会从 column 属性指定的列中检索数据，作为参数传递给目标 select 语句。  
    - `fetchType`: 可选的。有效值为 lazy 和 eager。 指定属性后，将在映射中忽略全局配置参数 lazyLoadingEnabled，使用属性的值。
- 嵌套结果映射所用的属性： 
    - `resultMap`: 结果映射的 ID,将多表连接操作的结果映射成一个单一的 ResultSet。这样的 ResultSet 有部分数据是重复的。 为了将结果集正确地映射到嵌套的对象树中, MyBatis 允许你“串联”结果映射。  
    - `columnPrefix`: 连接多表是可能使用别名，若**别名命名为"<前缀>列名"**，则可以通过`columnPrefix`将这些带有前缀的列映射到结果映射（该结果映射中必然不是使用别名而是使用原名）。  
    - `notNullColumn`: 指定**非空列（逗号分隔多列）**。只有在这些列非空的时候才创建子对象。默认情况下，在至少一个被映射到属性的列不为空时，子对象才会被创建。默认值：未设置（unset）。  
    - `autoMapping`: 覆盖全局的属性`autoMappingBehavior`, 为本结果映射**开启或者关闭自动映射**,对外部的结果映射无效，所以不能搭配 select 或 resultMap 元素使用。默认值：未设置（unset）。
- 关联的多结果集所用的属性： 
    - `column`: 当使用多个结果集时，该属性(被参考系的属性)指定结果集中用于与foreignColumn 匹配的列（多个列名以逗号隔开），以识别关系中的父类型与子类型。  
    - `foreignColumn`: 指定(参考系的参考属性)外键对应的列名，指定的列将**与父类型中 column**的给出的列进行匹配（子类型作为父类的一个属性储存在父类型中）。  
    - `resultSet`: 指定用于加载复杂类型的结果集名字。  
> 嵌套Select查询虽然很简单，但在大型数据集或大型数据表上表现不佳。这个问题被称为“N+1 查询问题”。 概括地讲，N+1 查询问题是这样子的：  
> 你执行了一个单独的 SQL 语句来获取结果的一个列表（就是“+1”）。  
> 对列表返回的每条记录，你执行一个 select 查询语句来为每条记录加载详细信息（就是“N”）。  
> 嵌套结果映射则没有这个问题，因为它是将连接查询结果映射到关联，而不是1+N查询。  
```xml
<!--嵌套Select查询-->
<mapper namespace="..."> 
    <resultMap id="blogResult" type="Blog">
        <association property="author" column="author_id" javaType="Author" select="selectAuthor"/>
    </resultMap>
    
    <select id="selectBlog" resultMap="blogResult">
        SELECT * FROM BLOG WHERE ID = #{id}
    </select>
    
    <select id="selectAuthor" resultType="Author">
        SELECT * FROM AUTHOR WHERE ID = #{id}
    </select>

</mapper>
```
```xml
<!--嵌套结果映射-->
<mapper namespace="...">
    <select id="selectBlog" resultMap="blogResult">
        SELECT 
            B.id            AS blog_id,
            B.title         AS blog_title,
            B.author_id     AS blog_author_id,
            A.id            AS author_id,
            A.username      AS author_username,
            A.password      AS author_password,
            A.email         AS author_email,
            A.bio           AS author_bio 
        FROM Blog B LEFT OUTER JOIN Author A ON B.author_id = A.id  
    </select>
    <resultMap id="blogResult" type="Blog">
        <id property="id" column="blog_id"/>
        <result property="title" column="blog_title"/>
        <association property="author" column="blog_author_id" javaType="Author" resultMap="authorResult"/>
    </resultMap>
    <resultMap id="authorResult" type="Author">
        <id property="id" column="author_id"/>
        <result property="username" column="author_username"/>
        <result property="password" column="author_password"/>
        <result property="email" column="author_email"/>
        <result property="bio" column="author_bio"/>
    </resultMap>
<!--或者 可以直接将结果映射作为子元素嵌套在内-->
 <resultMap id="blogResult" type="Blog">
        <id property="id" column="blog_id"/>
        <result property="title" column="blog_title"/>
        <association property="author" javaType="Author">
            <id property="id" column="author_id"/>
            <result property="username" column="author_username"/>
            <result property="password" column="author_password"/>
            <result property="email" column="author_email"/>
            <result property="bio" column="author_bio"/>
          </association>
    </resultMap>
</mapper>
```
```xml
<!--使用别名前缀 处理连接时所用别名，从而匹配结果映射中不同的列名-->
<mapper namespace="...">
    <select id="selectBlog" resultMap="blogResult">
      select
        B.id            as blog_id,
        B.title         as blog_title,
        A.id            as author_id,
        A.username      as author_username,
        A.password      as author_password,
        A.email         as author_email,
        A.bio           as author_bio,
        CA.id           as co_author_id,
        CA.username     as co_author_username,
        CA.password     as co_author_password,
        CA.email        as co_author_email,
        CA.bio          as co_author_bio
      from Blog B
      left outer join Author A on B.author_id = A.id
      left outer join Author CA on B.co_author_id = CA.id
      where B.id = #{id}
    </select>
    <resultMap id="blogResult" type="Blog">
      <id property="id" column="blog_id" />
      <result property="title" column="blog_title"/>
      <association property="author"
        resultMap="authorResult" />
      <association property="coAuthor"
        resultMap="authorResult"
        columnPrefix="co_" /><!--标识别名前缀，从而匹配结果映射-->
    </resultMap>

    <resultMap id="authorResult" type="Author">
        <id property="id" column="author_id"/>
        <result property="username" column="author_username"/>
        <result property="password" column="author_password"/>
        <result property="email" column="author_email"/>
        <result property="bio" column="author_bio"/>
    </resultMap>
</mapper>
```
```xml
<!--设有储存过程如下-->
<!--CREATE PROCEDURE getBlogAndAuthros(IN INT id){-->
<!--    SELECT * FROM BLOG WHERE ID = #{id};-->
<!--    SELECT * FROM AUTHOR WHERE ID = #{id};-->
<!--}-->
<mapper namespace="...">
<!--多结果集时，在映射语句，必须通过 resultSets 属性为每个结果集指定一个名字，多个名字使用逗号隔开。-->
    <select id="selectBlog" resultSets="blogs,authors" resultMap="blogResult" 
        statementType="CALLABLE">
    {CALL getBlogsAndAuthors(#{id, jdbcType=INTEGER, mode=IN})}
    </select>
    <resultMap id="blogResult" type="Blog">
        <id property="id" column="id"/>
        <result property="title" column="title"/>
        <association property="author" javaType="Author" 
        column="author_id" foreignColumn="id">
            <id property="id" column="id"/>
            <result property="username" column="username"/>
            <result property="password" column="password"/>
            <result property="email" column="email"/>
            <result property="bio" column="bio"/>
        </association>
    </resultMap>
</mapper>
```

##### 集合  
关联处理“有一个”类型的关联，集合处理“有多个”类型的关联。 
> 集合元素和关联元素几乎是一样的，它们相似的程度之高，以致于没有必要再介绍集合元素的相似部分。  
> 所以让我们来关注它们的不同之处吧。
>继续上面的示例，一个博客（Blog）只有一个作者（Author)。但一个博客有很多文章（Post)。 在博客类中，这可以用下面的写法来表示：
>`private List<Post> posts;`

**两种方式加载集合**：  
- **嵌套Select查询**：通过执行另外一个 SQL 映射语句来加载期望的复杂类型。  
- **嵌套结果映射**：使用嵌套的结果映射来处理连接结果的重复子集。  

**`<collection>`属性与集合**：
- `ofType`属性：这个属性非常重要，它用来将 JavaBean（或字段）**属性的类型**和集合**存储的类型**区分开来。  
- 其他属性与关联`<association>`相同。  

```xml
<!--集合的嵌套 Select 查询-->
<mapper namespace="...">
    <resultMap id="blogResult" type="Blog">
        <collection property="posts" javaType="ArrayList" column="id" 
    ofType="Post" select="selectPostsForBlog"/>
    </resultMap>
    <select id="selectBlog" resultMap="blogResult">
        SELECT * FROM BLOG WHERE ID = #{id}
    </select>
    <select id="selectPostsForBlog" resultType="Post">
        SELECT * FROM POST WHERE BLOG_ID = #{id}
    </select>
</mapper>
```
```xml
<!--集合的嵌套结果映射-->
<mapper namespace="...">
     <select id="selectBlog" resultMap="blogResult">
         SELECT 
            B.id            AS blog_id,
            B.title         AS blog_title,
            B.author_id     AS blog_author_id,
            P.id            AS post_id,
            P.subject       AS post_subject,
            P.body          AS post_body,
            FROM Blog B LEFT OUTER JOIN Post P ON B.author_id = p.blog_id  
     </select>
    <resultMap id="blogResult" type="Blog">
        <id property="id" column="blog_id"/>
        <result property="title" column="blog_title"/>
        <collection property="author" ofType="Post" reusltMap="blogPostResult" columnPrefix="post_"/>
    </resultMap>
    <resultMap id="blogPostResult" type="Post">
        <id preperty="id" column="id"/>
        <result property="subject" column="subject"/>
        <result property="body" column="body"/>
    </resultMap>
</mapper>
```
```xml
<!--设有储存过程如下-->
<!--CREATE PROCEDURE getBlogAndAuthros(IN INT id){-->
<!--    SELECT * FROM BLOG WHERE ID = #{id};-->
<!--    SELECT * FROM POST WHERE BLOG_ID = #{id};-->
<!--}-->
<mapper namespace="...">
    <select id="selectBlog" resultSets="blogs,posts" resultMap="blogResult" 
        statementType="CALLABLE">
    {CALL getBlogsAndAuthors(#{id, jdbcType=INTEGER, mode=IN})}
    </select>
    <resultMap id="blogResult" type="Blog">
        <id property="id" column="id"/>
        <result property="title" column="title"/>
        <collection property="posts" ofType="Post" 
        resultSet="posts" column="id" foreignColumn="blog_id">
            
        </collection>
    </resultMap>
</mapper>
```

##### 鉴别器  
> 有时候，一个数据库查询可能会返回多个不同的结果集（但总体上还是有一定的联系的）。  
> 鉴别器（discriminator）元素就是被设计来应对这种情况的，另外也能处理其它情况，例如类的继承层次结构。   
> 鉴别器的概念很好理解——它很像 Java 语言中的 switch 语句。

> 在这个示例中，MyBatis 会从结果集中得到每条记录，然后比较它的 vehicle type 值。   
> 如果它匹配任意一个鉴别器的 case，就会使用这个 case 指定的结果映射。   
> 这个过程是互斥的，也就是说，剩余的结果映射将被忽略（除非它是扩展的，我们将在稍后讨论它）。   
> 如果不能匹配任何一个 case，MyBatis 就只会使用鉴别器块外定义的结果映射。   

**鉴别器属性**： 
- `column`: 指定了 MyBatis 查询**被比较**值的地方。  
- `javaType`: 用来确保使用**正确的相等测试**（虽然很多情况下字符串的相等测试都可以工作）。  

```xml
<mapper namespace="...">
    <resultMap id="vehicleResult" type="Vehicle">
        <id property="id" column="id"/>
        <result property="vin" column="vin"/>
        <result property="year" column="year"/>
        <result property="make" column="make"/>
        <result property="mode1" column="mode1"/>
        <result property="color" column="color"/>
        <discriminator column="vehicle_type" javaType="int">
            <case value="1" resultType="carResult">
                <result property="doorCount" column="door_count"/>
            </case>
            <case column="2" resultType="truckResult">
                <result property="boxSize" column="box_size"/>
                <result property="extendedCab" column="extended_cab"/>
            </case>
            <case value="3" resultMap="vanResult"/>
            <case value="4" resultMap="suvResult"/>
        </discriminator>
    </resultMap>
</mapper>
```

#### 自动映射  

**自动映射和结果映射**：  
> 在简单的场景下，MyBatis 可以为你自动映射查询结果。  
> 但如果遇到复杂的场景，你需要构建一个结果映射。  
> 可以混合使用这两种策略。

**自动映射原理**：  
> 当自动映射查询结果时，MyBatis 会获取结果中返回的列名并在 Java 类中查找相同名字的属性（忽略大小写）。   
> 这意味着如果发现了 ID 列和 id 属性，MyBatis 会将列 ID 的值赋给 id 属性。  

**命名对应规则**：  
> 通常数据库列使用大写字母组成的单词命名，单词间用**下划线分隔**；  
> 而 Java 属性一般遵循驼峰命名法约定。  
- **`mapUnderscoreToCamelCase`设置**： 为了在这两种命名方式之间启用自动映射，需要将 mapUnderscoreToCamelCase 设置为 true。 
- **映射等级**：  
    - `NONE`: 禁用自动映射。仅对手动映射的属性进行映射。  
    - `PARTIAL`: 对除在内部定义了嵌套结果映射（也就是连接的属性）以外的属性进行映射。  
    - `FULL` - 自动映射所有属性。 
>默认值是 PARTIAL，这是有原因的。当对连接查询的结果使用 FULL 时，连接查询会在同一行中获取多个不同实体的数据，因此可能导致非预期的映射。 
>无论设置的自动映射等级是哪种，你都可以通过在结果映射上设置 autoMapping 属性来为指定的结果映射设置启用/禁用自动映射。  

**混合映射说明**：  
> 甚至在提供了结果映射后，自动映射也能工作。  
> 在这种情况下，对于每一个结果映射，在 ResultSet 出现的列，如果没有设置手动映射，将被自动映射。  
> 在自动映射处理完毕后，再处理手动映射。   

```xml
<mapper namesapce="...">
    <select id="selectUsers" resultMap="userResultMap">
        SELECT 
            user_id     AS id,
            user_name   AS username,
            hashed_password
        FROM some_table
        WHERE id = #{id}
    </select>
    <resultMap id="userResultMap" type="User">
        <result property="password" column="hashed_password"/>
    </resultMap>
</mapper>
```
 

