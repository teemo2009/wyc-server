package com.ga.wyc.domain.dto;

import com.ga.wyc.domain.entity.Driver;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class DriverDTO extends Driver {
    private String token;
    private String refreshToken;
}
