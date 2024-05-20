package com.wusu.wu.mybatis.mapper;

import com.wusu.wu.mybatis.annotation.Param;
import com.wusu.wu.mybatis.annotation.Select;
import com.wusu.wu.mybatis.entity.User;

import java.util.List;

/**
 * @author github.com/solano33
 * @date 2024/5/15 22:32
 */
public interface UserMapper {

    @Select("select * from user where id = #{id}")
    User selectById(@Param("id") Integer id);

    @Select("select * from user where name = #{name} and age = #{age}")
    List<User> selectBy(@Param("name") String name, @Param("age") Integer age);

    @Select("select * from user where name = #{userName}")
    List<User> selectBy(@Param("userName") String name);
}
