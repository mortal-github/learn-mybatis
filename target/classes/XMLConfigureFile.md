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

