package lk.megasupply.esms_backend.dto;

import lombok.Data;

@Data
public class UserCreateDto {
    private String fullName;
    private String nicNumber;
    private String email;
    private String username;
    private String password;
    private String role;
}