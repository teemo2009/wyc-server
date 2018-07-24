package com.ga.wyc.domain.entity;

import com.ga.wyc.domain.bean.TimeEntity;
import com.ga.wyc.domain.enums.ManagerState;
import com.ga.wyc.domain.group.IManagerAddGroup;
import com.ga.wyc.domain.group.IManagerLoginGroup;
import com.ga.wyc.util.MUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class Manager extends TimeEntity{
    private Long id;

    @NotBlank(groups = {IManagerAddGroup.class, IManagerLoginGroup.class},message = "用户名不为空")
    @Size(groups = {IManagerAddGroup.class,IManagerLoginGroup.class},min = 6,max = 12,message = "长度6~12位")
    private String userName;

    @NotBlank(groups = {IManagerAddGroup.class,IManagerLoginGroup.class},message = "密码不能为空")
    private String password;

    @NotBlank(groups = {IManagerAddGroup.class},message = "用户名不能为空")
    private String realName;

    @NotBlank(groups = {IManagerAddGroup.class},message = "电话号不能为空")
    @Pattern(regexp = MUtil.REGX_PHONE,message = "电话号码格式错误")
    private String phone;

    private ManagerState state;
}