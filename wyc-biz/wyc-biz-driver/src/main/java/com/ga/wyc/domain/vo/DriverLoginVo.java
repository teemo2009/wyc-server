package com.ga.wyc.domain.vo;

import com.ga.wyc.domain.entity.Driver;
import com.ga.wyc.domain.entity.DriverDev;
import com.ga.wyc.domain.group.IDriverAutoLoginGroup;
import com.ga.wyc.domain.group.IDriverLoginGroup;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
public class DriverLoginVo {
    @NotBlank(groups = {IDriverLoginGroup.class},message = "验证码不能为空")
    @Size(groups = {IDriverLoginGroup.class},max = 4,message = "验证码为4位")
    private String code;
    @NotBlank(groups = {IDriverAutoLoginGroup.class},message = "token不能为空")
    private String token;
    @NotNull(message ="driver对象不能为空" )
    @Valid
    private Driver driver;
    @NotNull(message = "driverDev对象不能空")
    @Valid
    private DriverDev driverDev;


}
