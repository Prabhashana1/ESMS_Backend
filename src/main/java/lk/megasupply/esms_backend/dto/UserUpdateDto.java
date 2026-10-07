package lk.megasupply.esms_backend.dto;

import lombok.Data;

@Data
public class UserUpdateDto {
    private String fullName;
    private String nicNumber;
    private String email;
    private String role;
}