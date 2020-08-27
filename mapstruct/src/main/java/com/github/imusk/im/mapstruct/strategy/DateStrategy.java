package com.github.imusk.im.mapstruct.strategy;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: Musk
 * @email: muskcool@protonmail.com
 * @datetime: 2020-08-27 09:08:26
 * @classname: DateStrategy
 * @description: 日期转换
 */
@Component
public class DateStrategy {

    public String asString(Date date) {
        return date != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(date) : null;
    }

    public Date asDate(String date) {
        try {
            return date != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .parse(date) : null;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
