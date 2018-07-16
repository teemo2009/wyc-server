package com.ga.wyc.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ga.wyc.domain.bean.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class User extends BaseEntity {
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}