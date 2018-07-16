package com.ga.wyc.domain.entity;

import com.ga.wyc.domain.bean.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class Car extends BaseEntity {
    private Long id;

    private String code;

    private String vehicleNo;

    private Integer seats;

    private String model;

    private String vehicleType;

    private String vehicleColor;

    private Integer state;

    private Date createTime;

    private Date updateTime;

}