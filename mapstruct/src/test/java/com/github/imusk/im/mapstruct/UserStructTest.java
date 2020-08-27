package com.github.imusk.im.mapstruct;

import com.github.imusk.im.MapStructApplication;
import com.github.imusk.im.dto.UserInfoDto;
import com.github.imusk.im.entity.UserInfo;
import com.github.imusk.im.enums.Gender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;

/**
 * @author: Musk
 * @email: muskcool@protonmail.com
 * @datetime: 2020-08-27 12:34:46
 * @classname: UserStructTest
 * @description: UserStructTest
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MapStructApplication.class})
public class UserStructTest {

    private static final Logger logger = LoggerFactory.getLogger(UserStructTest.class);

    @Autowired
    private UserStruct userStruct;

    @Test
    public void getInfo() throws Exception {

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

        logger.info("Entity：{}", userInfo);

        UserInfoDto dto = UserInfoStruct.INSTANCE.toDto(userInfo);
        logger.info("DTO1：{}", dto);

        UserInfoDto userDto = userStruct.toDto(userInfo);
        logger.info("DTO2：{}", userDto);
    }

}
