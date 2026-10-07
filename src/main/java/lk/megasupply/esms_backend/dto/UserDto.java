package lk.megasupply.esms_backend.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String fullName;
    private String nicNumber;
    private String email;
    private String username;
    private String role;
    private boolean isActive;
}