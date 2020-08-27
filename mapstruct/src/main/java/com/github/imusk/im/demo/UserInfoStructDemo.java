package com.github.imusk.im.demo;

import com.github.imusk.im.dto.UserInfoDto;
import com.github.imusk.im.entity.UserInfo;
import com.github.imusk.im.enums.Gender;
import com.github.imusk.im.mapstruct.UserInfoStruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

/**
 * @author: Musk
 * @email: muskcool@protonmail.com
 * @datetime: 2020-08-27 09:25:42
 * @classname: UserInfoStructDemo
 * @description: UserInfoStructDemo
 */
public class UserInfoStructDemo {

    private static final Logger logger = LoggerFactory.getLogger(UserInfoStructDemo.class);

    public static void main(String[] args) throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        UserInfo userInfo = new UserInfo();
        userInfo.setId(1L);
        userInfo.setUid("UID_100000");
        userInfo.setUsername("musk");
        userInfo.setPassword("123456");
        userInfo.setDeleted((byte) 1);
        userInfo.setGender(Gender.UNKNOWN.getValue());
        userInfo.setAmount(1234L);
        userInfo.setVersion(1);
        userInfo.setBirthday(dateFormat.parse("2000-01-01"));

        UserInfoDto dto = UserInfoStruct.INSTANCE.toDto(userInfo);

        logger.info("DTO : {}", dto);

        UserInfo entity = UserInfoStruct.INSTANCE.toEntity(dto);
        logger.info("Entity : {}", entity);
    }


}
