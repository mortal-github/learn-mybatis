## 日志  

### 日志工厂 

**默认日志委托**：  
- **日志工厂**：Mybatis 通过使用内置的日志工厂提供日志功能。内置日志工厂将会把日志工作委托给下面的实现之一。  
- **默认委托与优先级**：MyBatis 内置日志工厂会基于运行时检测信息选择日志委托实现。它会使用第一个查找到的实现。当没有找到这些实现时，将会禁用日志功能。   
    - `SLE4J`
    - `Apache Commons Logging`
    - `Log4j 2`
    - `Log4j`
    - `JDK loggin`
- **XML指定委托**： 想使用其它日志实现，你可以通过在 MyBatis 配置文件 `mybatis-config.xml` 里面添加一项 setting 来选择其它日志实现。  
    - 可选值：`SLF4J`、`LOG4J`、`LOG4J2`、`JDK_LOGGING`、`COMMONS_LOGGING`、`STDOUT_LOGGING`、`NO_LOGGING`。  
    - 自定义实现：或者是实现了 org.apache.ibatis.logging.Log 接口，且构造方法以字符串为参数的类完全限定名。  
- **Java指定委托**：也可以调用方法来选择日志实现。   
    - 调用时机：你应该在调用其它 MyBatis 方法之前调用以上的某个方法。 
    - 调用失败：仅当运行时类路径中存在该日志实现时，日志实现的切换才会生效。 
    - 失败处理：调用失败，MyBatis 就会忽略这一切换请求，并将以默认的查找顺序决定使用的日志实现。
```xml
<configuration>
    <settings>
        <setting name="logImpl" value="LOG4J"/>
    </settings>
</configuration>
```
```java
package org.apache.ibatis.loggin;
public class LogFactory{
    public void userSlf4jLogging();
    public void useCommonsLogging();
    public void userLog4JLogging();
    public void useJdkLogging();
    public void useStdOutLogging();
}
```

### 日志配置  
<section>
    <p>你可以通过在包、映射类的全限定名、命名空间或全限定语句名上开启日志功能，来查看 MyBatis 的日志语句。</p>   
    <p>再次提醒，具体配置步骤取决于日志实现。接下来我们会以 Log4J 作为示范。配置日志功能非常简单：添加一个或多个配置文件（如 log4j.properties），有时还需要添加 jar 包（如 log4j.jar）。下面的例子将使用 Log4J 来配置完整的日志服务。一共两个步骤：</p>
    <section>
        <h4>步骤 1：添加 Log4J 的 jar 包</h4>
            <p>由于我们使用的是 Log4J，我们要确保它的 jar 包可以被应用使用。为此，需要将 jar 包添加到应用的类路径中。Log4J 的 jar 包可以在上面的链接中下载。</p>
            <p>对于 web 应用或企业级应用，你可以将 <code>log4j.jar</code> 添加到 <code>WEB-INF/lib</code> 目录下；对于独立应用，可以将它添加到 JVM 的 <code>-classpath</code> 启动参数中。</p>
    </section>
<section>
<h4>步骤 2：配置 Log4J</h4>
        <p>配置 Log4J 比较简单。假设你需要记录这个映射器的日志：</p>
        <div class="source"><pre class="prettyprint"><span class="kwd">package</span><span class="pln"> org</span><span class="pun">.</span><span class="pln">mybatis</span><span class="pun">.</span><span class="pln">example</span><span class="pun">;</span><span class="pln">
</span><span class="kwd">public</span><span class="pln"> </span><span class="kwd">interface</span><span class="pln"> </span><span class="typ">BlogMapper</span><span class="pln"> </span><span class="pun">{</span><span class="pln">
  </span><span class="lit">@Select</span><span class="pun">(</span><span class="str">"SELECT * FROM blog WHERE id = #{id}"</span><span class="pun">)</span><span class="pln">
  </span><span class="typ">Blog</span><span class="pln"> selectBlog</span><span class="pun">(</span><span class="kwd">int</span><span class="pln"> id</span><span class="pun">);</span><span class="pln">
</span><span class="pun">}</span></pre></div>
        <p>在应用的类路径中创建一个名为 <code>log4j.properties</code> 的文件，文件的具体内容如下：</p>
        <div class="source"><pre class="prettyprint"><span class="com"># 全局日志配置</span><span class="pln">
log4j</span><span class="pun">.</span><span class="pln">rootLogger</span><span class="pun">=</span><span class="pln">ERROR</span><span class="pun">,</span><span class="pln"> stdout
</span><span class="com"># MyBatis 日志配置</span><span class="pln">
log4j</span><span class="pun">.</span><span class="pln">logger</span><span class="pun">.</span><span class="pln">org</span><span class="pun">.</span><span class="pln">mybatis</span><span class="pun">.</span><span class="pln">example</span><span class="pun">.</span><span class="typ">BlogMapper</span><span class="pun">=</span><span class="pln">TRACE
</span><span class="com"># 控制台输出</span><span class="pln">
log4j</span><span class="pun">.</span><span class="pln">appender</span><span class="pun">.</span><span class="pln">stdout</span><span class="pun">=</span><span class="pln">org</span><span class="pun">.</span><span class="pln">apache</span><span class="pun">.</span><span class="pln">log4j</span><span class="pun">.</span><span class="typ">ConsoleAppender</span><span class="pln">
log4j</span><span class="pun">.</span><span class="pln">appender</span><span class="pun">.</span><span class="pln">stdout</span><span class="pun">.</span><span class="pln">layout</span><span class="pun">=</span><span class="pln">org</span><span class="pun">.</span><span class="pln">apache</span><span class="pun">.</span><span class="pln">log4j</span><span class="pun">.</span><span class="typ">PatternLayout</span><span class="pln">
log4j</span><span class="pun">.</span><span class="pln">appender</span><span class="pun">.</span><span class="pln">stdout</span><span class="pun">.</span><span class="pln">layout</span><span class="pun">.</span><span class="typ">ConversionPattern</span><span class="pun">=%</span><span class="lit">5p</span><span class="pln"> </span><span class="pun">[%</span><span class="pln">t</span><span class="pun">]</span><span class="pln"> </span><span class="pun">-</span><span class="pln"> </span><span class="pun">%</span><span class="pln">m</span><span class="pun">%</span><span class="pln">n</span></pre></div>
        <p>上述配置将使 Log4J 详细打印 <code>org.mybatis.example.BlogMapper</code> 的日志，对于应用的其它部分，只打印错误信息。</p>
        <p>为了实现更细粒度的日志输出，你也可以只打印特定语句的日志。以下配置将只打印语句 <code>selectBlog</code> 的日志：</p>
<div class="source"><pre class="prettyprint"><span class="pln">log4j</span><span class="pun">.</span><span class="pln">logger</span><span class="pun">.</span><span class="pln">org</span><span class="pun">.</span><span class="pln">mybatis</span><span class="pun">.</span><span class="pln">example</span><span class="pun">.</span><span class="typ">BlogMapper</span><span class="pun">.</span><span class="pln">selectBlog</span><span class="pun">=</span><span class="pln">TRACE</span></pre></div>
        <p>或者，你也可以打印一组映射器的日志，只需要打开映射器所在的包的日志功能即可：</p>
        <div class="source"><pre class="prettyprint"><span class="pln">log4j</span><span class="pun">.</span><span class="pln">logger</span><span class="pun">.</span><span class="pln">org</span><span class="pun">.</span><span class="pln">mybatis</span><span class="pun">.</span><span class="pln">example</span><span class="pun">=</span><span class="pln">TRACE</span></pre></div>
        <p>某些查询可能会返回庞大的结果集。这时，你可能只想查看 SQL 语句，而忽略返回的结果集。为此，SQL 语句将会在 DEBUG 日志级别下记录（JDK 日志则为 FINE）。返回的结果集则会在 TRACE 日志级别下记录（JDK 日志则为 FINER)。因此，只要将日志级别调整为 DEBUG 即可：</p>
        <div class="source"><pre class="prettyprint"><span class="pln">log4j</span><span class="pun">.</span><span class="pln">logger</span><span class="pun">.</span><span class="pln">org</span><span class="pun">.</span><span class="pln">mybatis</span><span class="pun">.</span><span class="pln">example</span><span class="pun">=</span><span class="pln">DEBUG</span></pre></div>
        <p>但如果你要为下面的映射器 XML 文件打印日志，又该怎么办呢？</p>
             <div class="source"><pre class="prettyprint"><span class="pun">&lt;?</span><span class="pln">xml version</span><span class="pun">=</span><span class="str">"1.0"</span><span class="pln"> encoding</span><span class="pun">=</span><span class="str">"UTF-8"</span><span class="pln"> </span><span class="pun">?&gt;</span><span class="pln">
</span><span class="dec">&lt;!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd"&gt;</span><span class="pln">
</span><span class="tag">&lt;mapper</span><span class="pln"> </span><span class="atn">namespace</span><span class="pun">=</span><span class="atv">"org.mybatis.example.BlogMapper"</span><span class="tag">&gt;</span><span class="pln">
  </span><span class="tag">&lt;select</span><span class="pln"> </span><span class="atn">id</span><span class="pun">=</span><span class="atv">"selectBlog"</span><span class="pln"> </span><span class="atn">resultType</span><span class="pun">=</span><span class="atv">"Blog"</span><span class="tag">&gt;</span><span class="pln">
    select * from Blog where id = #{id}
  </span><span class="tag">&lt;/select&gt;</span><span class="pln">
</span><span class="tag">&lt;/mapper&gt;</span></pre></div>
        <p>这时，你可以通过打开命名空间的日志功能来对整个 XML 记录日志：</p>
    <div class="source"><pre class="prettyprint"><span class="pln">log4j</span><span class="pun">.</span><span class="pln">logger</span><span class="pun">.</span><span class="pln">org</span><span class="pun">.</span><span class="pln">mybatis</span><span class="pun">.</span><span class="pln">example</span><span class="pun">.</span><span class="typ">BlogMapper</span><span class="pun">=</span><span class="pln">TRACE</span></pre></div>
        <p>而要记录具体语句的日志，可以这样做：</p>       
        <div class="source"><pre class="prettyprint"><span class="pln">log4j</span><span class="pun">.</span><span class="pln">logger</span><span class="pun">.</span><span class="pln">org</span><span class="pun">.</span><span class="pln">mybatis</span><span class="pun">.</span><span class="pln">example</span><span class="pun">.</span><span class="typ">BlogMapper</span><span class="pun">.</span><span class="pln">selectBlog</span><span class="pun">=</span><span class="pln">TRACE</span></pre></div>
        <p>你应该会发现，为映射器和 XML 文件打开日志功能的语句毫无差别。</p>
        <p><span class="label important">提示</span> 如果你使用的是 SLF4J 或 Log4j 2，MyBatis 会设置 tag 为 MYBATIS。</p>
        <p>配置文件 <code>log4j.properties</code> 的余下内容用来配置输出器（appender），这一内容已经超出本文档的范围。关于 Log4J 的更多内容，可以参考上面的 Log4J 网站。或者，你也可以简单地做个实验，看看不同的配置会产生怎样的效果。</p>
      </section>
     </section>