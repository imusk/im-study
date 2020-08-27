package com.github.imusk.im.dto;

import com.github.imusk.im.enums.Gender;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: Musk
 * @email: muskcool@protonmail.com
 * @datetime: 2020-08-27 09:01:24
 * @classname: UserInfoDto
 * @description: UserInfoDto
 */
public class UserInfoDto implements Serializable {

    private String uid;

    private String username;

    /**
     * 自定义策略，Byte 转 String ： 0-保密，1-男，2-女
     */
    private Gender gender;

    /**
     * 别名，自定义策略，byte 转 Boolean ： 0-false，1-true
     */
    private boolean delete;

    private Double amount;

    private String birthday;

    private String versionTitle;

    private String content;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getVersionTitle() {
        return versionTitle;
    }

    public void setVersionTitle(String versionTitle) {
        this.versionTitle = versionTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "UserInfoDto{" +
                "uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", gender='" + gender + '\'' +
                ", delete=" + delete +
                ", amount=" + amount +
                ", birthday='" + birthday + '\'' +
                ", versionTitle='" + versionTitle + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
