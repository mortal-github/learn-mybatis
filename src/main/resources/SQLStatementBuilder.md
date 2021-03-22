## SQL 语句构建器  
- XML映射器构建动态SQL语句：MyBatis 在 XML 映射中具备强大的 SQL 动态生成能力。  
- Java代码中构建动态SQL语句： 但有时，我们还是需要在 Java 代码里构建 SQL 语句。  
- **SQL语句构建器高效构建动态SQL语句**：此时，MyBatis 有另外一个特性可以帮到你，让你从处理典型问题中解放出来，比如加号、引号、换行、格式化问题、嵌入条件的逗号管理及 AND 连接。  
```java
```
### `SQL`类  
`SQL`类： 
- 构建SQL语句： 借助 SQL 类，我们只需要简单地创建一个实例，并调用它的方法即可生成 SQL 语句。  
- 自动处理关键词：使用SQL类型构建SQL语句会**自动处理关键词**。  
    > 不用担心可能会重复出现的 "AND" 关键字，或者要做出用 "WHERE" 拼接还是 "AND" 拼接还是不用拼接的选择。 
    > SQL 类已经为你处理了哪里应该插入 "WHERE"、哪里应该使用 "AND" 的问题，
- 自动拼接字符串：并帮你完成所有的字符串拼接工作。  

**`SQL`类构建SQL语句示例**：  
```java
public class ExampleSQL {
    //匿名内部类风格
    public String deletePersonalSQL(){
        return new SQL(){
            {
                this.DELETE_FROM("PERSON");
                this.WHERE("ID = #{id}");
            }
        }.toString();
    }
    //Builder/Fluent风格
    public String insertPersonSql(){
        String sql = new SQL()
                .INSERT_INTO("PERSON")
                .VALUES("ID, FIRSTNAME", "#{id}, #{firstName}")
                .VALUES("LAST_NAME", "#{lastName}")
                .toString();
        return sql;
    }
    //动态条件（注意参数需要使用 final 修饰，以便匿名内部类对它们进行访问）。
    public String selectPersonLike(final String id, final String firstName, final String lastName){
        return new SQL(){
            {
                this.SELECT("P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME");
                this.FROM("PERSON P");
                if(id != null){
                    WHERE("P.ID LIKE #{id}");
                }
                if(firstName != null){
                    WHERE("P.FIRST_NAME LIKE #{firstName}");
                }
                if(lastName != null){
                    WHERE("P.LAST_NAME LIKE #{lastName}");
                }
                this.ORDER_BY("P.LAST_NAME");
            }
        }.toString();
    }
    
    public String updatePersonSql(){
        return new SQL(){
            {
                UPDATE("PERSON");
                SET("FIRST_NAME = #{firstName}");
                WHERE("ID = #{id}");
            }
        }.toString();
    }
}
```

**`SQL`类API**: 
> SQL 类将原样插入 LIMIT、OFFSET、OFFSET n ROWS 以及 FETCH FIRST n ROWS ONLY 子句。  
> 换句话说，类库不会为不支持这些子句的数据库执行任何转换。   
> 因此，用户应该要了解目标数据库是否支持这些子句。如果目标数据库不支持这些子句，产生的 SQL 可能会引起运行错误。

### `SqlBuilder`和`SelectBuiler`(已废弃)
> 在版本 3.2 之前，我们的实现方式不太一样，我们利用 ThreadLocal 变量来掩盖一些对 Java DSL 不太友好的语言限制。  
> 现在，现代 SQL 构建框架使用的构建器和匿名内部类思想已被人们所熟知。  
> 因此，我们废弃了基于这种实现方式的 SelectBuilder 和 SqlBuilder 类。  
- `BEGIN()`方法：清空 SelectBuilder 类的 ThreadLocal 状态，并准备好构建一个新的语句。  
    > 开始新的语句时，BEGIN() 是最名副其实的（可读性最好的）。  
- `RESET()`方法: 在执行过程中要重置语句构建状态，就很适合使用`RESET()`（比如程序逻辑在某些条件下需要一个完全不同的语句）。  
- `SQL()`方法:该方法返回生成的 SQL() 并重置 SelectBuilder 状态（等价于调用了 BEGIN() 或 RESET()）。因此，该方法只能被调用一次！  

```java
import static org.apache.ibatis.jdbc.SelectBuilder.*;
import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class Example{
    public String selectBlogsSql() {
      BEGIN(); // 重置 ThreadLocal 状态变量
      SELECT("*");
      FROM("BLOG");
      return SQL();
    }
    
    private String selectPersonSql() {
      BEGIN(); // 重置 ThreadLocal 状态变量
      SELECT("P.ID, P.USERNAME, P.PASSWORD, P.FULL_NAME");
      SELECT("P.LAST_NAME, P.CREATED_ON, P.UPDATED_ON");
      FROM("PERSON P");
      FROM("ACCOUNT A");
      INNER_JOIN("DEPARTMENT D on D.ID = P.DEPARTMENT_ID");
      INNER_JOIN("COMPANY C on D.COMPANY_ID = C.ID");
      WHERE("P.ID = A.ID");
      WHERE("P.FIRST_NAME like ?");
      OR();
      WHERE("P.LAST_NAME like ?");
      GROUP_BY("P.ID");
      HAVING("P.LAST_NAME like ?");
      OR();
      HAVING("P.FIRST_NAME like ?");
      ORDER_BY("P.ID");
      ORDER_BY("P.FULL_NAME");
      return SQL();
    }
}
```

