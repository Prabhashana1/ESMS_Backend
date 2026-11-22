package lk.megasupply.esms_backend.service.impl;

import lk.megasupply.esms_backend.dto.AttendanceDto;
import lk.megasupply.esms_backend.dto.ManualAttendanceRequestDto;
import lk.megasupply.esms_backend.entity.Attendance;
import lk.megasupply.esms_backend.entity.User;
import lk.megasupply.esms_backend.enums.AttendanceStatus;
import lk.megasupply.esms_backend.exception.BadRequestException;
import lk.megasupply.esms_backend.exception.ResourceNotFoundException;
import lk.megasupply.esms_backend.repository.AttendanceRepository;
import lk.megasupply.esms_backend.repository.UserRepository;
import lk.megasupply.esms_backend.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    // Grace period limit: 08:45 AM
    private static final LocalTime LATE_THRESHOLD = LocalTime.of(8, 45);
    private static final double STANDARD_WORK_HOURS = 8.0;

    @Override
    @Transactional
    public AttendanceDto checkIn(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // දැනටමත් record එකක් තිබේදැයි පරීක්ෂා කිරීම
        Attendance attendance = attendanceRepository.findByUserIdAndDate(user.getId(), today)
                .orElse(null);

        if (attendance != null && attendance.getCheckInTime() != null) {
            throw new BadRequestException("You have already checked in today at " + attendance.getCheckInTime());
        }

        if (attendance == null) {
            attendance = new Attendance();
            attendance.setUser(user);
            attendance.setDate(today);
            attendance.setLocationValid(true);
        }

        attendance.setCheckInTime(now);

        // 08:45 ට පසු නම් LATE, නැතහොත් ON_TIME
        if (now.isAfter(LATE_THRESHOLD)) {
            attendance.setStatus(AttendanceStatus.LATE);
        } else {
            attendance.setStatus(AttendanceStatus.ON_TIME);
        }

        Attendance saved = attendanceRepository.save(attendance);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public AttendanceDto checkOut(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        Attendance attendance = attendanceRepository.findByUserIdAndDate(user.getId(), today)
                .orElseThrow(() -> new BadRequestException("No check-in record found for today. Please check in first."));

        if (attendance.getCheckInTime() == null) {
            throw new BadRequestException("You have not checked in today.");
        }

        if (attendance.getCheckOutTime() != null) {
            throw new BadRequestException("You have already checked out today at " + attendance.getCheckOutTime());
        }

        attendance.setCheckOutTime(now);

        // වැඩ කළ පැය ගණන සහ අතිකාල (OT) ගණනය
        long minutesWorked = Duration.between(attendance.getCheckInTime(), now).toMinutes();
        double hoursWorked = Math.round((minutesWorked / 60.0) * 100.0) / 100.0;
        attendance.setWorkingHours(hoursWorked);

        // පැය 4 ට අඩු නම් HALF_DAY
        if (hoursWorked < 4.0) {
            attendance.setStatus(AttendanceStatus.HALF_DAY);
        }

        // පැය 8 ට වැඩි නම් OT ගණනය කිරීම
        if (hoursWorked > STANDARD_WORK_HOURS) {
            double ot = Math.round((hoursWorked - STANDARD_WORK_HOURS) * 100.0) / 100.0;
            attendance.setOvertimeHours(ot);
        } else {
            attendance.setOvertimeHours(0.0);
        }

        Attendance saved = attendanceRepository.save(attendance);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceDto getTodayAttendance(String username) {
        Attendance attendance = attendanceRepository.findByUserUsernameAndDate(username, LocalDate.now())
                .orElse(null);
        return attendance != null ? mapToDto(attendance) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDto> getMyMonthlyAttendance(String username, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<Attendance> list = attendanceRepository.findByUserUsernameAndDateBetweenOrderByDateDesc(username, start, end);
        return mapListToDto(list);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDto> getDailyAttendance(LocalDate date) {
        List<Attendance> list = attendanceRepository.findByDate(date);
        return mapListToDto(list);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDto> getUserMonthlyAttendance(Long userId, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<Attendance> list = attendanceRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, start, end);
        return mapListToDto(list);
    }

    @Override
    @Transactional
    public AttendanceDto manualMark(ManualAttendanceRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));

        Attendance attendance = attendanceRepository.findByUserIdAndDate(dto.getUserId(), dto.getDate())
                .orElse(new Attendance());

        attendance.setUser(user);
        attendance.setDate(dto.getDate());
        attendance.setLocationValid(true);

        AttendanceStatus status;
        try {
            status = dto.getStatus() != null ? AttendanceStatus.valueOf(dto.getStatus().toUpperCase()) : AttendanceStatus.ON_TIME;
        } catch (Exception e) {
            throw new BadRequestException("Invalid status provided. Use ON_TIME, LATE, HALF_DAY, ABSENT, or ON_LEAVE.");
        }

        // ABSENT හෝ ON_LEAVE නම් වේලාවන් සහ පැය 0 වේ
        if (status == AttendanceStatus.ABSENT || status == AttendanceStatus.ON_LEAVE) {
            attendance.setCheckInTime(null);
            attendance.setCheckOutTime(null);
            attendance.setWorkingHours(0.0);
            attendance.setOvertimeHours(0.0);
            attendance.setStatus(status);
        } else {
            attendance.setCheckInTime(dto.getCheckInTime());
            attendance.setCheckOutTime(dto.getCheckOutTime());

            if (dto.getCheckInTime() != null && dto.getCheckOutTime() != null) {
                if (dto.getCheckOutTime().isBefore(dto.getCheckInTime())) {
                    throw new BadRequestException("Check-out time cannot be before check-in time.");
                }
                long minutes = Duration.between(dto.getCheckInTime(), dto.getCheckOutTime()).toMinutes();
                double hours = Math.round((minutes / 60.0) * 100.0) / 100.0;
                attendance.setWorkingHours(hours);

                if (hours > STANDARD_WORK_HOURS) {
                    attendance.setOvertimeHours(Math.round((hours - STANDARD_WORK_HOURS) * 100.0) / 100.0);
                } else {
                    attendance.setOvertimeHours(0.0);
                }
            } else {
                attendance.setWorkingHours(0.0);
                attendance.setOvertimeHours(0.0);
            }
            attendance.setStatus(status);
        }

        Attendance saved = attendanceRepository.save(attendance);
        return mapToDto(saved);
    }

    private List<AttendanceDto> mapListToDto(List<Attendance> list) {
        List<AttendanceDto> dtoList = new ArrayList<>();
        for (Attendance a : list) {
            dtoList.add(mapToDto(a));
        }
        return dtoList;
    }

    private AttendanceDto mapToDto(Attendance attendance) {
        return AttendanceDto.builder()
                .id(attendance.getId())
                .userId(attendance.getUser().getId())
                .fullName(attendance.getUser().getFullName())
                .username(attendance.getUser().getUsername())
                .designation(attendance.getUser().getDesignation() != null ? attendance.getUser().getDesignation().name() : "")
                .date(attendance.getDate())
                .checkInTime(attendance.getCheckInTime())
                .checkOutTime(attendance.getCheckOutTime())
                .workingHours(attendance.getWorkingHours())
                .overtimeHours(attendance.getOvertimeHours())
                .status(attendance.getStatus() != null ? attendance.getStatus().name() : "")
                .locationValid(attendance.isLocationValid())
                .build();
    }
}