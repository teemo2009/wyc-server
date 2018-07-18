package com.ga.wyc.domain.entity;

import com.ga.wyc.domain.bean.TimeEntity;
import com.ga.wyc.domain.group.IDriverAutoLoginGroup;
import com.ga.wyc.domain.group.IDriverLoginGroup;
import com.ga.wyc.util.MUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class Driver extends TimeEntity {
    private Long id;
    private String code;
    private String name;
    @Pattern(groups = {IDriverLoginGroup.class,IDriverAutoLoginGroup.class},regexp =MUtil.REGX_PHONE,message = "电话号码格式错误")
    private String phone;
    private String gender;
    private Integer state;
}