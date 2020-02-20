package com.itheima.mybatis.utils;

import com.itheima.mybatis.cfg.Mapper;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyPermission;

/**
 * 负责执行SQL语句，并且封装结果集
 */
public class Executor {
    public <E> List<E> selectList(Mapper mapper, Connection conn){
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try{
            //1.取出mapper中的数据 select * from user
            String queryString = mapper.getQueryString();
            //com.itheima.domain.User
            String resultType = mapper.getResultType();
            Class domainClass = Class.forName(resultType);
            //2.获取PreparedStatement对象
            pstm = conn.prepareStatement(queryString);
            //3.执行SQL获得结果集
            rs = pstm.executeQuery();
            //4.封装结果集  定义返回值
            List<E> list = new ArrayList<E>();
            while(rs.next()){
                //实例化要封装的实体类对象
                E obj = (E)domainClass.newInstance();
                //取出结果集的元信息：ResultSetMeteData
                ResultSetMetaData rsmd = rs.getMetaData();
                //列出总列数
                int columnCount = rsmd.getColumnCount();
                //遍历总列数
                for(int i = 1; i <= columnCount; i++){
                    //获取每列的名称，列名的序号是从1开始
                    String columnName = rsmd.getColumnName(i);
                    //根据得到列名，获取每列的值
                    Object columnValue= rs.getObject(columnName);
                    //给obj赋值，使用Java省内机制（借助PropertyDescriptor实现属性的封装）
                    PropertyDescriptor pd = new PropertyDescriptor(columnName,domainClass);
                    //获取它的写入方法
                    Method writeMethod = pd.getWriteMethod();
                    //把获取的列的值，给对象赋值
                    writeMethod.invoke(obj,columnValue);
                }
                //把赋值好的对象加入到集合中
                list.add(obj);
            }
            return list;
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            release(pstm,rs);
        }
    }

    private void release(PreparedStatement pstm,ResultSet rs){
        if(rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(pstm != null){
            try {
                pstm.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
