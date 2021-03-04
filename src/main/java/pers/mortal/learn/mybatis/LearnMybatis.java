package pers.mortal.learn.mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * Hello world!
 *
 */
public class LearnMybatis
{
    public static void main( String[] args )throws IOException
    {
        System.out.println( "Hello World!" );
        //从XML中构建SqlSessionFactroy
        String resource = "META-INF/mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory xmlSqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        //不适用XML构建SqlSessionFactory
//        DataSource dataSource = BlogDataSourceFactory.getBlogDataSource();
//        TransactionFactory transactionFactory = new JdbcTransactionFactory();
//        Environment environment = new Environment("development", transactionFactory, dataSource);
//        Configuration configuration = new Configuration(environment);
//        configuration.addMapper(BlogMapper.class);
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);

        //从SqlSessionFactory中获取SqlSession
        try(SqlSession session = xmlSqlSessionFactory.openSession()) {
            Blog blog = session.selectOne("pers.mortal.learn.mybatis.BlogMapper.selectBlog", 1);
            System.out.println(blog.getContent());
        }
        //更简洁的方式，使用和指定语句的参数的返回值相匹配的接口————代码更清晰，更类型安全
        try (SqlSession session = xmlSqlSessionFactory.openSession()) {
            BlogMapper mapper = session.getMapper(BlogMapper.class);
            Blog blog = mapper.selectBlog(1);
            System.out.println(blog.getContent());
        }
    }
}
