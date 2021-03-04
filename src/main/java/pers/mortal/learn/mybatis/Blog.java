package pers.mortal.learn.mybatis;

public class Blog {
    private int id;
    private String content;

    public int getId(){
        return this.id;
    }
    public void setId(int id){
        this.id = id;
    }

    public String getContent(){
        return this.content;
    }
    public void setContent(String content){
        this.content = content;
    }
}
