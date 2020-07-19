package com.wen.im.entity;

import java.io.Serializable;

/**
 * @classname: User
 * @description: User
 * @data: 2020-07-18 01:22
 * @author: Musk
 */
public class MyUser implements Serializable {

    /**
     * ID
     */
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 年龄
     */
    private int age;

    /**
     * 地址
     */
    private Address address;

    /**
     * 性别
     */
    private Gender gender;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    static class Address implements Serializable {
        private String country;

        private String province;

        private String city;

        private String detail;

        private String postcode;

        public Address() {
        }

        public Address(String country, String province, String city, String detail, String postcode) {
            this.country = country;
            this.province = province;
            this.city = city;
            this.detail = detail;
            this.postcode = postcode;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public String getPostcode() {
            return postcode;
        }

        public void setPostcode(String postcode) {
            this.postcode = postcode;
        }
    }

    enum Gender {
        UNKNOWN("未知", 0),
        MALE("男", 1),
        FEMALE("女", 2),

        ;

        private String label;

        private Integer value;

        Gender(String label, Integer value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }

}
