package com.wen.im.flyway.service.impl;

import com.wen.im.flyway.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @author 93806-wenzhou
 * @date 2020-08-03 14:15
 * @email iwenzhou@qq.com
 * @description UserServiceImpl
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void create(String name, Integer age) {
        jdbcTemplate.update("insert into user(NAME, AGE) values(?, ?)", name, age);
    }

    @Override
    public void deleteByName(String name) {
        jdbcTemplate.update("delete from user where NAME = ?", name);
    }


    @Override
    public void deleteAllUsers() {
        jdbcTemplate.update("delete from user");
    }

    @Override
    public Integer getAllUsers() {
        return jdbcTemplate.queryForObject("select count(1) from user", Integer.class);
    }
}