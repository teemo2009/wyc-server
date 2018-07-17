package com.ga.wyc.domain.entity;


import com.ga.wyc.domain.bean.TimeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class Car extends TimeEntity {
    private Long id;
    private String code;
    private String vehicleNo;
    private Integer seats;
    private String model;
    private String vehicleType;
    private String vehicleColor;
    private Integer state;
}