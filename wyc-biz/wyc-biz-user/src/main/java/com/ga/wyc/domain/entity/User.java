package com.ga.wyc.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ga.wyc.domain.bean.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class User extends BaseEntity {
    private Long id;
    private String code;
    private String name;
    private String phone;
    private String gender;
    private Integer state;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}