package com.github.imusk.im.mapstruct;

import com.github.imusk.im.base.BaseStruct;
import com.github.imusk.im.dto.UserInfoDto;
import com.github.imusk.im.entity.UserInfo;
import com.github.imusk.im.enums.Gender;
import com.github.imusk.im.mapstruct.strategy.BooleanStrategy;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

/**
 * @author: Musk
 * @email: muskcool@protonmail.com
 * @datetime: 2020-08-27 09:04:36
 * @classname: UseInfoStruct
 * @description: UseInfoStruct
 */
@Mapper(uses = {BooleanStrategy.class})
public interface UserInfoStruct extends BaseStruct<UserInfoDto, UserInfo> {

    //静态方法
    UserInfoStruct INSTANCE = Mappers.getMapper(UserInfoStruct.class);


    @Override
    @Mappings({
            @Mapping(target = "gender", expression = "java(getGenderValue(dto.getGender()))"),
            @Mapping(source = "delete", target = "deleted"),
            //日期转化
            @Mapping(source = "birthday", target = "birthday", dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(target = "amount", expression = "java(new Double(dto.getAmount() * 100).longValue())"),
    })
    UserInfo toEntity(UserInfoDto dto);

    /*
    时间转换并格式化
    @Mapping(source = “birthday”, target = “birthDateFormat”, dateFormat = “yyyy-MM-dd HH:mm:ss”),
    @Mapping：属性映射，若源对象属性与目标对象名字一致，会自动映射对应属性
    source：源属性
    target：目标属性
    dateFormat：String 到 Date 日期之间相互转换，通过 SimpleDateFormat，该值为 SimpleDateFormat的日期格式
    ignore: 忽略这个字段
    @Mappings：配置多个@Mapping
    @MappingTarget 用于更新已有对象
    @InheritConfiguration 用于继承配置
     */

    @Override
    @Mappings({
            @Mapping(target = "gender", expression = "java(genderConveter(entity.getGender()))"),
            @Mapping(source = "deleted", target = "delete"),
            // @Mapping(source = "amount", target = "amount", numberFormat = "$#.00", defaultValue = "0"), // int -> String
            // @Mapping(source = "amount", target = "amount", numberFormat = "#.##E0", defaultValue = "0"), // BigDecimal -> String
            //@Mapping(source = "amount", target = "amount", numberFormat = "0.00", defaultValue = "0"),
            @Mapping(target = "amount", numberFormat = "0.00", expression = "java(entity.getAmount()/100.0)"),
            @Mapping(target = "birthday", source = "birthday", dateFormat = "yyyy-MM-dd HH:mm:ss"), // Date -> String
            //调用方法转换字段,注意其中的person为入参的形参{person}
            @Mapping(target = "versionTitle", expression = "java(versionConvert(entity.getVersion()))"),
            //固定一个字段不变
            @Mapping(target = "content", constant = "默认不变"),
    })
    UserInfoDto toDto(UserInfo entity);

    //转换versionTitle
    default String versionConvert(Integer version) {
        return "V" + version;
    }

    default Gender genderConveter(int value) {
        return Gender.getGender(value);
    }

    default int getGenderValue(Gender gender) {
        return gender.getValue();
    }

}
