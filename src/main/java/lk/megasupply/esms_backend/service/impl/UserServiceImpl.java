package lk.megasupply.esms_backend.service.impl;

import lk.megasupply.esms_backend.dto.PasswordResetDto;
import lk.megasupply.esms_backend.dto.UserCreateDto;
import lk.megasupply.esms_backend.dto.UserDto;
import lk.megasupply.esms_backend.dto.UserUpdateDto;
import lk.megasupply.esms_backend.entity.User;
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
        user.setUsername(createDto.getUsername());
        user.setPassword(passwordEncoder.encode(createDto.getPassword()));
        user.setRole(Role.valueOf(createDto.getRole().toUpperCase()));
        user.setActive(true);

        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id)); // නිවැරදි HTTP 404 Error එක යවයි
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

        if (updateDto.getRole() != null) {
            user.setRole(Role.valueOf(updateDto.getRole().toUpperCase()));
        }

        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    // අලුත්: User කෙනෙක්ව Active හෝ Deactivate කිරීම
    @Override
    public UserDto changeUserStatus(Long id, boolean isActive) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setActive(isActive);
        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    // අලුත්: Password එක පමණක් වෙනස් කිරීම
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
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole().name());
        dto.setActive(user.isActive());
        return dto;
    }
}