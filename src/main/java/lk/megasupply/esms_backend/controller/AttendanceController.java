package lk.megasupply.esms_backend.controller;

import jakarta.validation.Valid;
import lk.megasupply.esms_backend.dto.AttendanceDto;
import lk.megasupply.esms_backend.dto.ManualAttendanceRequestDto;
import lk.megasupply.esms_backend.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // සේවකයා Check-In වීම
    @PostMapping("/check-in")
    @PreAuthorize("hasAnyRole('OWNER', 'EMPLOYEE')")
    public ResponseEntity<AttendanceDto> checkIn(Authentication authentication) {
        return ResponseEntity.ok(attendanceService.checkIn(authentication.getName()));
    }

    // සේවකයා Check-Out වීම
    @PostMapping("/check-out")
    @PreAuthorize("hasAnyRole('OWNER', 'EMPLOYEE')")
    public ResponseEntity<AttendanceDto> checkOut(Authentication authentication) {
        return ResponseEntity.ok(attendanceService.checkOut(authentication.getName()));
    }

    // අද දවසේ සේවකයාගේ attendance තත්ත්වය බැලීම
    @GetMapping("/today")
    @PreAuthorize("hasAnyRole('OWNER', 'EMPLOYEE')")
    public ResponseEntity<AttendanceDto> getTodayAttendance(Authentication authentication) {
        AttendanceDto dto = attendanceService.getTodayAttendance(authentication.getName());
        if (dto == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dto);
    }

    // සේවකයාගේ මාසික attendance ඉතිහාසය
    @GetMapping("/my-history")
    @PreAuthorize("hasAnyRole('OWNER', 'EMPLOYEE')")
    public ResponseEntity<List<AttendanceDto>> getMyHistory(
            Authentication authentication,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(attendanceService.getMyMonthlyAttendance(authentication.getName(), year, month));
    }

    // OWNER ONLY: නිශ්චිත දිනයක සියලුම සේවකයින්ගේ attendance
    @GetMapping("/daily")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<AttendanceDto>> getDailyAttendance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getDailyAttendance(date));
    }

    // OWNER ONLY: නිශ්චිත සේවකයෙකුගේ මාසික attendance ඉතිහාසය
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<AttendanceDto>> getUserHistory(
            @PathVariable Long userId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(attendanceService.getUserMonthlyAttendance(userId, year, month));
    }

    // OWNER ONLY: අතින් ලකුණු කිරීම (Manual Entry / Override)
    @PostMapping("/manual")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<AttendanceDto> manualMark(@Valid @RequestBody ManualAttendanceRequestDto dto) {
        return ResponseEntity.ok(attendanceService.manualMark(dto));
    }
}