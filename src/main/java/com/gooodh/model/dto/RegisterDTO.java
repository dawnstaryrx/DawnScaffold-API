package com.gooodh.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {
    private String type;
    private String email;
    private String phone;
    private String code;
    private String password;
    private String rePassword;
}
