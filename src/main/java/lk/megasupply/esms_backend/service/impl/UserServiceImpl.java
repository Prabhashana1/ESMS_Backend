package lk.megasupply.esms_backend.service.impl;

import lk.megasupply.esms_backend.dto.PasswordResetDto;
import lk.megasupply.esms_backend.dto.UserCreateDto;
import lk.megasupply.esms_backend.dto.UserDto;
import lk.megasupply.esms_backend.dto.UserUpdateDto;
import lk.megasupply.esms_backend.entity.User;
import lk.megasupply.esms_backend.enums.Designation;
import lk.megasupply.esms_backend.enums.Role;
import lk.megasupply.esms_backend.exception.ResourceNotFoundException;
import lk.megasupply.esms_backend.repository.UserRepository;
import lk.megasupply.esms_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(UserCreateDto createDto) {
        User user = new User();
        user.setFullName(createDto.getFullName());
        user.setNicNumber(createDto.getNicNumber());
        user.setEmail(createDto.getEmail());
        user.setPhoneNumber(createDto.getPhoneNumber());
        user.setUsername(createDto.getUsername());
        user.setPassword(passwordEncoder.encode(createDto.getPassword()));
        user.setRole(Role.valueOf(createDto.getRole().toUpperCase()));
        user.setDesignation(Designation.valueOf(createDto.getDesignation().toUpperCase()));
        user.setActive(true);

        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();

        for (User user : users) {
            userDtos.add(mapToDto(user));
        }
        return userDtos;
    }

    @Override
    public UserDto updateUser(Long id, UserUpdateDto updateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setFullName(updateDto.getFullName());
        user.setNicNumber(updateDto.getNicNumber());
        user.setEmail(updateDto.getEmail());
        user.setPhoneNumber(updateDto.getPhoneNumber());

        if (updateDto.getRole() != null) {
            user.setRole(Role.valueOf(updateDto.getRole().toUpperCase()));
        }

        if (updateDto.getDesignation() != null) {
            user.setDesignation(Designation.valueOf(updateDto.getDesignation().toUpperCase()));
        }

        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    @Override
    public UserDto changeUserStatus(Long id, boolean isActive) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setActive(isActive);
        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    @Override
    public void resetPassword(Long id, PasswordResetDto passwordResetDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setPassword(passwordEncoder.encode(passwordResetDto.getNewPassword()));
        userRepository.save(user);
    }

    private UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setNicNumber(user.getNicNumber());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        dto.setDesignation(user.getDesignation() != null ? user.getDesignation().name() : null);
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}