package com.wusu.wu;

import com.wusu.wu.mybatis.entity.User;
import com.wusu.wu.mybatis.mapper.UserMapper;
import com.wusu.wu.mybatis.proxy.MapperProxyFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class Main {
    public static void main(String[] args) {
        UserMapper userMapper = MapperProxyFactory.getMapper(UserMapper.class);
        User user = userMapper.selectById(2);
        log.info("user: {}", user);

        List<User> users = userMapper.selectBy("tom", 12);
        log.info("users: {}", users);
        List<User> users2 = userMapper.selectBy("tom", 18);
        log.info("users2: {}", users2);
    }
}