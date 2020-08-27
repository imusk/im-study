package com.github.imusk.im.entity;

import com.github.imusk.im.enums.Gender;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: Musk
 * @email: muskcool@protonmail.com
 * @datetime: 2020-08-27 08:58:50
 * @classname: UserInfo
 * @description: UserInfo
 */
public class UserInfo implements Serializable {

    private Long id;

    private String uid;

    private String username;

    private String password;

    private int gender;

    private byte deleted;

    private Long amount;

    private Date birthday;

    private Integer version;

    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public byte getDeleted() {
        return deleted;
    }

    public void setDeleted(byte deleted) {
        this.deleted = deleted;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", gender=" + gender +
                ", deleted=" + deleted +
                ", amount=" + amount +
                ", birthday=" + birthday +
                ", version=" + version +
                ", content='" + content + '\'' +
                '}';
    }

}



