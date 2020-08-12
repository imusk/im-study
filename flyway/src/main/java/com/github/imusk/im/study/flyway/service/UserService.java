package com.github.imusk.im.study.flyway.service;

import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 93806-wenzhou
 * @date 2020-08-03 14:14
 * @email iwenzhou@qq.com
 * @description UserService
 */
public interface UserService {

    void create(String name, Integer age);

    void deleteByName(String name);

    Integer getAllUsers() throws Exception;

    void deleteAllUsers();

}
