package com.ga.wyc.domain.entity;


import com.ga.wyc.domain.bean.TimeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class User extends TimeEntity {
    @NotNull(message ="id不能为空")
    private Long id;
    private String code;
    @NotBlank(message = "用户名称不能为空")
    @Size(min = 1,max = 10,message = "用户名称长度不能为空，10个字符内")
    private String name;
    private String phone;
    @NotBlank(message = "性别不能为空")
    @Size(max = 2,message = "性别长度不能大于2")
    private String gender;
    private Integer state;

}