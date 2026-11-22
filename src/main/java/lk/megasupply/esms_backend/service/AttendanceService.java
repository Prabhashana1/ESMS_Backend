package lk.megasupply.esms_backend.service;

import lk.megasupply.esms_backend.dto.AttendanceDto;
import lk.megasupply.esms_backend.dto.ManualAttendanceRequestDto;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    // සේවකයා තමන්ගේ check-in / check-out සිදුකිරීම
    AttendanceDto checkIn(String username);

    AttendanceDto checkOut(String username);

    AttendanceDto getTodayAttendance(String username);

    List<AttendanceDto> getMyMonthlyAttendance(String username, int year, int month);

    // Owner ට පමණක් අදාළ methods
    List<AttendanceDto> getDailyAttendance(LocalDate date);

    List<AttendanceDto> getUserMonthlyAttendance(Long userId, int year, int month);

    AttendanceDto manualMark(ManualAttendanceRequestDto dto);
}