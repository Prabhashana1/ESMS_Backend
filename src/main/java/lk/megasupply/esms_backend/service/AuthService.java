package lk.megasupply.esms_backend.service;

import lk.megasupply.esms_backend.dto.AuthResponseDto;
import lk.megasupply.esms_backend.dto.LoginDto;

public interface AuthService {
    AuthResponseDto login(LoginDto loginDto);
}