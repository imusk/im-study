package com.github.imusk.im.mapstruct.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author: Musk
 * @email: muskcool@protonmail.com
 * @datetime: 2020-08-27 09:08:08
 * @classname: BooleanStrategy
 * @description: 布尔型策略
 */
@Component
public class BooleanStrategy {

    public Boolean stringToBoolean(String value) {

        if (value == null) {
            return false;
        }

        return "0".equals(value) ? false : true;
    }

    public String booleanToString(Boolean value) {

        if (value == null) {
            return "0";
        }

        return value ? "1" : "0";
    }

    public Integer booleanToInteger(Boolean value) {
        if (value == null) {
            return null;
        }

        return value ? 1 : 0;
    }

    public Boolean integerToBoolean(Integer value) {
        if (value == null) {
            return null;
        }

        return value == 0 ? false : true;
    }

    public BigDecimal booleanToBigDecimal(Boolean value) {
        if (value == null) {
            return null;
        }

        return value ? BigDecimal.valueOf(1) : BigDecimal.valueOf(0);
    }

    public Boolean bigDecimalToBoolean(BigDecimal value) {
        if (value == null) {
            return null;
        }

        return value.equals(BigDecimal.valueOf(0)) ? false : true;
    }

}
