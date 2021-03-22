package pers.mortal.learn.mybatis.sqlstatementbuilder;

import org.apache.ibatis.jdbc.SQL;

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
