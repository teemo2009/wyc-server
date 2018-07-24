package com.ga.wyc.domain.dto;

import com.ga.wyc.domain.entity.Manager;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class ManagerDTO extends Manager {
    private String token;
    private String refreshToken;
}
