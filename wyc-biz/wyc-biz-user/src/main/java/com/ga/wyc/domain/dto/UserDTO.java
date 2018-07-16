package com.ga.wyc.domain.dto;

import com.ga.wyc.domain.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class UserDTO extends User {
    private String token;
    private String refreshToken;
}
