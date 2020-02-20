package com.itheima.mybatis.sqlsession;

public interface SqlSessionFactory {
    /**
     * 用于打开一个新的SqlSession对象
     */
    SqlSession openSession();
}
