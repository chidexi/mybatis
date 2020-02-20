package com.itheima.dao;

import com.itheima.domain.User;
import com.itheima.mybatis.annotations.Select;

import java.util.List;

/**
* 用户的持久层接口
*/
public interface IUserDao {
    @Select("SELECT * FROM USER")
    List<User> findAll();
}
