package lk.megasupply.esms_backend.controller;

import lk.megasupply.esms_backend.dto.PasswordResetDto;
import lk.megasupply.esms_backend.dto.UserCreateDto;
import lk.megasupply.esms_backend.dto.UserDto;
import lk.megasupply.esms_backend.dto.UserUpdateDto;
import lk.megasupply.esms_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserCreateDto userCreateDto) {
        return new ResponseEntity<>(userService.createUser(userCreateDto), HttpStatus.CREATED); // 201 Created Status
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id)); // 200 OK
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserUpdateDto userUpdateDto) {
        return ResponseEntity.ok(userService.updateUser(id, userUpdateDto));
    }

    // Delete වෙනුවට - User Status එක Active හෝ Deactivate කිරීම
    // උදාහරණ: PATCH /api/v1/users/1/status?isActive=false
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserDto> changeUserStatus(@PathVariable Long id, @RequestParam boolean isActive) {
        return ResponseEntity.ok(userService.changeUserStatus(id, isActive));
    }

    // Password එක වෙනස් කිරීම සඳහා වෙනම Endpoint එකක්
    // උදාහරණ: PUT /api/v1/users/1/password
    @PutMapping("/{id}/password")
    public ResponseEntity<String> resetPassword(@PathVariable Long id, @RequestBody PasswordResetDto passwordResetDto) {
        userService.resetPassword(id, passwordResetDto);
        return ResponseEntity.ok("Password updated successfully");
    }
}