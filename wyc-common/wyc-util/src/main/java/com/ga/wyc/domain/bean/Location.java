package com.ga.wyc.domain.bean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class Location {
    private BigDecimal lng;
    private BigDecimal lat;
}
