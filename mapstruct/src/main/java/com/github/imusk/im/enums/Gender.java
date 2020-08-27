package com.github.imusk.im.enums;

/**
 * @author: Musk
 * @email: muskcool@protonmail.com
 * @datetime: 2020-08-27 09:59:50
 * @classname: GenderEnum
 * @description: GenderEnum
 */
public enum Gender {

    UNKNOWN(0,"未知"),

    MAN(1,"男"),

    WOMAN(2,"女"),

    ;

    private int value;

    private String label;

    Gender(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public static Gender getGender(int value) {
        for (Gender gender : Gender.values()) {
            if (gender.getValue() == value) {
                return gender;
            }
        }
        return Gender.UNKNOWN;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
