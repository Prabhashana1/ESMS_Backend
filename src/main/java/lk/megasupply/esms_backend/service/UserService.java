package lk.megasupply.esms_backend.service;

import lk.megasupply.esms_backend.dto.PasswordResetDto;
import lk.megasupply.esms_backend.dto.UserCreateDto;
import lk.megasupply.esms_backend.dto.UserDto;
import lk.megasupply.esms_backend.dto.UserUpdateDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserCreateDto createDto);
    UserDto getUserById(Long id);
    List<UserDto> getAllUsers();
    UserDto updateUser(Long id, UserUpdateDto updateDto);
    UserDto changeUserStatus(Long id, boolean isActive);
    void resetPassword(Long id, PasswordResetDto passwordResetDto);

}