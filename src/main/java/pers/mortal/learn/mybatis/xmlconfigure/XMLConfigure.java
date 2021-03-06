package pers.mortal.learn.mybatis.xmlconfigure;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import pers.mortal.learn.mybatis.Blog;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class XMLConfigure {

    public static void main(String[] args)throws IOException{
        setProperties();
    }

    public static void setProperties() throws IOException{
        String resource = "META-INF/xmlConfigure/configureFile.xml";
        InputStream input = Resources.getResourceAsStream(resource);

        Properties props = new Properties();
        props.setProperty("username", "root");
        props.setProperty("password", "root");

        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(input, props);

        try(SqlSession session = factory.openSession()) {
            Blog blog = session.selectOne("pers.mortal.learn.mybatis.BlogMapper.selectBlog", 1);
            System.out.println(blog.getContent());
        }
    }
}
