package lk.megasupply.esms_backend.repository;

import lk.megasupply.esms_backend.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByUserIdAndDate(Long userId, LocalDate date);

    Optional<Attendance> findByUserUsernameAndDate(String username, LocalDate date);

    // Owner ට දිනක සියලු දෙනාගේ attendance බැලීමට
    List<Attendance> findByDate(LocalDate date);

    // සේවකයෙකුගේ මාසික attendance ඉතිහාසය ලබාගැනීමට
    List<Attendance> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);

    List<Attendance> findByUserUsernameAndDateBetweenOrderByDateDesc(String username, LocalDate startDate, LocalDate endDate);

    boolean existsByUserIdAndDate(Long userId, LocalDate date);
}