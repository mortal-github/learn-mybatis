## Dynamic SQL  

### XML中建立动态SQL  
建立动态SQL只需要4个元素：  
- `<if>`元素：根据条件是否使用SQL语句片段。  
- `<choose>,<when>, <otherwise>`元素：根据条件从**多个**语句片段中**选择一个**。  
- `<trim,<where>,<set>`元素：**自动增删**语句片段中的**一些关键词**，保证SQL语句**合法语法**。（上述两个语句，可能在增删某些子句后造成语法错误）  
- `<foreach>`元素：多用于`IN`条件。遍历集合元素自动生成所有元素的SQL语句片段，以便将其作为`IN`条件。  

`<if>`元素：
- **`<if>`**元素：**包裹需要动态出现的SQL**语句，**根据条件选择**是否出现SQL语句。  
- `<if>`元素的**`test`属性**：`test`属性**包含条件**，判断是否使用这条SQL子句。  
```xml
<select id="findActiveBlogLike" resultType="Blog">
    SELECT * 
    FROM BLOG 
    WHERE state = "ACTIVE"  
    <if test="title != null">
    AND title LIKE #{title}
    </if>
</select>
```  

`<choose>,<when>,<otherwise>`元素：
- **`<choose>`**元素：根据条件从**多个**语句片段中**选择一个**。  
   - `<choose>`的子元素**`<when>`**元素：根据**`test`**属性判断是否使用这条SQL语句片段，若是，则忽略其他选项。  
   - `<choose>`的子元素**`<otherwise>`**元素： 若没有任何一个`<when>`元素被选中，则使用`<otherwise>`元素。  
```xml
<select id="findActiveBlogLike" resultType="Blog">
    SELECT * 
    FROM BLOG 
    WHERE state = "ACTIVE" 
    <choose>
        <when test="title != null">
        AND title like #{title} 
        </when>
        <when test="author != null and author.name author.name != null">
        AND author_name like #{authro.name} 
        </when>
        <otherwise>
            AND featured = 1 
        </otherwise>
    </choose>
</select>
``` 

`<if>,<choose>,<when>,<otherwise>`的问题：  
- **动态选择不当造成SQL语法问题**：简单地选择SQL语句可能导致语法问题。 例如`WHEN`子句没有条件，`WHEN`后跟`AND |OR `等错误问题。  
```xml
<select id="findActiveBlogLike" resultType="Blog">
    SELECT * 
    FROM BLOG 
    WHERE 
    <if test="state != null">
        state = #{state}
    </if>
    <if test="title != null">
        AND title LIKE #{title}
    </if>
    <if test="author != null and authro.name != null">
        AND author_name LIKE #{author.name}
    </if>
</select>
```
```SQL
//没有匹配条件造成这样子的错误SQL语句。
SELECT * 
FROM BLOG 
WHERE 
//没有匹配第1个条件造成这样子错误的SQL语句。  
SELECT * 
FROM BLOG 
WHERE 
    AND title LIKE "someTitle"
```

`<trim>,<where>,<set>`元素： 
- **增删关键字解决语法问题**：`<trim>,<where>,<set>`可通过增删前后缀来**修正SQL语句**。  
- **`<where>`**元素：`<where>`元素**自动插入`WHERE`**关键词，**自动删除`AND`,`OR`等连结词**来修正语法。  
    - 插入`WHERE`关键词： **只会在子元素返回**任何内容的情况下才插入`WHERE`关键词。（从而与子元素返回的子句构成完整的`WHERE`语句）。  
    - 删除连结词：若返回子句开头为`AND`或`OR`，`<where>`元素**自动删除开头的连结词**。  
- **`<set>`**元素： 用于动态更新语句中的列。与`<where>`元素类似，`<set>`元素自动增加`SET`关键词，自动删除多余的`,`。  
    - 插入`SET`关键词：`<set>`元素会动态地在行首插入SET关键字。 
    - 删除额外逗号：并会删掉额外的逗号（这些逗号是在使用条件语句给列赋值时引入的）。  
- **`<trim>`**元素：自定义增删规则，通过在返回子句中增闪前后缀来修正语法。`<where>`,`<set>`都可以用`<trim>`来表示。  
    - `prefix`属性：自动增加的前缀。  
    - `suffix`属性：自动增加的后缀。  
    - `prefixOverrides`：被自动删除的前缀，**`|`分隔多个**文本序列，每个**文本序列后面要有空格**。  
    - `suffixOverrides`: 被自动删除的后缀。 
```xml
<trim prefix="WHERE" prefixOverrides="AND |OR ">
    <!--与<WHERE>效果等同-->
</trim>
```
```xml
<trim prefix="SET" suffixOverrides=",">
    <!--与<set>效果相同-->
</trim>
```
```xml
<select id="findActiveBlogLke" resultType="Blog">
    SELECT * 
    FROM BLOG 
    <where>
        <if test="state != null">
            state = #{state}
        </if>
        <if test="title != null">
            AND title LIKE #{title}
        </if>  
        <if test="author != null and author.name != null">
            AND author_name LIKE #{author.name}
        </if>  
    </where>
</select>
```
```xml
<update id="updateAuthorIfNecessary">
    UPDATE Author 
    <set>
        <if test="username != null">username=#{username},</if>
        <if test="password != null">password=#{password},</if>
        <if test="email != null">email=#{email},</if>
        <if test="bio != null">bio=#{bio}</if>
    </set>
</update>
```
`<foreach>`元素：
- `<foreach>`元素遍历集合参数，利用每一个项构造合适的SQL语句片段，常用于与`IN`条件。  
>foreach 元素的功能非常强大，它允许你指定一个集合，
>声明可以在元素体内使用的集合项（item）和索引（index）变量。
>它也允许你指定开头与结尾的字符串以及集合项迭代之间的分隔符。
>这个元素也不会错误地添加多余的分隔符，看它多智能！

>提示示 你可以将任何可迭代对象（如 List、Set 等）、Map 对象或者数组对象作为集合参数传递给 foreach。
>当使用可迭代对象或者数组时，index 是当前迭代的序号，item 的值是本次迭代获取到的元素。
>当使用 Map 对象（或者 Map.Entry 对象的集合）时，index 是键，item 是值。
```xml
<select id="selectPostIn" resultType="domain.blog.Post">
    SELECT * 
    FROM POST P 
    WHERE ID IN 
    <foreach item="item" index="index" collection="list" 
        open="(" separator="," close=")">
        #{item}
    </foreach>
</select>
```
### `<script>`元素在Java代码中建立动态SQL  
- **注解中使用`<script>`**：在**映射器接口中的注解**可以使用`<script>`**实现动态SQL**。  
```java
class Example{
    @Update({"<script>",
      "update Author",
      "  <set>",
      "    <if test='username != null'>username=#{username},</if>",
      "    <if test='password != null'>password=#{password},</if>",
      "    <if test='email != null'>email=#{email},</if>",
      "    <if test='bio != null'>bio=#{bio}</if>",
      "  </set>",
      "where id=#{id}",
      "</script>"})
    void updateAuthorValues(Author author){}
}
```

### `<band>`元素绑定SQL语句的外部变量  
- **`<band>`**元素：允许在OGNL表达式**以外创建**一个变量，并将其**绑定到上下文中**。  
```xml
<select id="selectBlogsLike" resultType="Blog">
    <bing naem="pattern" value="'%' + _parameter.getTitle() + '%'"/>
    SELECT * 
    FROM BLOG 
    WHERE title LIKE #{pattern} 
</select>
```
###多数据库支持  
- 如果**配置了`databaseIdProvider`**，  
- 你就可以在**动态代码中**使用名为**`“_databaseId”`**的变量来为不同的数据库构建特定的语句。  
```xml
<insert id="insert">
    <selectKey keyProperty="id" resultType="int" order="BEFORE">
        <if test="_databaseId == 'oracle'">
            SELECT seq_users.nextval FROM dual 
        </if>
        <if test="_databaseId == 'db2' ">
            SELECT nextval for seq_users from sysibm.sysdummy1
        </if>
    </selectKey>
    INSERT 
    INTO users 
    VALUES (#{id}, #{name}) 
</insert>
```
### 使用自定义脚本语言构建动态SQL  
- MyBatis 从 3.2 版本开始支持插入脚本语言，这允许你插入一种语言驱动，  
- 并**基于这种语言来编写动态 SQL 查询语句**。  
- 可以通过**实现`LanguageDriver`接口**来插入一种语言。 
- 设置语言：  
    - 设置为**默认语言**：实现自定义语言驱动后，在配置文件中将它设置为默认语言。  
    - **`lang`属性**为特定语句指定语言：或者，你也可以使用 lang 属性为特定的语句指定语言。  
    - **@Lang**注解为映射器接口映射语句指定特定语言：或者，在你的 mapper 接口上添加 @Lang 注解。  
```java
public interface LanguageDriver{
    ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql); 
    SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType);
    SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType);
}
```
```xml
<!--指定默认语言-->
<configuration>
    <typeAliases>
        <typeAlias type="package.MyLanguageDriver" alias="myLanguage"/>
    </typeAliases>
    <settings>
        <setting name="defaultScriptLanguage" value="myLanguage"/>
    </settings>
</configuration>
```
```xml
<!--特定语句指定语言-->
<select id="selectBlog" lang="myLanguage">
    SELECT * FROM BLOG 
</select>
```
```java
public interface Mapper{
    @Lang(MyLanguageDriver.class)
    @Select("SELECT * FROM BLOG")
    List<BLog> selectBlog();
}
```
