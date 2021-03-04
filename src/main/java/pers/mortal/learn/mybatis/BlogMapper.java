package pers.mortal.learn.mybatis;

import org.apache.ibatis.annotations.Select;

public interface BlogMapper {
    //@Select("SELECT * FROM Blog WHERE id = #{id}")//有xml定义时不能重复定义，这个接口可以用来实现用更简洁的方式调用映射语句。
    Blog selectBlog(int id);
}
