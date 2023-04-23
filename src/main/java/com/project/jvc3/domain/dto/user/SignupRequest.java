package com.project.jvc3.domain.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    private String email;
    private String name;
    private String nickname;
    private String password;
}
