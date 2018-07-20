package com.ga.wyc.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ga.wyc.domain.bean.TimeEntity;
import com.ga.wyc.domain.enums.Encrypt;
import com.ga.wyc.domain.enums.OrderState;
import com.ga.wyc.domain.enums.PayState;
import com.ga.wyc.domain.group.*;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class Order extends TimeEntity {
    @NotNull(groups = {IOrderTackGroup.class,IOrderStartGroup.class,
            IOrderReachGroup.class,IOrderFinishGroup.class},message = "订单id不能为空")
    private Long id;

    private String code;

    @NotNull(groups = {IOrderInitGroup.class},message = "userId不能为空")
    private Long userId;

    @NotNull(groups = {IOrderTackGroup.class,IOrderStartGroup.class,IOrderReachGroup.class},message = "driverCarId不能为空")
    private Long driverCarId;

    private Long driverCarBatchId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date departTime;

    @Size(groups = {IOrderInitGroup.class},min = 0,max = 128,message = "driverCarId不能为空")
    private String passengerNote;

    @NotBlank(groups = {IOrderInitGroup.class},message = "出发地不能为空")
    private String departure;

    @NotNull(groups = {IOrderInitGroup.class},message = "出发经度不能为空")
    private BigDecimal depLongitude;

    @NotNull(groups = {IOrderInitGroup.class},message = "出发纬度不能为空")
    private BigDecimal depLatitude;

    @NotBlank(groups = {IOrderInitGroup.class},message = "目的地不能为空")
    private String desination;

    @NotNull(groups = {IOrderInitGroup.class},message = "目的地经度不能为空")
    private BigDecimal destLongitude;

    @NotNull(groups = {IOrderInitGroup.class},message = "目的地纬度不能为空")
    private BigDecimal destLatitude;

    private Encrypt encrypt;

    @NotNull(groups = {IOrderInitGroup.class},message = "运价类型编码")
    private String faretype;

    private OrderState state;

    private Integer serviceScore;

    private String evaluateDetail;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date evaluateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date depTime;

    @NotNull(groups = {IOrderStartGroup.class},message = "空载里程不能为空")
    private BigDecimal waitMile;
    @NotNull(groups = {IOrderStartGroup.class},message = "空载时间不能为空")
    private Integer waitTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date destTime;

    @NotNull(groups = {IOrderReachGroup.class},message = "载客里程不能为空")
    private BigDecimal driveMile;

    private Integer driveTime;

    @NotNull(groups = {IOrderInitGroup.class},message = "预计花费不能为空")
    private BigDecimal price;

    private BigDecimal factPrice;

    private BigDecimal farUpPrice;

    private BigDecimal otherUpPrice;

    private PayState payState;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date finishTime;

    @NotNull(groups = {IOrderInitGroup.class},message = "预计花费时间不能为空")
    private Integer estTime;

    @NotNull(groups = {IOrderInitGroup.class},message = "预计里程不能为空")
    private BigDecimal estMile;
}