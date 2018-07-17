package com.ga.wyc.domain.entity;

import com.ga.wyc.domain.bean.TimeEntity;
import com.ga.wyc.domain.enums.NetType;
import com.ga.wyc.domain.group.IDriverAutoLoginGroup;
import com.ga.wyc.domain.group.IDriverLoginGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class DriverDev extends TimeEntity{
    private Long id;
    private Long driverId;
    @NotBlank(groups = {IDriverLoginGroup.class, IDriverAutoLoginGroup.class},message = "设备码不能为空")
    @Size(groups = {IDriverLoginGroup.class,IDriverAutoLoginGroup.class},max = 256,message = "设备码过长")
    private String devNo;

    private String version;

    private NetType netType;

    @NotBlank(groups = {IDriverLoginGroup.class,IDriverAutoLoginGroup.class},message = "操作系统不能为空")
    private String os;

    private Integer mapType;



}