package lk.megasupply.esms_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    private Long id;
    private String fullName;
    private String nicNumber;
    private String email;
    private String phoneNumber;
    private String username;
    private String role;
    private String designation;
    private boolean isActive;
    private LocalDateTime createdAt; // User Register වුණ දවස

    @JsonProperty("isActive")
    public boolean isActive() {
        return isActive;
    }
}